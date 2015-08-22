<?php

class authorizer extends CI_Model {

	function __construct() {
		parent::__construct();
		$this->load->model('msql');
		$this->load->model('mFetch');
	}

	/*function newDevice($token, $uuid){
		//Check if uuid exists
		$query = $this->db->query("SELECT * FROM resource WHERE resource_uuid='".$uuid."'");
		if ($query->num_rows() > 0){
			//Resource already exists, not authorized to register a new device
			return false;
		}else{
			//Check if token and consumer key are valid
			$ver_query = $this->db->query("SELECT * FROM token WHERE token='".$token."'");
			if ($ver_query->num_rows() <= 0) return false;
			//Resource is not present, insert device and resouce_access
			$resource_id = $this->msql->insert_resource($uuid);
			//Get token id
			$token_id = $this->getTokenId($token);
			//Insert new resource access for token
			$auth['resource'] = $resource_id;
			$auth['token']=$token_id;
			$auth['actions']=array('GET','POST','PUT','DELETE','AUTH');
			$this->msql->insert_new_resource_access($auth);
			return true;
		}	
	}*/
	
	function isAuthorized($consumer_key, $token, $device, $actions){
		//Check if resouce exists
		$uuid = $device->uuid;
		$query = $this->db->query("SELECT * FROM resource WHERE resource_uuid='".$uuid."'");
		if ($query->num_rows()<=0) return true;
		//Check if user has permission on an existing resource
		$data = $this->mFetch->fetchData($consumer_key, $token);
		if (is_null($data)) return false;
		for ($ii=0; $ii<count($data['resources']); $ii++){
			if (strcmp($data['resources'][$ii]['uuid'],$uuid)==0){
				//Check if desired actions are possible
				if ($this->checkActions($actions,$data['resources'][$ii]['actions'])){
					return true;
				}
 			}
		}
		return false;
		
	}
	
	function authorize($token, $device, $actions){	
		$auth['token'] = $this->getTokenId($token);
		//Check if is a new resource
		$query = $this->db->query("SELECT * FROM resource WHERE resource_uuid='".$device->uuid."'");
		if ($query->num_rows()<=0){
			//New resource
			$auth['resource'] = $this->msql->insert_resource($device->uuid,$device->producer,$device->model,$device->name,$device->type);
			$auth['action_string']= "GET,POST,PUT,DELETE,AUTH,OWN";
		}else{
			//Already existing resource
			$auth['resource'] = $this->getResourceId($device->uuid);
			$auth['action_string']= $actions;
		}	
		//Check if authorization is already inserted
		$query = $this->db->query("SELECT * FROM resource_access WHERE resource_id='".$auth['resource']."' AND token_id='".$auth['token']."'");
		if ($query->num_rows()<=0)
			$this->msql->insert_new_resource_access($auth);
	}

	function getTokenId($token){
		$query = $this->db->query("SELECT t_id FROM token WHERE token='".$token."'");
		if ($query->num_rows()<=0) return NULL;
		$token = $query->row()->t_id;
		return $token;
	}

	function getResourceId($uuid){
		$query = $this->db->query("SELECT r_id FROM resource WHERE resource_uuid='".$uuid."'");
		if ($query->num_rows()<=0) return NULL;
		$id = $query->row()->r_id;
		return $id;
	}

	function checkActions($desired, $available){
		if (strpos($available,'AUTH') === false) return false;
		if ((strpos($desired,'GET') !== false)&&(strpos($available,'GET') === false)) return false;
		if ((strpos($desired,'POST') !== false)&&(strpos($available,'POST') === false)) return false;
		if ((strpos($desired,'PUT') !== false)&&(strpos($available,'PUT') === false)) return false;
		if ((strpos($desired,'DELETE') !== false)&&(strpos($available,'DELETE') === false)) return false;
		if ((strpos($desired,'AUTH') !== false)&&(strpos($available,'AUTH') === false)) return false;
		return true;
	}


}
?>
