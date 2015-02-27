
/*
 * Editor client script for DB table report
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
	var reportTable;
	var theReport = 0;
	var theTemplateCollection;
	var theTemplateName;
	var theTemplateColumn;
	
	var editor = new $.fn.dataTable.Editor( {
		"ajax": "php/table.report.php",
		"table": "#report",
		"fields": [
			{
				"label": "Name",
				"name": "report.name",
				"type": "text",
			},
			{
				"label": "Template",
				"name": "report.idtemplate",
				"type": "select"
			},
			{
				"label": "Output Dir",
				"name": "report.outputRoot",
				"type": "text"
			}
		]
	} );

	$('#report').dataTable( {
		"dom": "Tfrtip",
		"ajax": "php/table.report.php",
		"columns": [
			{"data": "report.name"},
			{"data": "templatefull.fullName"},
			{"data": "report.outputRoot"},
		],
		"tableTools": {
			"sRowSelect": "os",
			"fnRowSelected": function ( nodes ) {setReport(reportTable, nodes);},			
			"aButtons": [
				{ "sExtends": "editor_create", "editor": editor },
				{ "sExtends": "editor_edit",   "editor": editor },
				{ "sExtends": "editor_remove", "editor": editor },
				{ "sExtends": "text", "sButtonText": "Generate", 
					"fnClick": function ( nButton, oConfig, oFlash ) {generateReport();} 
                }  				
			]
		}
	} );
	reportTable =  $('#report').DataTable();

	$('#reportListener').on('templateSubmit', function( event, name, id ) {
		reportTable.ajax.reload();
		theReport = 0;
		theTemplateCollection ='';
		theTemplateName = '';
		theTemplateColumn = '';
		var event = new CustomEvent('reportSet');
		$( ".listener" ).trigger( "reportSet", ["", 0]  );
	});	

	function setReport(table, nodes) {
		theReport = table.row(nodes).data().report.idreport;
		theTemplateCollection = table.row(nodes).data().templatefull.collectionName;
		theTemplateName = table.row(nodes).data().templatefull.name;
		theTemplateColumn = table.row(nodes).data().templatefull.columnValue;		
		var theName = table.row(nodes).data().report.name;
		var event = new CustomEvent('reportSet');
		$( ".listener" ).trigger( "reportSet", [theName, theReport]  );
	}
	
	function generateReport() {
		if (theReport == 0) {
			alert( 'Please select a report' );
		} else {
			var parameters = "";
			parameters += "collection=" + theTemplateCollection.trim();
			parameters += "&name=" + theTemplateName.trim();
			if ( theTemplateColumn != "" ) {
				parameters += "&column=" + theTemplateColumn.trim();
			} 
			
			var replaceTable = $('#reportReplace').DataTable();
			var replaceData =  replaceTable.rows().data();
			for (row = 0 ; row < replaceData.length ; row++) {
				var rowData = replaceTable.row(row).data();
				parameters += "&" + rowData.reportReplace.fromValue + "=" + rowData.reportReplace.toValue;
			}
			win=window.open("http://localhost:8080/dragonfly/Merge.html?" + parameters, '_blank');
			win.focus();
		}
	}

} );

}(jQuery));

