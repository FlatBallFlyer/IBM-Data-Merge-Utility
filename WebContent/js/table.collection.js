/*
 * Editor client script for DB table replace
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
 */

(function($){
	var collectionEditor;
	
	$(document).ready(function() {
		var collectionEditor = new $.fn.dataTable.Editor( {
			ajax: "php/table.collection.php",
			table: "#collection",
			fields: [
				{
					label: "Name",
					name: "name"
				},
				{
					label: "Table Name",
					name: "tableName"
				},
				{
					label: "Column Name",
					name: "columnName"
				},
				{
					label: "Sample Value",
					name: "sampleValue"
				}
			]
		} );
	
	    // Activate an inline edit on click of a table cell
	    $('#collection').on( 'click', 'tbody td:not(:first-child)', function (e) {
	    	collectionEditor.inline( this );
	    } );

	    $('#collection').dataTable( {
			searching: false,
			paging: false,
			info: false,
			dom: "Tfrtip",
			ajax: "php/table.collection.php", 
			columns: [
	            { data: null, defaultContent: '', orderable: false  },
				{ data: "name" 			},
				{ data: "tableName" 	},
				{ data: "columnName" 	},
				{ data: "sampleValue" 	}
			],
	        order: [ 1, 'asc' ],
			tableTools: {
				sRowSelect: "os",
	            sRowSelector: 'td:first-child',
				aButtons: [
					{ sExtends: "editor_create", editor: collectionEditor },
					{ sExtends: "editor_remove", editor: collectionEditor }
				]
			},
		} );
	} );
}(jQuery));

