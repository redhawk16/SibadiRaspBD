<?php
header('Content-Type: application/json; charset=utf-8');

// Create connection
// $host = 'localhost';
// $user = 'root';
// $pass = '';
// $db_name = 'schedules';

$host = 'localhost';
$user = 'u999451g_bd';
$pass = '1aGSa6De';
$db_name = 'u999451g_bd';

$connect = new PDO("mysql:host=$host; dbname=$db_name; charset=utf8", $user, $pass);

if (!$connect) {
	echo 'Не могу соединиться с БД. Код ошибки: ' . mysqli_connect_errno() . ', ошибка: ' . mysqli_connect_error();
	exit;
}
 
// Select all of our stocks from table 'stock_tracker'
$sql = 'SELECT id, Name_Dayweek, Time_Start, Time_End, Name_Typeweek, Name_Typelesson, Name_Discipline, Name_Teacher, Number_Auditory FROM schedules INNER JOIN dayweeks ON schedules.Code_Dayweek=dayweeks.Code_Dayweek INNER JOIN lessons ON schedules.Number_Lesson=lessons.Number_Lesson INNER JOIN typeweeks ON schedules.Code_Typeweek=typeweeks.Code_Typeweek INNER JOIN typelessons ON schedules.Code_Typelesson=typelessons.Code_Typelesson INNER JOIN disciplines ON schedules.Code_Discipline=disciplines.Code_Discipline INNER JOIN teachers ON schedules.Code_Teacher=teachers.Code_Teacher INNER JOIN auditories ON schedules.Code_Auditory=auditories.Code_Auditory GROUP BY id';
 
// Confirm there are results
$result = $con->prepare($sql);
$result->excute();
if ($result)
{
	// We have results, create an array to hold the results
        // and an array to hold the data
	$resultArray = array();
	$tempArray = array();
 
	// Loop through each result
	while($row = $result->fetch(PDO::FETCH_OBJ))
	{
		// Add each result into the results array
		$tempArray = $row;
	    array_push($resultArray, $tempArray);
	}
 
	// Encode the array to JSON and output the results
	echo json_encode($resultArray, JSON_UNESCAPED_UNICODE);
}
 
// Close connections
mysqli_close($con);
?>