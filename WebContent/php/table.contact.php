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
 * Editor server script for DB table contact
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
Editor::inst( $db, 'contact', 'idcontact' )
	->fields(
		Field::inst( 'contact.idcontact' ),
		Field::inst( 'contact.idcustomer' ),
		Field::inst( 'contact.name' ),
		Field::inst( 'contact.preference' ),
		Field::inst( 'contact.email' ),
		Field::inst( 'contact.phone' )
	)
	->where( 'contact.idcustomer', $_POST['customerId'] )
	->process( $_POST )
	->json();

