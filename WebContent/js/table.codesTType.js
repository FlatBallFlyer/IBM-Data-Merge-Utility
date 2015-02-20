/*
 * Editor client script for DB table replace
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
	var TTypeEditor;

	$(document).ready(function() {
		TTypeEditor = new $.fn.dataTable.Editor( {
			ajax: "php/table.codesTType.php",
			table: "#codesTType",
			fields: [ {
					label: "Template Type",
					name: "meaning"
				}
			]
		} );

	    // Activate an inline edit on click of a table cell
	    $('#codesTType').on( 'click', 'tbody td:not(:first-child)', function (e) {
	    	TTypeEditor.inline( this );
	    } );
	
		$('#codesTType').DataTable( {
			searching: false,
			paging: false,
			info: false,
			dom: "Tfrtip",
			ajax: "php/table.codesTType.php",
			columns: [
	            { data: null, defaultContent: '', orderable: false  },
				{ data: "meaning" }
			],
	        order: [ 1, 'asc' ],
			tableTools: {
				sRowSelect: "os",
	            sRowSelector: 'td:first-child',
				aButtons: [
					{ sExtends: "editor_create", editor: TTypeEditor },
					{ sExtends: "editor_remove", editor: TTypeEditor }
				]
			}
		} );
	} );
}(jQuery));

