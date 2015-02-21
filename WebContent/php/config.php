<?php 

/*
 * Configure Database Connection and include editor.datatabls.net library
 */
$sql_details = array(
	"type" => "Mysql",
	"user" => getenv('OPENSHIFT_MYSQL_DB_USERNAME'),
	"pass" => getenv('OPENSHIFT_MYSQL_DB_PASSWORD'),
	"host" => getenv('OPENSHIFT_MYSQL_DB_HOST'),
	"port" => getenv('OPENSHIFT_MYSQL_DB_PORT'),
	"db"   => "MergeTool"
);

include( "../../../Editor/php/DataTables.php" );

?>