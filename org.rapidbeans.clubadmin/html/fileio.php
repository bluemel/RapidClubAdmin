<?php

ob_start();

$password = "musashi09";
$valid_files = array ("file1", "file2", "file3");

if ($_GET["password"] !== $password) {
	header('HTTP/1.1 401 Unauthorized');	
	echo("Invalid password!");
	exit;
}

$file = $_GET["file"];
if (strpos($file, "/") === 0 || strpos($file, "..") !== FALSE)  {
	header('HTTP/1.1 403 Forbidden');	
	exit;
}

$data_root = "data/";

$op = $_GET["op"];

if ($op === "read") {
	header("Content-Disposition: attachment; filename=" . $file . ";");
	readfile($data_root . $file);
} elseif ($op === "write") {
	$contents = $_POST["contents"];
	if (get_magic_quotes_gpc()) {
		$contents = stripslashes($contents);
	}
	$handle = fopen ($data_root . $file, "w");
	if ($handle === FALSE) {
		header('HTTP/1.1 405 Method not allowed');
	} else {
	 	fwrite($handle, $contents);
		fclose($handle);
		echo "success";
	}
} elseif ($op === "list") {
	$folder = dir($file); 
	while ($folderEntry = $folder->read()) { 
		echo $folderEntry . "\n";
	}
	$folder->close();
} elseif ($op === "delete") {
	if (unlink($data_root . $file)) {
		echo "success";
	} else {
		echo "failure \"" . $file . "\"";
	}
} elseif ($op === "exists") {
	if (is_readable($data_root . $file)) {
		echo "success";
	} else {
		echo "failure \"" . $file . "\"";
	}
} elseif ($op === "mkdirs") {
	if (mkdir($data_root . $file)) {
		echo "success";
	} else {
		echo "failure \"" . $file . "\"";
	}
} elseif ($op === "rmdir") {
	if (rmdir($data_root . $file)) {
		echo "success";
	} else {
		echo "failure \"" . $file . "\"";
	}
} elseif ($op === "sendmail") {
    $msgargs = explode('|', deobfcte($file));
	$to      = $msgargs[0];
	$subject = $msgargs[1];
	$headers = "From: " . $msgargs[2];
	$message = $msgargs[3];
	$result = mail($to, $subject, $message, $headers);
	echo "Mail result is " . $result;
} elseif ($op === "readj") {
	$dom = new DOMDocument();
	$dom->loadXML(file_get_contents($data_root . $file));
	changeAttributesIntoElements($dom, $dom);
	$sxml = simplexml_load_string($dom->saveXML());
	echo json_encode($sxml);
} else {
	header('HTTP/1.1 405 Method not allowed');
}

function deobfcte($str) {
    $ret = '';
    for ($i = 0; $i < strlen($str); $i++) {
        $ret = $ret . chr(ord($str{$i}) + (($i + 3) % 4));
    }
    return $ret;
}

function changeAttributesIntoElements($dom, $elem) {
	foreach ($elem->attributes as $attr) {
		$elem->appendChild($dom->createElement($attr->name, $attr->value));
	}
	while ($elem->attributes->length > 0) {
		$elem->removeAttributeNode($elem->attributes->item(0));
	}
	foreach ($elem->childNodes as $subnode) {
		changeAttributesIntoElements($dom, $subnode);
	}
}

?>
