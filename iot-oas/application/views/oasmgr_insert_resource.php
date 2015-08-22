<h1>Insert a new Resource URI</h1>
<?php echo validation_errors(); ?>
<?php echo form_open('oasmanager/f_insert_resource'); ?>
	<label for="resourceURI">Resource URI</label>
	<input type="text" name="resourceURI" id="resourceURI" value="<?php echo set_value('resourceURI'); ?>" style="width:20em;" />
	<br>
	<input type="submit" value="Insert new Resource URI" />
<?php echo form_close(); ?>