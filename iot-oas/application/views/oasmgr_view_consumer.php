<style>
	table, tr, td, th {
		border: 1px solid black;
	}
</style>

<h1>Stored Consumers</h1>
<a href="./insert_consumer"><b>Insert a new Consumer (Client)</b></a>
<br><br>
<table style="text-align: center; width: 100%;">
	<tr>
		<th>Consumer ID</th>
		<th>Consumer Name</th>
		<th>Consumer KEY</th>
		<th>Consumer Secret</th>
	</tr>
<?php 
	for ($ii = 0; $ii < count($consumers); $ii++) {
		echo '<tr>
				<td>'.$consumers[$ii]->c_id.'</td>
				<td>'.$consumers[$ii]->name.'</td>
				<td>'.$consumers[$ii]->consumer_key.'</td>
				<td>'.$consumers[$ii]->consumer_secret.'</td>
			  </tr>';
	}
?>
</table>