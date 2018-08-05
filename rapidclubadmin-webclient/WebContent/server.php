<?php


// we always return JSON (unless there is an error)
header('Content-Type: application/json');

class User {
	public $username;
	private $passwordHash;
	public $firstname;
	public $lastname;
	public $role;
	public $departments;
	public $email;

	/// Constructs a user from an XML element
	public function __construct($userElement) {
		$this->username = (string) $userElement['id'];
		$this->passwordHash = (string) $userElement['pwd'];
		$this->firstname = (string) $userElement['firstname'];
		$this->lastname = (string) $userElement['lastname'];
		$this->email = (string) $userElement['email'];
		$this->role = (string) $userElement['roles'];
		$this->departments = explode(',', (string) $userElement['departments']);
	}

	public function getAuthenticationHash() {
		return sha1($this->username . ':' . $this->passwordHash);
	}

	public function matchesPassword($password) {
		return $this->passwordHash == base64_encode(sha1($password, true));
	}
}

// returns error message to caller and exits
function error($message) {
	http_response_code(500);
	exit ($message);
}

// loads a user with given name (return null for unknown)
function loadUser($username) {
	$masterdata = file_get_contents("data/masterdata.xml");
	if ($masterdata === FALSE) {
		error('Could not load masterdata.xml!');
	}

	$xml = new SimpleXMLElement($masterdata);
	foreach ($xml->user as $user) {
		if ($user['id'] == $username) {
			return new User($user);
		}
	}

	return null;
}

// returns the user for the currently set auth token or null if the
// token is not set or is invalid.
function getUserFromCookie() {
	$token = $_COOKIE['RapidClubAdminSession'];
	if (is_null($token)) {
		return null;
	}
	list ($username, $authHash) = explode(':', $token, 2);
	$user = loadUser($username);
	if (!is_null($user) && $user->getAuthenticationHash() == $authHash) {
		return $user;
	}
	return null;
}

// returns information on the currently logged in user 
// (or an empty dummy user if no user is logged in)
function getUser() {
	$user = getUserFromCookie();
	if (is_null($user)) {
		echo '{}';
	} else {
		echo json_encode($user);
	}
}
function findSubnode($elem, $name, $value) {
	foreach ($elem->childNodes as $subnode) {
		if ($subnode->nodeName === $name and $subnode->nodeValue === $value) {
			return $subnode;
		}
	}
	return NULL;
}

function changeAttributesIntoElements($dom, $elem) {
	// replace all attributes with elements
	if ($elem->hasAttributes()) {
		foreach ($elem->attributes as $attr) {
			$elem->appendChild($dom->createElement($attr->name, $attr->value));
		}
		while ($elem->attributes->length > 0) {
			$elem->removeAttributeNode($elem->attributes->item(0));
		}
	}

	// remove all empty "notes" elements
	$nodeToDelete = findSubnode($elem, "notes", "");
	while (!is_null($nodeToDelete)) {
		$elem->removeChild($nodeToDelete);
		$nodeToDelete = findSubnode($elem, "notes", "");
	}
	// recurse over child nodes
	foreach ($elem->childNodes as $subnode) {
		if ($subnode->nodeType == XML_ELEMENT_NODE) {
			changeAttributesIntoElements($dom, $subnode);
		}
	}
}

function getDepartmentFile() {
	$department = $_GET['department'];
	$file = 'data/current/' . $department . '/trainingslist.xml';
	if (!file_exists($file)) {
		error("Could not find file for department $department");
	}
	return $file;
}

function getList() {
	$file = getDepartmentFile();

	// TODO: Permission check

	$dom = new DOMDocument();
	$dom->load($file);
	changeAttributesIntoElements($dom, $dom);
	echo json_encode(simplexml_load_string($dom->saveXML()));
}

