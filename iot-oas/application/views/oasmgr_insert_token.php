<h1>Insert a new Token</h1>
<?php echo validation_errors(); ?>
<?php echo form_open('oasmanager/f_insert_token'); ?>
	<label for="token">Token</label>
	<input type="text" name="token" id="token" value="<?php echo set_value('token'); ?>" style="width:20em;" />
	<button type="button" onclick="generateKey()" style="vertical-align: 20%;">Generate Token</button>
	<label for="tokenSecret">Token Secret</label>
	<input type="text" name="tokenSecret" id="tokenSecret" value="<?php echo set_value('tokenSecret'); ?>" style="width:20em;" />
	<button type="button" onclick="generateSecret()" style="vertical-align: 20%;">Generate Secret</button>
	<label for="tokenType">Token Type</label>
	<select name="tokenType" id="tokenType">
	  <option value="AT" <?php echo set_select('tokenType', 'AT', TRUE); ?>>AT - Access Token</option>
	  <option value="RT" <?php echo set_select('tokenType', 'RT'); ?>>RT - Request Token</option>
	</select>
	<label for="tokenRelatedUser">Related User</label>
	<select name="tokenRelatedUser" id="tokenRelatedUser">
		<?php 
			for ($ii = 0; $ii < count($users); $ii++) {
				echo '<option value="'.$users[$ii]->u_id.'">'.$users[$ii]->name.'</option>';
			}
		?>
	</select>
	<label for="tokenRelatedConsumer">Related Consumer</label>
	<select name="tokenRelatedConsumer" id="tokenRelatedConsumer">
		<?php 
			for ($ii = 0; $ii < count($consumers); $ii++) {
				echo '<option value="'.$consumers[$ii]->c_id.'">'.$consumers[$ii]->name.'</option>';
			}
		?>
	</select>
	<br>
	<input type="submit" value="Insert new token" />
<?php echo form_close(); ?>

<script>
	function generateKey() {
		var k = random_generator(20);
		$("#token").val(k);
		return false;
	}

	function generateSecret() {
		var s = random_generator(20);
		$("#tokenSecret").val(s);
		return false;
	}
</script>