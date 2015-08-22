<h1>Insert a new Resource Access policy</h1>
<?php echo validation_errors(); ?>
<?php echo form_open('oasmanager/f_insert_resource_access'); ?>
	<label for="resource">Related Resource</label>
	<select name="resource" id="resource">
		<?php 
			for ($ii = 0; $ii < count($resources); $ii++) {
				echo '<option value="'.$resources[$ii]->r_id.'">'.$resources[$ii]->r_id.' - '.$resources[$ii]->resource_uri.'</option>';
			}
		?>
	</select>
	<label for="token">Related Token ID</label>
	<select name="token" id="token">
		<?php 
			for ($ii = 0; $ii < count($tokens); $ii++) {
				echo '<option value="'.$tokens[$ii]->t_id.'">'.$tokens[$ii]->t_id.'</option>';
			}
		?>
	</select>
	<a href="./view_token" target="_blank" style="vertical-align: 20%;">See all available tokens</a>
	<br>
	<span>Actions</span>
	<br>
	<input type="checkbox" name="actions[]" id="actions[]" value="GET" style="vertical-align: inherit;">&nbsp;GET<br>
	<input type="checkbox" name="actions[]" id="actions[]" value="POST" style="vertical-align: inherit;">&nbsp;POST<br>
	<input type="checkbox" name="actions[]" id="actions[]" value="PUT" style="vertical-align: inherit;">&nbsp;PUT<br>
	<input type="checkbox" name="actions[]" id="actions[]" value="DELETE" style="vertical-align: inherit;">&nbsp;DELETE<br>
	<br>
	<input type="submit" value="Insert new Resource Access policy" />
<?php echo form_close(); ?>