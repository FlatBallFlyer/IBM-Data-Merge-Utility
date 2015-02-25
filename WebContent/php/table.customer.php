<?php

/*
 * Copyright 2015, 2015 IBM
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Editor server script for DB table customer
 */

// DataTables PHP library
include( "configTraining.php" );

// Alias Editor classes so they are easy to use
use
	DataTables\Editor,
	DataTables\Editor\Field,
	DataTables\Editor\Format,
	DataTables\Editor\Join,
	DataTables\Editor\Validate;


// Build our Editor instance and process the data coming from _POST
Editor::inst( $db, 'customer', 'idcustomer' )
	->fields(
		Field::inst( 'customer.idcustomer' ),
		Field::inst( 'customer.name' ),
		Field::inst( 'customer.revenue' ),
		Field::inst( 'customer.profit' ),
		Field::inst( 'customer.street' ),
		Field::inst( 'customer.city' ),
		Field::inst( 'customer.state' ),
		Field::inst( 'customer.zip' ),
		Field::inst( 'customer.phone' ),
		Field::inst( 'customer.primary' )
	)
	->process( $_POST )
	->json();
