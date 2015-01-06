<?php

// we always return JSON (unless there is an error)
header('Content-Type: application/json');

class User {
  public $username;
  public $passwordHash;

  public function __construct ($username, $password) {
    $this->username = $username;
    $this->passwordHash = sha1($password);
  }

  public function getAuthenticationHash() {
    return sha1($this->username . ':' . $this->passwordHash);
  }
}


// loads a user with given name (return null for unknown)
function loadUser ($username) {
  // TODO(BH): currently, dummy implementation (no disk access)
  $users = array('martin' => 'secret', 'benjamin' => 'test');

  if (!array_key_exists($username, $users)) {
    return null;
  }

  return new User ($username, $users[$username]);
}

// returns error message to caller and exits
function error($message) {
  http_response_code(500);
  exit ($message);
}

// returns the user for the currently set auth token or null if the
// token is not set or is invalid.
function getUserFromCookie () {
  $token = $_COOKIE['RapidClubAdminSession'];
  if (is_null ($token)) {
    return null;
  }
  list($username, $authHash) = explode (':', $token, 2);
  $user = loadUser ($username);
  if (!is_null ($user) && $user->getAuthenticationHash() == $authHash) {
    return $user;
  }
  return null;
}

// returns information on the currently logged in user 
// (or an empty dummy user if no user is logged in)
function getUser () {
  $user = getUserFromCookie();
  if (is_null ($user)) {
    echo '{}';
  } else {
    echo json_encode ($user);
  }
}

function logout () {
  setcookie('RapidClubAdminSession', '', time()-3600);
}

function login () {
  $username = $_POST['username'];
  $password = $_POST['password'];
  $passwordHash = sha1($password);
  $authHash = sha1($username . ':' . $passwordHash);

  $user = loadUser ($username);
  if (!is_null($user) && $user->getAuthenticationHash() == $authHash) {
    setcookie('RapidClubAdminSession', $username . ':' . $authHash);
    error_log (json_encode ($user));
    echo json_encode ($user);
  } else {
    logout();
    echo '{}';
  }
}

switch($_GET['action']) {
case 'getuser': 
  getUser ();
  break;
case 'login':
  login();
  break;
case 'logout':
  logout();
  break;
default: 
  error('Unsupported action: ' . $_GET['action']);
}


?>