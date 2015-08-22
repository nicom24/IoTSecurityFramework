<!DOCTYPE html>
<html lang="en">
	<head>
		<meta charset="utf-8">
		<title>OAuth HTTP Proxy</title>
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<meta name="description" content="">
		<meta name="author" content="">

		<!-- Le styles -->
		<link href="<?= base_url(); ?>css/bootstrap.min.css" rel="stylesheet"/>		
		<style>
			body {
				padding-top: 60px; /* 60px to make the container go all the way to the bottom of the topbar */
			}
			.well hr{
				border-top: 1px solid #bbbbbb;
				border-bottom: 1px solid #dddddd;
			}
		</style>
		<link href="<?= base_url(); ?>css/bootstrap-responsive.css" rel="stylesheet"/>

		<!-- HTML5 shim, for IE6-8 support of HTML5 elements -->
		<!--[if lt IE 9]>
		<script src="../assets/js/html5shiv.js"></script>
		<![endif]-->

		<!-- Fav and touch icons -->
		<link rel="apple-touch-icon-precomposed" sizes="144x144" href="../assets/ico/apple-touch-icon-144-precomposed.png">
		<link rel="apple-touch-icon-precomposed" sizes="114x114" href="../assets/ico/apple-touch-icon-114-precomposed.png">
		<link rel="apple-touch-icon-precomposed" sizes="72x72" href="../assets/ico/apple-touch-icon-72-precomposed.png">
		<link rel="apple-touch-icon-precomposed" href="../assets/ico/apple-touch-icon-57-precomposed.png">
		<link rel="shortcut icon" href="<?= base_url(); ?>images/icons/favicon.ico">
		<script src="<?= base_url(); ?>js/jquery.js"></script>
		
	</head>

	<body>
	    
		<div class="navbar navbar-inverse navbar-fixed-top">
			<div class="navbar-inner">
				<?php
				    $this->load->view('_topmenu');
				?>
			</div>
		</div>
		<div class="container"></div>
		<div class="container">
