<?php
if(!defined('BASEPATH'))
	exit('No direct script access allowed');

class OasLogon extends CI_Controller {
	
	function __construct() {
		parent::__construct();
		$this->load->model('mLogin');
		$this->load->helper(array('form', 'url'));
		$this->load->library('form_validation');
		$this->load->library('session');
	}
	
	public function index() {
		redirect('oaslogon/login');
	}
	
	public function login($err = NULL) {
		if ($this->session->userdata('logged_in'))
			redirect('oasmanager');
		$data['page'] = __FUNCTION__;
    	$this->load->view('_header', $data);
    	$this->load->view('oaslogon_login');
    	//$this->load->view('_footer');
	}

	public function validate() {		
		$u = $this->security->xss_clean($this->input->post('username'));
		$p = $this->security->xss_clean($this->input->post('password'));
		
		$exist = $this->_check_credentials($u, $p);
		if(!$exist) {
			$err_m = 'Invalid USERNAME or PASSWORD!';
			$this->session->set_flashdata('err_msg', $err_m);
			redirect('oaslogon/login');
		}
		else {
			redirect('oasmanager');
		}
	}
	
	private function _check_credentials($u, $p) {
		return $this->mLogin->check_credentials($u, $p);
	}
	
	public function logout() {
		$this->session->sess_destroy();
		redirect('oaslogon');
	}
}

/* End of file welcome.php */
/* Location: ./application/controllers/oaslogon.php */
