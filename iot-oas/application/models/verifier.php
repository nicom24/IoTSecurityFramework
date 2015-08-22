<?php

class verifier extends CI_Model {

	function __construct() {
		parent::__construct();
		$this->load->model('mFetch');
	}
	
	function verify($authHeader){
		//Check if header starts with OAuth		
		if (!$this->startsWith($authHeader,"OAuth")) return false;
		$authHeader = substr($authHeader, 6);
		$resp = "";	
		$authHeader = str_replace(",","&",$authHeader);
		$authHeader = str_replace('"',"",$authHeader);
		parse_str($authHeader);
		$data = $this->mFetch->fetchData($oauth_consumer_key,$oauth_token);
		if (is_null($data))return false;
		
		//$oauth_consumer_key
		//$oauth_nonce
		//$oauth_signature
		//$oauth_signature_method
		//$oauth_timestamp
		//$oauth_token
		//$oauth_version
		return true;
	}

	function startsWith($haystack, $needle) {
    		// search backwards starting from haystack length characters from the end
    		return $needle === "" || strrpos($haystack, $needle, -strlen($haystack)) !== FALSE;
	}
}
?>
