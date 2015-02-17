 <!DOCTYPE html>
<html>
 
<head>

	<meta http-equiv="content-type" content="text/html; charset=utf-8" />
		
	<title>Template Admin</title>

	<link rel="stylesheet" type="text/css" href="/DataTables/css/jquery.dataTables.css"/>
	<link rel="stylesheet" type="text/css" href="/DataTables/extensions/TableTools/css/dataTables.tableTools.css"/>
	<link rel="stylesheet" type="text/css" href="/Editor/css/dataTables.editor.css"/>

	<script type="text/javascript" charset="utf-8" src="/DataTables/js/jquery.js"></script>
	<script type="text/javascript" charset="utf-8" src="/DataTables/js/jquery.dataTables.min.js"></script>
	<script type="text/javascript" charset="utf-8" src="/DataTables/extensions/TableTools/js/dataTables.tableTools.min.js"></script>
	<script type="text/javascript" charset="utf-8" src="/Editor/js/dataTables.editor.min.js"></script>
	
	<link rel="stylesheet" type="text/css" href="css/toolkit.css"/>
	<script type="text/javascript" charset="utf-8" src="js/table.collection.js"></script>
	<script type="text/javascript" charset="utf-8" src="js/table.template.js"></script>
	<script type="text/javascript" charset="utf-8" src="js/table.directive.js"></script>	
	<script type="text/javascript" charset="utf-8" src="js/table.report.js"></script>
	<script type="text/javascript" charset="utf-8" src="js/table.reportReplace.js"></script>
	<script type="text/javascript" charset="utf-8" src="js/table.codesDType.js"></script>
	<script type="text/javascript" charset="utf-8" src="js/table.codesTType.js"></script>
	
	<link rel="stylesheet" type="text/css" href="//code.jquery.com/ui/1.11.2/themes/smoothness/jquery-ui.css"/>
	<script src="//code.jquery.com/jquery-1.10.2.js"></script>
	<script src="//code.jquery.com/ui/1.11.2/jquery-ui.js"></script>	

	<script>
		$(function() {$( "#tabsTemplate" ).tabs();});
	</script>
	
	<style>
		td.validationTable {width:33%;vertical-align: top;} 
	</style>

</head>

<body>
	<div id="tabsTemplate">
		<ul>
			<li><a href="#tabTemplates">Templates</a></li>
			<li><a href="#tabReports">Reports</a></li>
			<li><a href="#tabTemplateValidation">Validation</a></li>
		</ul>
		<div id="tabTemplates">
			<table><tr><td valign="top">
				<div class="container" id="divTemplate">
					<?php require 'html/table.template.html';?>
				</div>
			</td><td class="validationTable">
				<div class="container" id="divDirective">
					<?php require 'html/table.directive.html';?>
				</div>
			</td></tr></table>
		</div>
		<div id="tabReports">
			<table><tr><td width="60%" valign="top">
				<div class="container" id="divReport">
					<?php require 'html/table.report.html';?>
				</div>
			</td><td width="40%" valign="top">
				<div class="container" id="divReportReplace">
					<?php require 'html/table.reportReplace.html';?>
				</div>
			</td></tr></table>
		</div>
		<div id="tabTemplateValidation">
			<table><tr><td width="33%" class="validationTable">
				<div class="container" id="divCollection">
					<?php require 'html/table.collection.html';?>
				</div>
		  	</td><td width="33%" class="validationTable">
				<div class="container" id="divTType">
					<?php require 'html/table.codesTType.html';?>
				</div>
			</td><td width="33%" class="validationTable">				
				<div class="container" id="divDType">
					<?php require 'html/table.codesDType.html';?>
				</div>
			</td></tr></table>
		</div>	
	</div>
</body>

</html>
