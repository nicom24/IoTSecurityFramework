<?php
	if (!defined('BASEPATH'))
		exit('No direct script access allowed');

class mLogin extends CI_Model {
	
    function __construct(){
        parent::__construct();
    }
    
    public function check_credentials($u, $p) {
        $this->db->where('username', $u);
        $this->db->where('password', sha1($p));
        $query = $this->db->get('logon_user');
        
        if ($query->num_rows > 0) {
            $row = $query->row();
            $session_data = array (
            		'logged_in' => true,
                    'uname' => $row->username,
                    'name' => $row->name,
                    'surname' => $row->surname
            		);
            $this->session->set_userdata($session_data);
            return true;
        }
        return false;
    }
}