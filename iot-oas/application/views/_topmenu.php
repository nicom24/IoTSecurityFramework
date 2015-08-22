    		<div class="container">
    			<button type="button" class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
    				<span class="icon-bar"></span>
    				<span class="icon-bar"></span>
    				<span class="icon-bar"></span>
    			</button>
    			<?php
    				$uri = '';
    				if ($this->session->userdata('logged_in'))
    					$uri = site_url().'/oasmanager';
    				else
    					$uri = site_url().'/oaslogon';
    			?>
    			<!--  <a class="brand" href="<?= site_url(); ?>/oasmanager/view_general">OAuth HTTP Proxy</a> -->
    			<a class="brand" href="<?php echo $uri; ?>">OAuth HTTP Proxy</a>
    			<div class="nav-collapse collapse">
    				<ul class="nav">
    					<!--  <li><a href="<?= site_url(); ?>/oasmanager/view_general">Home</a></li> -->
    					<li><a href="<?php echo $uri; ?>">Home</a></li>
    				</ul>
    				<?php
    					if ($this->session->userdata('logged_in')) {
					?>
		    				<ul class="nav pull-right">
		    					<li>
		    						<span style="font-weight: bold; color: #00FF00; vertical-align: -50%;">
		    							Logged as: <?php echo $this->session->userdata('name').' '.$this->session->userdata('surname'); ?>&nbsp;&nbsp;
		    							<a href="<?= site_url(); ?>/oaslogon/logout"><img src="<?= base_url(); ?>images/icons/logoff.png" width="32"></a>
		    						</span>
		    					</li>
		    				</ul>
    				<?php
    					}
    				?>
    			</div><!--/.nav-collapse -->
    		</div>