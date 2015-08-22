<h1>Insert a new User (Resource Owner)</h1>
<?php echo validation_errors(); ?>
<?php echo form_open('oasmanager/f_insert_user'); ?>
	<label for="user">User Name</label>
	<input type="text" name="user" id="user" value="<?php echo set_value('user'); ?>" style="width:20em;" />
	<br>
	<input type="submit" value="Insert new user" />
<?php echo form_close(); ?>