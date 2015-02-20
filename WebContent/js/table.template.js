
/*
 * Editor client script for DB table template
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
	var templateTable;
	var theTemplate;
	var theName;
	var theCollection;
	var theColumn;
	
	var editor = new $.fn.dataTable.Editor( {
		"ajax": "php/table.template.php",
		"table": "#template",
		"fields": [
			{
				"label": "Collection",
				"name": "template.idcollection",
				"type": "select",
			},
			{
				"label": "Template Name",
				"name": "template.name",
				"type": "text"
			},
			{
				"label": "Column Value",
				"name": "template.columnValue",
				"type": "text"
			},
			{
				"label": "Type",
				"name": "template.type",
				"type": "select",
			},
			{
				"label": "Output",
				"name": "template.output",
				"type": "text"
			},
			{
				"label": "Description",
				"name": "template.description",
				"type": "text"
			},
			{
				"label": "Content",
				"name": "template.content",
				"type": "textarea"
			}
		]
	} );

	$('#template').dataTable( {
		"dom": "Tfrtip",
		"ajax": "php/table.template.php",
		"orderFixed": {
	        "post": [[0, 'asc'], [1, 'asc'], [2, 'asc']],
			"pre": [[0, 'asc'], [1, 'asc'], [2, 'asc']]
		},
		"columns": [
			{"data": "collection.name", 		"orderable": false},
			{"data": "template.name", 			"orderable": false},
			{"data": "template.columnValue", 	"orderable": false},
			{"data": "codesTtype.meaning", 		"orderable": false},
			{"data": "template.description", 	"orderable": false},
		],
		"tableTools": {
			"sRowSelect": "os",
			"fnRowSelected": function ( nodes ) {setTemplate(templateTable, nodes);},			
			"aButtons": [
				{ "sExtends": "editor_create", "editor": editor },
				{ "sExtends": "editor_edit",   "editor": editor },
				{ "sExtends": "editor_remove", "editor": editor },
				{ "sExtends": "text", "sButtonText": "Generate", 
					"fnClick": function ( nButton, oConfig, oFlash ) {merge();} 
                }  
			]
		}
	} );
	templateTable = $('#template').DataTable();

	editor.on( 'close',  function () { 
		theTemplate = '';
		theCollection = '';
		theName = '';
		theColumn = '';
		var event = new CustomEvent('templateSubmit');
		$( ".listener" ).trigger( "templateSubmit", ['', 0]  );
	} );
	
	function setTemplate(table, nodes) {
		theTemplate = table.row(nodes).data().template.idtemplate;
		theCollection = table.row(nodes).data().collection.name;
		theName = table.row(nodes).data().template.name;
		theColumn = table.row(nodes).data().template.columnValue;
		var theDisplayName = theCollection + ":" + theName + ":" + theColumn;
		var event = new CustomEvent('templateSet');
		$( ".listener" ).trigger( "templateSet", [theDisplayName, theTemplate]  );
	}

	$('#searchCollection').on( 'keyup', function () {
		templateTable
	        .columns( 0 )
	        .search( this.value )
	        .draw();
	} );
	
	function merge() {
		if (theTemplate == 0) {
			alert( 'Please select a Template' );
		} else {
			var parameters = "";
			parameters += "collection=" + theCollection;
			parameters += "&name=" + theName;
			if ( theColumn != "" ) {
				parameters += "&column=" + thColumn;
			} 
			win=window.open("http://localhost:8080/MergeTool/Merge.html?" + parameters, '_blank');
			win.focus();
		}		
	}

} );

}(jQuery));

