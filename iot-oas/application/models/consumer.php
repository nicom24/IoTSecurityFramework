<?php

class consumer extends CI_Model {

	function __construct() {
		parent::__construct();
		$this->load->model('msql');
	}
	
	function getAllInfos($provider, $user_id){
		//Load consumer and token from provider and user_id
		$info_query = $this->db->query("SELECT * FROM consumer as c, token as t, user as u WHERE u.provider_id = '".$user_id."' AND u.provider = '".$provider."' AND c.c_id = t.c_id AND u.u_id = c.u_id");
		if ($info_query->num_rows() <= 0) return NULL;
		//Consumer exists
		$cons = $info_query->row();
		$data["consumer"]["key"] = $cons->consumer_key;
		$data["consumer"]["secret"] = $cons->consumer_secret;
		$data["token"]["token"] = $cons->token;
		$data["token"]["secret"] = $cons->token_secret;
		$data["token"]["type"] = $cons->type;
		$data['token']['expires'] = $cons->expiration_time;
		//Search for resources
		$data['resources'] = array();
		$res_query = $this->db->query("SELECT * FROM resource_access as ra, resource as r WHERE
			ra.resource_id=r.r_id AND ra.token_id='".$cons->t_id."'");	
		for ($ii = 0; $ii < $res_query->num_rows(); $ii++) {
				$it = $res_query->row($ii);
				$data['resources'][$ii]['uuid'] = $it->resource_uuid;
				$data['resources'][$ii]['producer'] = $it->resource_producer;
				$data['resources'][$ii]['model'] = $it->resource_model;
				$data['resources'][$ii]['name'] = $it->resource_name;
				$data['resources'][$ii]['type'] = $it->resource_type;
				$data['resources'][$ii]['actions'] = $it->actions;
		}
		return $data;
	}

	function getUser($provider, $user_id){
		$query = $this->db->query("SELECT * FROM user WHERE provider = '". $provider . "' AND provider_id = '". $user_id . "'");
		if($query->num_rows() <= 0 ) return NULL;		
		$row = $query->row();
		return $row;
	}

	function getConsumer($provider, $user_id){
		//Load consumer and token from provider and user_id
		$info_query = $this->db->query("SELECT * FROM consumer as c, user as u WHERE u.provider_id = '".$user_id."' AND u.provider = '".$provider."' AND  u.u_id = c.u_id");
		if ($info_query->num_rows() <= 0) return NULL;
		//Consumer exists
		$cons = $info_query->row();
		return $cons;
	}

	function createNewUserAuth($provider, $user_id, $username){
		//Check if user is already present
		if ($this->getUser($provider, $user_id)==NULL)		
			$new_user_id = $this->createNewUser($provider, $user_id, $username);
		else
			$new_user_id = $this->getUser($provider, $user_id)->u_id;	
		//Check if consumer is already present
		if ($this->getConsumer($provider, $user_id)==NULL)
			$new_consumer_id = $this->createNewConsumer($provider, $new_user_id, "Generic client");
		else
			$new_consumer_id = $this->getConsumer($provider, $user_id)->c_id;
		//Check if token is already present
		if ($this->getAllInfos($provider, $user_id)==NULL)
			$this->createNewToken($new_consumer_id);
		return $this->getAllInfos($provider, $user_id);
	}

	function createNewUser($provider, $user_id, $username){
		$new_user["provider_id"] = $user_id;
		$new_user["name"] = $username;
		$new_user["provider"] = $provider;
		return $this->msql->insert_user($new_user);
	}

	function createNewConsumer($provider, $user_id, $consumer_name){
		$new_cons["consumerKey"] = $this->generateKey(25);
		$new_cons["consumerSecret"] = $this->generateKey(25);
		$new_cons["consumerName"] = $consumer_name;
		$new_cons["u_id"] = $user_id;
		return $this->msql->insert_consumer($new_cons);
	}

	function createNewToken($c_id){
		$new_token["token"] = $this->generateKey(25);
		$new_token["tokenSecret"] = $this->generateKey(25);
		$new_token["tokenType"] = "AT";
		$new_token["tokenConsumer"] = $c_id;
		$this->msql->insert_token($new_token);
	}

	function generateKey($length){
		$result = "";
		$charset = "abcdefghijklmnopqrstuvwxyz-0123456789_ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		$charsetLength = strlen($charset);
		for ($ii=0; $ii<$length; $ii++){
			$result .= $charset[rand(0, $charsetLength -1)];
		}
		return $result;
	}

}
?>
