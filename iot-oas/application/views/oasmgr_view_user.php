<style>
	table, tr, td, th {
		border: 1px solid black;
	}
</style>

<h1>Stored Users</h1>
<a href="./insert_user"><b>Insert a new User (Resource Owner)</b></a>
<br><br>
<table style="text-align: center; width: 50%;">
	<tr>
		<th>User ID</th>
		<th>User Name</th>
	</tr>
<?php 
	for ($ii = 0; $ii < count($users); $ii++) {
		echo '<tr>
				<td>'.$users[$ii]->u_id.'</td>
				<td>'.$users[$ii]->name.'</td>
			  </tr>';
	}
?>
</table>