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

	/// Constructs a user from an XML element
	public function __construct($userElement) {
		$this->username = (string) $userElement['id'];
		$this->passwordHash = (string) $userElement['pwd'];
		$this->firstname = (string) $userElement['firstname'];
		$this->lastname = (string) $userElement['lastname'];
		$this->role = (string) $userElement['roles'];
		$this->departments = explode(',', (string) $userElement['departments']);
	}

	public function getAuthenticationHash() {
		return sha1($this->username . ':' . $this->passwordHash);
	}
	
	public function matchesPassword ($password) {
		error_log("PW : $password");
		error_log("Expected Hash : $this->passwordHash");
		error_log("Actual Hash : " . base64_encode(sha1($password, true)));
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
	foreach($xml->user as $user) {
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
	default :
		error('Unsupported action: ' . $_GET['action']);
}
?>