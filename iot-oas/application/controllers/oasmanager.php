<?php
if(!defined('BASEPATH'))
	exit('No direct script access allowed');

class OasManager extends CI_Controller {

	/**
	 * Index Page for this controller.
	 *
	 * Maps to the following URL
	 * 		http://example.com/index.php/welcome
	 *	- or -
	 * 		http://example.com/index.php/welcome/index
	 *	- or -
	 * Since this controller is set as the default controller in
	 * config/routes.php, it's displayed at http://example.com/
	 *
	 * So any other public methods not prefixed with an underscore will
	 * map to /index.php/welcome/<method_name>
	 * @see http://codeigniter.com/user_guide/general/urls.html
	 */
	 
	function __construct() {
		parent::__construct();
		$this->load->model('mSql');
		$this->load->helper(array('form', 'url'));
		$this->load->library('form_validation');
		$this->load->library('session');
		
		$this->isLoggedIn();
	}
	
	// Pages
	 
	public function index() {
		redirect('oasmanager/view_general');
	}
	
	public function view_general($i = NULL, $ii = NULL) {
		$data['page'] = __FUNCTION__;
		$data['info'] = $i;
		$data['infoID'] = $ii;
		$this->load->view('_header', $data);
		$this->load->view('oasmgr_general');
		$this->load->view('_footer');
	}
	
	public function insert_resource($error = NULL) {
		$data['page'] = __FUNCTION__;
		$this->load->view('_header', $data);
		$this->load->view('oasmgr_insert_resource');
		$this->load->view('_footer');
	}
	
	public function insert_user() {
		$data['page'] = __FUNCTION__;
		$this->load->view('_header', $data);
		$this->load->view('oasmgr_insert_user');
		$this->load->view('_footer');
	}
	
	public function insert_consumer() {
		$data['page'] = __FUNCTION__;
		$this->load->view('_header', $data);
		$this->load->view('oasmgr_insert_consumer');
		$this->load->view('_footer');
	}
	
	public function insert_token() {
		$data['page'] = __FUNCTION__;

		$data['users'] = $this->_get_all_users();
		$data['consumers'] = $this->_get_all_consumers();
		
		$this->load->view('_header', $data);
		$this->load->view('oasmgr_insert_token', $data);
		$this->load->view('_footer');
	}
	
	public function insert_resource_access() {
		$data['page'] = __FUNCTION__;
		
		$data['resources'] = $this->_get_all_resources();
		$data['tokens'] = $this->_get_all_tokens();
		
		$this->load->view('_header', $data);
		$this->load->view('oasmgr_insert_resource_access', $data);
		$this->load->view('_footer');		
	}
	
	function view_consumer() {
		$data['page'] = __FUNCTION__;
		
		$data['consumers'] = $this->_get_all_consumers();
		
		$this->load->view('_header', $data);
		$this->load->view('oasmgr_view_consumer', $data);
		$this->load->view('_footer');
	}
	
	function view_token() {
		$data['page'] = __FUNCTION__;
	
		//$data['tokens'] = $this->_get_all_tokens();
		$data['tokens'] = $this->_get_all_tokens_fetched();
	
		$this->load->view('_header', $data);
		$this->load->view('oasmgr_view_token', $data);
		$this->load->view('_footer');
	}
	
	/*
	function view_resource_access() {
		$data['page'] = __FUNCTION__;
	
		$data['resources_access'] = $this->_get_all_resources_access();
	
		$this->load->view('_header', $data);
		$this->load->view('oasmgr_view_resource_access', $data);
		$this->load->view('_footer');
	}
	*/
	
	function view_user() {
		$data['page'] = __FUNCTION__;
	
		$data['users'] = $this->_get_all_users();
	
		$this->load->view('_header', $data);
		$this->load->view('oasmgr_view_user', $data);
		$this->load->view('_footer');
	}
	
	function view_resource() {
		$data['page'] = __FUNCTION__;
	
		$data['resources'] = $this->_get_all_resources();
	
		$this->load->view('_header', $data);
		$this->load->view('oasmgr_view_resource', $data);
		$this->load->view('_footer');
	}
	
	// Functions
	
	public function f_insert_resource() {
		$this->form_validation->set_rules('resourceURI', 'Resource URI', 'trim|required|xss_clean|is_unique[resource.resource_uri]');
		
		if ($this->form_validation->run() == FALSE)
		{
			$this->insert_resource();
		}
		else
		{
			$val = $this->input->post('resourceURI');
			$new_id = $this->_insert_new_resource($val);
			$this->session->set_flashdata('info', 'resourceURI');
			$this->session->set_flashdata('infoID', $new_id);
			redirect('oasmanager/view_general');
		}
	}
	
