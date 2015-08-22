<?php

class authorizer extends CI_Model {

	function __construct() {
		parent::__construct();
		$this->load->model('msql');
	}

	function newDevice($token, $uuid){
		//Check if uuid exists
		$query = $this->db->query("SELECT r_id FROM resource WHERE resource_uuid=".$uuid);
		if ($query->num_rows()>0){
			//Resource already exists, not authorized to register a new device
			return NULL;
		}else{
			//Resource is not present, insert device and resouce_access
			//$resource_id = $this->msql->insert_resource($uuid);
			//Get token id
			$query = $this->db->query("SELECT t_id FROM token WHERE token=".$token);
			if ($query->num_rows()<=0) return NULL;
			$token = $query->row()->t_id;
			return $token;
			//Insert new resource access for token
			$auth['resource_id'] = $resource_id;
		}	
	}	

}
?>
