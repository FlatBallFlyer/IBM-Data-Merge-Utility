
/*
 * Editor client script for DB table reportReplace
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

$(document).ready(function() {
	var theReport=0;
	var reportReplaceTable;
	
	var editor = new $.fn.dataTable.Editor( {
		"table": "#reportReplace",
	    "ajax": {
	        url: "php/table.reportReplace.php",
	        type: "POST",
	        data: function ( d ) {d.reportId = theReport;}
	    },
		"fields": [
			{
				name: "reportReplace.idreport",
				type: "hidden",
				def: function () {return theReport ;}
			},
			{
				"label": "From",
				"name": "reportReplace.fromValue",
				"type": "text",
			},
			{
				"label": "To",
				"name": "reportReplace.toValue",
				"type": "text",
			}
		]	
	} );

	reportReplaceTable = $('#reportReplace').DataTable( {
		searching: false,
		paging: false,
		info: false,
	    "orderFixed": [ 0, 'asc' ],
		"dom": "Tfrtip",
	    "ajax": {
	        url: "php/table.reportReplace.php",
	        type: "POST",
	        data: function ( d ) {d.reportId = theReport;}
	    },
		"columns": [
			{ "data": "reportReplace.fromValue"   },
			{ "data": "reportReplace.toValue" }
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
                                    var tt = $.fn.dataTable.TableTools.fnGetInstance('reportReplace');
                                    var row = tt.fnGetSelected()[0];
                                    var rows = reportReplaceTable.rows( {filter:'applied'} ).nodes();
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
                                    var tt = $.fn.dataTable.TableTools.fnGetInstance('reportReplace');
                                    var row = tt.fnGetSelected()[0];
                                    var rows = reportReplaceTable.rows( {filter:'applied'} ).nodes();
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
	
	$('#repReport').on('reportSet', function( event, name, id ) {
		$('#repReport').html( name ); 
		theReport = id;
		reportReplaceTable.ajax.reload();
	});	
		
} );

}(jQuery));

