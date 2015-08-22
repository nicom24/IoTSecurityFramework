<style>
	table, tr, td, th {
		border: 1px solid black;
	}
</style>

<h1>Stored Resources URIs</h1>
<a href="./insert_resource"><b>Insert a new Resource URI</b></a>
<br><br>
<table style="text-align: center; width: 50%;">
	<tr>
		<th>Resource URI ID</th>
		<th>Resource URI</th>
	</tr>
<?php 
	for ($ii = 0; $ii < count($resources); $ii++) {
		echo '<tr>
				<td>'.$resources[$ii]->r_id.'</td>
				<td>'.$resources[$ii]->resource_uri.'</td>
			  </tr>';
	}
?>
</table>