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
 * Editor server script for DB table directive
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
Editor::inst( $db, 'directive', 'iddirective' )
	->fields(
		Field::inst( 'directive.iddirective' ),
		Field::inst( 'directive.idtemplate' ),
		Field::inst( 'directive.idcollection' )->options( 'collection', 'idcollection', 'name' ),
		Field::inst( 'directive.type' )->options( 'codesDtype', 'code', 'meaning' ),
		Field::inst( 'directive.description' ),
		Field::inst( 'directive.selectColumns' ),
		Field::inst( 'directive.fromTables' ),
		Field::inst( 'directive.whereCondition' ),
		Field::inst( 'directive.fromValue' ),
		Field::inst( 'directive.toValue' ),
		Field::inst( 'directive.notLast' ),
		Field::inst( 'directive.onlyLast' ),
		Field::inst( 'collection.name' ),
		Field::inst( 'codesDtype.meaning' )
	)
	->where( 'directive.idtemplate', $_POST['templateId'] )
	->leftJoin( 'collection', 'collection.idcollection', '=', 'directive.idcollection' )
	->leftJoin( 'codesDtype', 'codesDtype.code', '=', 'directive.type' )
	->process( $_POST )
	->json();

