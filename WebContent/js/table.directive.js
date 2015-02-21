
/*
 * Editor client script for DB table directive
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
 */

(function($){

$(document).ready(function() {
	var theTemplate=0;
	var directiveTable;
	
	var editor = new $.fn.dataTable.Editor( {
		"table": "#directive",
	    "ajax": {
	        url: "php/table.directive.php",
	        type: "POST",
	        data: function ( d ) {d.templateId = theTemplate;}
	    },
		"fields": [
			{
				name: "directive.idtemplate",
				type: "hidden",
				def: function () {return theTemplate ;}
			},
			{
				"label": "Type",
				"name": "directive.type",
				"type": "select",
			},
			{
				"label": "Description",
				"name": "directive.description",
				"type": "text",
			},
			{
				"label": "Insert From Collection",
				"name": "directive.idcollection",
				"type": "select",
			},
			{
				"label": "JNDI Source",
				"name": "directive.jndiSource",
				"type": "text"
			},
			{
				"label": "Select Columns",
				"name": "directive.selectColumns",
				"type": "textarea"
			},
			{
				"label": "From Tables",
				"name": "directive.fromTables",
				"type": "textarea"
			},
			{
				"label": "Where Condition",
				"name": "directive.whereCondition",
				"type": "textarea"
			},
			{
				"label": "Not Last Tags:",
				"name": "directive.notLast",
				"type": "text"
			},
			{
				"label": "Only Last Tags",
				"name": "directive.onlyLast",
				"type": "text"
			},
			{
				"label": "From",
				"name": "directive.fromValue",
				"type": "text"
			},
			{
				"label": "To",
				"name": "directive.toValue",
				"type": "text"
			}
		]	
	} );

	directiveTable = $('#directive').DataTable( {
		searching: false,
		paging: false,
		info: false,
	    "orderFixed": [ 0, 'asc' ],
		"dom": "Tfrtip",
	    "ajax": {
	        url: "php/table.directive.php",
	        type: "POST",
	        data: function ( d ) {d.templateId = theTemplate;}
	    },
		"columns": [
			{ "data": "codesDtype.meaning"   },
			{ "data": "directive.description" }
		],
		"tableTools": {
			"sRowSelect": "os",
            "sRowSelector": 'td:first-child',
			"aButtons": [
				{ "sExtends": "editor_create", "editor": editor },
				{ "sExtends": "editor_edit",   
					"sButtonClass": "editor_edit",
                    "editor": editor,
                    "formButtons": [
                        {
                            label: "&gt;",
                            fn: function(e) {
                                this.submit( function () {
                                    var tt = $.fn.dataTable.TableTools.fnGetInstance('directive');
                                    var row = tt.fnGetSelected()[0];
                                    var rows = directiveTable.rows( {filter:'applied'} ).nodes();
                                    var index = rows.indexOf( row );

                                    tt.fnDeselect( row );
                                    if ( rows[ index+1 ] ) {
                                        tt.fnSelect( rows[index+1] );
                                        $('a.editor_edit').click();
                                    }
                                }, null, null, false );
                            }
                        },
                        {
                            label: "Save",
                            fn: function (e) {
                                this.submit();
                            }
                        },
                        {
                            label: "&lt;",
                            fn: function (e) {
                                this.submit( function () {
                                    var tt = $.fn.dataTable.TableTools.fnGetInstance('directive');
                                    var row = tt.fnGetSelected()[0];
                                    var rows = directiveTable.rows( {filter:'applied'} ).nodes();
                                    var index = rows.indexOf( row );
 
                                    tt.fnDeselect( row );
                                    if ( rows[index-1] ) {
                                        tt.fnSelect( rows[index-1] );
                                        $('a.editor_edit').click();
                                    }
                                }, null, null, false );
                            }
                        }
                   ]
				},
				{ "sExtends": "editor_remove", "editor": editor }
			]
		}
	} );
	
	$('#dirTemplate').on('templateSet', function( event, name, id ) {
		$('#dirTemplate').html( name ); 
		theTemplate = id;
		directiveTable.ajax.reload();
	});	

	editor.dependent('directive.type', function ( val, data, callback ) {
		if (val === '1' ) {  // 'Insert' {
			return { 
				show: [	"directive.idcollection",
				       		"directive.jndiSource",
				       		"directive.selectColumns",
				         	"directive.fromTables",
				         	"directive.whereCondition",
				         	"directive.notLast",
				         	"directive.onlyLast"],
				hide: [ "directive.fromValue",
			         		"directive.toValue" ]
			};
		} else if (val === '2' ) { //'ReplaceRow') {
			return {
				show: [ 	"directive.selectColumns",
				       		"directive.jndiSource",
				        	"directive.fromTables",
				        	"directive.whereCondition"],
		        hide: [		"directive.idcollection",
		               		"directive.fromValue",
		               		"directive.toValue",
				         	"directive.notLast",
				         	"directive.onlyLast"]
			};
		} else if (val === '3' ) { //'ReplaceCol') {
			return {
				show: [ 	"directive.selectColumns",
				       		"directive.jndiSource",
		               		"directive.fromTables",
				        	"directive.whereCondition"],
				hide: [		"directive.idcollection",
				       		"directive.selectColumns",
				        	"directive.fromValue",
				       		"directive.toValue",
				         	"directive.notLast",
				         	"directive.onlyLast"]
			};
		} else if (val === '4' ) { //'ReplaceVal') {
			return {
		        show: [ 	"directive.fromValue",
		                	"directive.toValue"],
		        hide: [		"directive.selectColumns",
				       		"directive.jndiSource",
		               		"directive.fromTables",
		               		"directive.whereCondition",
		               		"directive.idcollection",
				         	"directive.notLast",
				         	"directive.onlyLast"]
	        };
		}
	} );
		
} );

}(jQuery));

