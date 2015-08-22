<h1>Insert a new Consumer (Client)</h1>
<?php echo validation_errors(); ?>
<?php echo form_open('oasmanager/f_insert_consumer'); ?>
	<label for="consumerKey">Consumer KEY</label>
	<input type="text" name="consumerKey" id="consumerKey" value="<?php echo set_value('consumerKey'); ?>" style="width:20em;" />
	<button type="button" onclick="generateKey()" style="vertical-align: 20%;">Generate KEY</button>
	<label for="consumerSecret">Consumer Secret</label>
	<input type="text" name="consumerSecret" id="consumerSecret" value="<?php echo set_value('consumerSecret'); ?>" style="width:20em;" />
	<button type="button" onclick="generateSecret()" style="vertical-align: 20%;">Generate SECRET</button>
	<label for="consumerName">Consumer Name</label>
	<input type="text" name="consumerName" id="consumerName" value="<?php echo set_value('consumerName'); ?>" style="width:20em;" />
	<br>
	<input type="submit" value="Insert new consumer" />
<?php echo form_close(); ?>

<script>
	function generateKey() {
		var k = random_generator(20);
		$("#consumerKey").val(k);
		return false;
	}

	function generateSecret() {
		var s = random_generator(20);
		$("#consumerSecret").val(s);
		return false;
	}
</script>