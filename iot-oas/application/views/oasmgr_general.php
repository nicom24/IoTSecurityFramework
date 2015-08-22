<h1>Main Panel - Administration</h1>

<?php
	$infoText = '';
	if ($this->session->flashdata('info') != FALSE) {
		switch($this->session->flashdata('info')) {
			case 'user': $infoText = 'New user inserted, with ID: '.$this->session->flashdata('infoID'); break;
			case 'resourceURI': $infoText = 'New Resource URI inserted, with ID: '.$this->session->flashdata('infoID'); break;
			case 'consumer': $infoText = 'New Consumer inserted, with ID: '.$this->session->flashdata('infoID'); break;
			case 'token': $infoText = 'New Token inserted, with ID: '.$this->session->flashdata('infoID'); break;
			case 'resource_access': $infoText = 'New Resource Access policy inserted, with ID: '.$this->session->flashdata('infoID'); break;
		}
	}
?>

<br>
<div style="font-size: 20px; font-weight: bold; color: red;"><?php echo $infoText; ?></div>
<br>
<b>VIEW actions:</b>
<ul>
	<li><a href="./view_consumer">View all <b>Consumers</b> (Clients)</a></li>
	<li><a href="./view_token">View all <b>Tokens</b></a></li>
	<li><a href="./view_user">View all <b>Users</b> (Resources Owners)</a></li>
	<li><a href="./view_resource">View all <b>Resources URIs</b></a></li>
</ul>
<br>
<b>INSERT actions:</b>
<ul>
	<li><a href="./insert_consumer">Insert new <b>Consumer</b> (Client)</a></li>
	<li><a href="./insert_token">Insert new <b>Token</b></a></li>
	<li><a href="./insert_resource_access">Insert new <b>Resource Access</b> policy</a></li>
	<li><a href="./insert_user">Insert new <b>User</b> (Resource Owner)</a></li>
	<li><a href="./insert_resource">Insert new <b>Resource URI</b></a></li>
</ul>