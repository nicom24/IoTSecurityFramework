<?php

class provider extends CI_Model {

	function __construct() {
		parent::__construct();
	}

	function getRedirectHeader($provider){
		if (strcmp($provider,"facebook")==0){
			$oauth_provider = new facebookProvider();
		}else if(strcmp($provider,"google")==0){
			$oauth_provider = new googleProvider();
		}else{
			return "http://www.unipr.it";
		}
		if ($oauth_provider!=null) return $oauth_provider->redirectUrl();
	}		

	function getUserInfos($provider,$validation_code){
		if (strcmp($provider,"facebook")==0){
			$oauth_provider = new facebookProvider();
		}else if(strcmp($provider,"google")==0){
			$oauth_provider = new googleProvider();
		}else{
			return "http://www.unipr.it";
		}
		if ($oauth_provider!=null) return $oauth_provider->getUserInfos($validation_code);
	}

}

class googleProvider extends oauth2provider{
		
	const CLIENT_ID = "1004170036705-hgn287ipt8631d46a7mnio33gc4ro8jl.apps.googleusercontent.com";
	const CLIENT_SECRET = "JyL5O0pGgz6ruhAF5CQ_eFNV";

	public function redirectUrl(){
		return "https://accounts.google.com/o/oauth2/auth?client_id=".self::CLIENT_ID."&redirect_uri=".oauth2provider::OAS_REDIRECT."google/&response_type=code&scope=email%20profile";
	}

	public function validationUrl($validation_code){
		return "https://www.googleapis.com/oauth2/v3/token";
	}

	public function getUserInfos($validation_code){
		//Authenticate for an access token
		$arguments = array( 'code' => $validation_code, 'client_id' => self::CLIENT_ID, 
			'client_secret' => self::CLIENT_SECRET, 'redirect_uri' => oauth2provider::OAS_REDIRECT."google/",
			'grant_type' => "authorization_code");
		$token_json = oauth2provider::curl_post($this->validationUrl($validation_code),"application/x-www-form-urlencoded",$arguments);	
		$token = json_decode($token_json);
		$access_token = $token->access_token;
		//Get user infos
		$user_json = oauth2provider::curl_get("https://www.googleapis.com/plus/v1/people/me?access_token=".$access_token, "google");
		$user_data = json_decode($user_json);
		$data["id"] = $user_data->id;
		$data["name"] = $user_data->displayName;
		return $data;
	}

}

class facebookProvider extends oauth2provider{

	const CLIENT_ID = "100111563656984";
	const CLIENT_SECRET = "d6cfaeff9f0098697419e5a1aaba81af";

	public function redirectUrl(){
		return "https://www.facebook.com/dialog/oauth?client_id=".self::CLIENT_ID."&redirect_uri=".oauth2provider::OAS_REDIRECT."facebook/";
	}

	public function validationUrl($validation_code){
		return "https://graph.facebook.com/v2.3/oauth/access_token?client_id=".self::CLIENT_ID."&redirect_uri=".oauth2provider::OAS_REDIRECT.
			"facebook/&client_secret=".self::CLIENT_SECRET."&code=".$validation_code;
	}

	public function getUserInfos($validation_code){
		//Authenticate for an access token
		$token_json = oauth2provider::curl_get($this->validationUrl($validation_code),"facebook");
		$token = json_decode($token_json);
		$access_token = $token->access_token;
		//Get user info
		$user_json = oauth2provider::curl_get("https://graph.facebook.com/me?access_token=".$access_token,"facebook");
		$user_data = json_decode($user_json);
		$data["id"] = $user_data->id;
		$data["name"] = $user_data->name;
		return $data;
	}

}

abstract class oauth2provider{
	
	const OAS_REDIRECT = "http://www.nicom.altervista.org/iot/iot-oas/index.php/oas/oauth2callback/";

	public abstract function redirectUrl();

	public abstract function validationUrl($validation_code);

	public abstract function getUserInfos($validation_code);

	public function curl_get($url, $provider){
		$curl = curl_init();
		curl_setOpt($curl, CURLOPT_RETURNTRANSFER, 1);
		curl_setopt($curl, CURLOPT_SSL_VERIFYPEER, true);
		curl_setopt($curl, CURLOPT_SSL_VERIFYHOST, 2);
		if (strcmp($provider,"google")==0)
			curl_setopt($curl, CURLOPT_CAPATH, getcwd() . "/application/certs/");
		else
			curl_setopt($curl, CURLOPT_CAINFO, getcwd() . "/application/certs/DigiCertHighAssuranceEVRootCA.crt");
		curl_setOpt($curl, CURLOPT_URL, $url);
		$result = curl_exec($curl);
		curl_close($curl);
		return $result;
	}

	public function curl_post($url, $content_type,$contents_array){
		$contents = http_build_query($contents_array);
		$curl = curl_init();
		curl_setOpt($curl, CURLOPT_RETURNTRANSFER, 1);
		curl_setopt($curl, CURLOPT_SSL_VERIFYPEER, true);
		curl_setopt($curl, CURLOPT_SSL_VERIFYHOST, 2);
		curl_setopt($curl, CURLOPT_CAPATH, getcwd() . "/application/certs/");
		curl_setopt($curl, CURLOPT_URL, $url);
		curl_setopt($curl, CURLOPT_POST, 1);
		curl_setopt($curl, CURLOPT_POSTFIELDS,$contents);
		$result = curl_exec($curl);
		curl_close($curl);
		return $result;
	}

}

?>