function updateTraining() {
	$department = $_GET['department'];
	$request_body = file_get_contents('php://input');
	$data = json_decode($request_body);

	$file = getDepartmentFile();
	$dom = new DOMDocument();
	$dom->load($file);
	$xpath = new DOMXpath($dom);

	// search for matching training
	$updatedTraining = null;
	foreach ($dom->getElementsByTagName('training') as $training) {
		if ($training->getAttribute('id') == $data->id) {
			$updatedTraining = $training;
			break;
		}
	}

	if ($updatedTraining == null) {
		error('No matching training found!');
		return;
	}

	// TODO: check permission
	$user = getUserFromCookie();

	$updatedTraining->setAttribute('state', $data->state);
	$updatedTraining->setAttribute('checkedByUser', $user->username);
	$updatedTraining->setAttribute('checkedDate', date('YmdHi'));
	if (property_exists($data, 'partipiciantscount')) {
		$updatedTraining->setAttribute('partipiciantscount', $data->partipiciantscount);
	}
	if (property_exists($data, 'notes')) {
		$updatedTraining->setAttribute('notes', $data->notes);
	}

	// Update trainers

	// 1.) remove all trainers from training
	$discardIds = array ();
	while ($updatedTraining->getElementsByTagName('heldbytrainer')->length > 0) {
		$held = $updatedTraining->getElementsByTagName('heldbytrainer')->item(0);
		$discardIds[$held->getAttribute('id')] = 1;
		$updatedTraining->removeChild($held);		
	}

	// 2.) update ids of trainers
	$maxId = 0;
	foreach ($dom->getElementsByTagName('trainer') as $trainer) {
		if ($trainer->hasAttribute('trainingsheld')) {
			$heldIds = split(',', $trainer->getAttribute('trainingsheld'));
			$heldIds = array_filter($heldIds, function ($value) use ($discardIds) {
				return !array_key_exists($value, $discardIds);
			});
			$trainer->setAttribute('trainingsheld', join(',', $heldIds));
			foreach ($heldIds as $id) {
				$maxId = max($maxId, (int) $id);
			}
		}
	}

	// 3.) integrate new trainings
	foreach ($data->heldbytrainer as $heldby) {
		$maxId += 1;
		$heldByNode = $dom->createElement('heldbytrainer');
		$heldByNode->setAttribute('id', $maxId);
		// TODO: might want to protect against invalid roles?
		$heldByNode->setAttribute('role', $heldby->role);
		$heldByNode->setAttribute('trainer', $heldby->trainer);
		$updatedTraining->appendChild($heldByNode);

		$matchingTrainers = $xpath->query("//trainer[@id='$heldby->trainer']");
		if (is_null($matchingTrainers) || $matchingTrainers->length != 1) {
			error("Missing or invalid trainer with id $heldby->trainer");
			return;
		}

		$trainer = $matchingTrainers->item(0);
		if ($trainer->hasAttribute('trainingsheld')) {
			$trainer->setAttribute('trainingsheld', $trainer->getAttribute('trainingsheld') . ',' . $maxId);
		} else {
			$trainer->setAttribute('trainingsheld', $maxId);
		}
	}

	// ensure current user is in DOM
	$userElements = $xpath->query("//user[@id='$user->username']");
	if (is_null($userElements) || $userElements->length == 0) {
		$userElement = $dom->createElement('user');
		$userElement->setAttribute('id', $user->username);
		$userElement->setAttribute('accountname', $user->username);
		$userElement->setAttribute('lastname', $user->lastname);
		$userElement->setAttribute('firstname', $user->firstname);
		$userElement->setAttribute('email', $user->email);
		$dom->documentElement->appendChild($userElement);
	}

	$dom->save($file);
}

function logout() {
	setcookie('RapidClubAdminSession', '', time() - 3600);
}

function login() {
	$username = $_POST['username'];
	$password = $_POST['password'];

	$user = loadUser($username);
	if (!is_null($user) && $user->matchesPassword($password)) {
		setcookie('RapidClubAdminSession', $username . ':' . $user->getAuthenticationHash());
		echo json_encode($user);
	} else {
		logout();
		echo '{}';
	}
}

switch ($_GET['action']) {
	case 'getuser' :
		getUser();
		break;
	case 'login' :
		login();
		break;
	case 'logout' :
		logout();
		break;
	case 'getlist' :
		getList();
		break;
	case 'updatetraining' :
		updateTraining();
		break;
	default :
		error('Unsupported action: ' . $_GET['action']);
}
?>