	public function f_insert_user() {
		$this->form_validation->set_rules('user', 'User', 'trim|required|xss_clean|is_unique[user.name]');
	
		if ($this->form_validation->run() == FALSE)
		{
			$this->insert_user();
		}
		else
		{
			$val = $this->input->post('user');
			$new_id = $this->_insert_new_user($val);
			$this->session->set_flashdata('info', 'user');
			$this->session->set_flashdata('infoID', $new_id);
			redirect('oasmanager/view_general');
		}
	}
	
	public function f_insert_consumer() {
		$this->form_validation->set_rules('consumerKey', 'Consumer KEY', 'trim|required|xss_clean');
		$this->form_validation->set_rules('consumerSecret', 'Consumer Secret', 'trim|required|xss_clean');
		$this->form_validation->set_rules('consumerName', 'Consumer Name', 'trim|required|xss_clean|is_unique[consumer.name]');
	
		if ($this->form_validation->run() == FALSE)
		{
			$this->insert_consumer();
		}
		else
		{
			$input = $this->input->post();
			$new_id = $this->_insert_new_consumer($input);
			$this->session->set_flashdata('info', 'consumer');
			$this->session->set_flashdata('infoID', $new_id);
			redirect('oasmanager/view_general');
		}
	}
	
	public function f_insert_token() {
		$this->form_validation->set_rules('token', 'Token', 'trim|required');
		$this->form_validation->set_rules('tokenSecret', 'Token Secret', 'trim|required');
	
		if ($this->form_validation->run() == FALSE)
		{
			$this->insert_token();
		}
		else
		{
			$input = $this->input->post();
			$new_id = $this->_insert_new_token($input);
			$this->session->set_flashdata('info', 'token');
			$this->session->set_flashdata('infoID', $new_id);
			redirect('oasmanager/view_general');
		}
	}
	
	public function f_insert_resource_access() {
		$this->form_validation->set_rules('actions[]', 'Actions', 'required');
	
		if ($this->form_validation->run() == FALSE)
		{
			$this->insert_resource_access();
		}
		else
		{
			$input = $this->input->post();
			$new_id = $this->_insert_new_resource_access($input);
			$this->session->set_flashdata('info', 'resource_access');
			$this->session->set_flashdata('infoID', $new_id);
			redirect('oasmanager/view_general');
		}
	}
	
	public function f_view_token($id) {
		$result = NULL;
		$output = '';
		$method = $_SERVER['REQUEST_METHOD'];
		if ($this->_equals($method,'GET')) {
			if ($id != NULL) {
				$result = $this->_get_all_resource_access_by_token_id($id);
			}
			else {
				$result = NULL;
			}
		}
		if(isset($_GET['callback'])){
			$output =  $_GET['callback'] . '(' . json_encode($result) . ')';
		}
		else{
			$output = json_encode($result);
		}
		$this->output->set_content_type('application/json');
		$this->output->set_output($output);
	}
	
	// Private functions
	
	private function _equals($str1,$str2){
		return (strcmp($str1,$str2) == 0);
	}
	
	private function _get_all_resources() {
		return $this->mSql->get_all_resources();
	}
	
	private function _get_all_users() {
		return $this->mSql->get_all_users();
	}
	
	private function _get_all_consumers() {
		return $this->mSql->get_all_consumers();
	}
	
	private function _get_all_tokens() {
		return $this->mSql->get_all_tokens();
	}
	
	private function _get_all_tokens_fetched() {
		return $this->mSql->get_all_tokens_fetched();
	}
	
	private function _get_all_resources_access() {
		return $this->mSql->get_all_resources_access();
	}
	
	private function _get_all_resource_access_by_token_id($id) {
		return $this->mSql->get_all_resource_access_by_token_id($id);
	}
	
	private function _insert_new_resource($res) {
		return $this->mSql->insert_resource($res);
	}
	
	private function _insert_new_user($u) {
		return $this->mSql->insert_user($u);
	}

	private function _insert_new_consumer($in) {
		return $this->mSql->insert_consumer($in);
	}
	
	private function _insert_new_token($in) {
		return $this->mSql->insert_token($in);
	}
	
	private function _insert_new_resource_access($in) {
		return $this->mSql->insert_new_resource_access($in);
	}
	
	private function isLoggedIn() {
		if (!$this->session->userdata('logged_in'))
			redirect('oaslogon');
	}
}

/* End of file oas.php */
/* Location: ./application/controllers/oasmanager.php */