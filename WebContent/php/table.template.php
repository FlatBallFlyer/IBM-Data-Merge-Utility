<?php

/*
 * Copyright 2015 IBM
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
 * Editor server script for DB table template
 */

// DataTables PHP library
include( "../../../Editor/php/DataTables.php" );

// Alias Editor classes so they are easy to use
use
	DataTables\Editor,
	DataTables\Editor\Field,
	DataTables\Editor\Format,
	DataTables\Editor\Join,
	DataTables\Editor\Validate;


// Build our Editor instance and process the data coming from _POST
Editor::inst( $db, 'template', 'idtemplate' )
	->fields(
		Field::inst( 'template.idtemplate' ),
		Field::inst( 'template.idcollection' )->options( 'collection', 'idcollection', 'name' ),
		Field::inst( 'template.columnValue' ),
		Field::inst( 'template.name' ),
		Field::inst( 'template.type' )->options( 'codesTtype', 'code', 'meaning' ),
		Field::inst( 'template.output' ),
		Field::inst( 'template.description' ),
		Field::inst( 'template.content' ),
		Field::inst( 'collection.name' ),
		Field::inst( 'codesTtype.meaning' )
	)
	->leftJoin( 'collection', 'collection.idcollection', '=', 'template.idcollection' )
	->leftJoin( 'codesTtype', 'codesTtype.code', '=', 'template.type' )
	->process( $_POST )
	->json();
