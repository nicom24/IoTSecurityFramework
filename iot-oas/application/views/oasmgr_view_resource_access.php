<style>
	table, tr, td, th {
		border: 1px solid black;
	}
</style>

<h1>Stored Resources Access policies</h1>
<a href="./insert_resource_access"><b>Insert a new Resource Access policy</b></a>
<br><br>
<table style="text-align: center; width: 100%;">
	<tr>
		<th>Resource ID</th>
		<th>Token ID</th>
		<th>Actions</th>
	</tr>
<?php 
	for ($ii = 0; $ii < count($resources_access); $ii++) {
		echo '<tr>
				<td>'.$resources_access[$ii]->resource_id.'</td>
				<td>'.$resources_access[$ii]->token_id.'</td>
				<td>'.$resources_access[$ii]->actions.'</td>
			  </tr>';
	}
?>
</table>