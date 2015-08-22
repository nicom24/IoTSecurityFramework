<style>
	table, tr, td, th {
		border: 1px solid black;
	}
</style>

<h1>Stored Tokens</h1>
<a href="./insert_token"><b>Insert a new Token</b></a>
<br><br>
<b>Click on each row to see related resources and related actions.</b>
<br><br>
<table style="text-align: center; width: 100%;">
	<tr>
		<th>Token ID</th>
		<th>User</th>
		<th>Consumer</th>
		<th>Token Public</th>
		<th>Token Secret</th>
		<th>Token Type</th>
	</tr>
<?php 
	for ($ii = 0; $ii < count($tokens); $ii++) {
		$tid = $tokens[$ii]->t_id;
		echo '<tr class="token_row" id="token_row_'.$tid.'">
				<td>'.$tokens[$ii]->t_id.'</td>
				<td>'.$tokens[$ii]->u_name.'</td>
				<td>'.$tokens[$ii]->c_name.'</td>
				<td>'.$tokens[$ii]->token.'</td>
				<td>'.$tokens[$ii]->token_secret.'</td>
				<td>'.$tokens[$ii]->type.'</td>
			  </tr>';
	}
?>
</table>
<div id="ABC"></div>

<script>
	$(function() {
		$(".token_row").click(function(e) {
			var id_complete = $(this).closest('tr').attr('id');
			e.stopPropagation();
			var id = id_complete.split('_')[2];

			$("#extra_info").remove();
			$.getJSON("http://localhost/oauthproxy/index.php/oasmanager/f_view_token/" + id + "?callback=?", function(data) {
				var list = 'Related Resources: <span style="color: red; font-weight: bold;">' + data.length + '</span> availables<br><ul>';
				for (var kk = 0; kk < data.length; kk++) {
					list += '<li>URI: ' + data[kk].resource_uri + ' --> ' + data[kk].actions + '</li>';
				}
				list += '</ul>';
				$("#" + id_complete).after('<tr id="extra_info" style="text-align:left;"><td colspan="6">' + list + '</td></tr>');
			});
		});
	});
</script>