 <!DOCTYPE html>
<html>
 
<head>
	<script>var dragonFlyUrl="http://localhost:8080/dragonfly/Merge.html"</script>
	<meta http-equiv="content-type" content="text/html; charset=utf-8" />
		
	<title>DragonFly Traning</title>

	<link rel="stylesheet" type="text/css" href="/DataTables/css/jquery.dataTables.css"/>
	<link rel="stylesheet" type="text/css" href="/DataTables/extensions/TableTools/css/dataTables.tableTools.css"/>
	<link rel="stylesheet" type="text/css" href="/Editor/css/dataTables.editor.css"/>

	<script type="text/javascript" charset="utf-8" src="/DataTables/js/jquery.js"></script>
	<script type="text/javascript" charset="utf-8" src="/DataTables/js/jquery.dataTables.min.js"></script>
	<script type="text/javascript" charset="utf-8" src="/DataTables/extensions/TableTools/js/dataTables.tableTools.min.js"></script>
	<script type="text/javascript" charset="utf-8" src="/Editor/js/dataTables.editor.min.js"></script>
	
	<script type="text/javascript" charset="utf-8" src="js/table.corporate.js"></script>
	<script type="text/javascript" charset="utf-8" src="js/table.customer.js"></script>
	<script type="text/javascript" charset="utf-8" src="js/table.contact.js"></script>
	<script type="text/javascript" charset="utf-8" src="js/table.template.js"></script>
	<script type="text/javascript" charset="utf-8" src="js/table.directive.js"></script>
	
	<link rel="stylesheet" type="text/css" href="//code.jquery.com/ui/1.11.2/themes/smoothness/jquery-ui.css"/>
	<script src="//code.jquery.com/jquery-1.10.2.js"></script>
	<script src="//code.jquery.com/ui/1.11.2/jquery-ui.js"></script>
	
</head>

<body>
	<h1>DragonFly Training</h1>
	<div id="tabs">
		<ul>
			<li><a href="#Overview">Overview</a></li>
			<li><a href="#TestCase">Test Case</a></li>
			<li><a href="#Templates">Templates</a></li>
			<li><a href="#TestApp">Test Data</a></li>
		</ul>
			<div id="Overview">
				<div id="introductionTabs">
					<ul>
						<li><a href="#Introduction">Introduction</a></li>
						<li><a href="#Replace">The Replace Stack</a></li>
						<li><a href="#Insert">Inserting Sub-Templates</a></li>
						<li><a href="#Collection">Dynamic Sub-Templates</a></li>
					</ul>
					<div id="Introduction">					
						<p>DragonFly is a general purpose SLQ / Template merge tool that allows you to create 
						any type of text output, to one or multiple files based on a set of Templates and SQL based Merge Directives.
						</p>
						<img src="images/overview1.png"/>
					</div>
					<div id="Replace">					
						<p>Processing a "Replace Stack" is a core feature of DragonFly</p>
						<img src="images/overview2.png"/>
						<p>There are 4 ways to add values to the replace stack.</p>						
						<ul>
							<li><button id="parameters">http Parameters</button></li>
							<li><button id="replaceVal">the Replace Value directive</button></li>
							<li><button id="replaceCol">the Replace Col directive</button></li>
							<li><button id="replaceRow">the Replace Row directive</button></li>
						</ul>
					</div>
					<div id="Insert">					
						<p>Sub-Templates are inserted at Bookmarks, by the Insert directive. 
						One sub-template is inserted for each row in the Insert Directive result set.</p>
						<img src="images/overview3.png"/>
					</div>
					<div id="Collection">					
						<p>Sub-Templates can be varied based on data with Collections. If the 
						collection specifies a column name, the template inserted will be 
						from the collection, with the name specified in the bookmark and 
						the value for the collections column. If no template is found for the 
						column value, the empty value is used. If no template exists for 
						either the current column or empty value the merge will fail.</p>
						<img src="images/overview4.png"/>
					</div>
				</div>
			</div>
			
			<div id="TestCase">
				<p>The testing case is based on a company with customer contacts subscribed to a monthly newsletter.<br/>
				It's time to send out subscription renewal notices.
				Each customer contact has a preferred method of communication, (eMail, SMS or snail-mail).<br/>
				You have determined the following requirements for file formats:
				<ul>
					<li><button id="eMail">eMail</button></li>
					<li><button id="SMS">SMS</button></li>
					<li><button id="bulk">Bulk Mail</button></li>
				</ul> 
				In addition to generating the required files, you need to generate a report of the customers and contacts. <br/>
				The Templates for this test can be found on the Templates tab in the "test" collections<br>
				<ul>
					<li>Review the content of the Templates, and their Directives.</li>
					<li>You can generate the "allReports", "root" and "SMSroot" templates</li>
					<li>Hint: Start with "test.root:allReports" and follow the bookmarks</li>
					<li>Hint: Enter "Test" in the search box above the Collections column.</li>
				</ul>
				This button <button id="merge">Merge output</button> runs <button id="show">This Script</button>:<br/> 
			</div>

			<div id="Templates">
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
			
			<div id="TestApp">
				<table  width="100%"><tr><td valign="top" width="20%">
					<div class="container" id="divCorporate">
						<?php require 'html/table.corporate.html';?>
					</div>
				</td><td valign="top" width="50%">
					<div class="container" id="divCustomer">
						<?php require 'html/table.customer.html';?>
					</div>
				</td><td valign="top" width="30%">
					<div class="container" id="divContact">
						<?php require 'html/table.contact.html';?>
					</div>
				</td></tr></table>
			</div>
	</div>
</body>

	<div id="eMailDialog" title="eMail Submission">
		<p>You should generate 1 file per eMail, in the SendMail format below, as well as a "SendEmAll.sh" 
		file that contains the following sendmail command for each file generated:</p>
		<ul><li>cat <i>theFileName</i> | sendmail -i -t</li></ul>
		<textarea cols="70" rows="15">
From: "You" <You@yourDoman.com>
To: "Customer" <Customer@someDomain.com>
Subject: Your Subject Here
MIME-Version: 1.0
Content-Type: text/html; charset=iso-8859-1
Content-Disposition: inline

<html>
    <body>
        This is the HTML version of the email. Put your content here.
    </body>
</html>
		</textarea>
	</div>

	<div id="smsDialog" title="SMS Submission Format">
		<p>SMS Transmittals should be subbmitted with a script in the following format:</p>
		<textarea rows="3" cols="95">
curl http://textbelt.com/text -d number=########## -d "message=Your Message Here";
curl http://textbelt.com/text -d number=########## -d "message=Your Message Here";
curl http://textbelt.com/text -d number=########## -d "message=Your Message Here";
		</textarea>
	</div>
	<div id="bulkDialog" title="Bulk Mail Submission Format">
		<p>Outbound Bulk Mail should be submitted, 1 file per envelope, in the following format
		<textarea rows="10" cols="70">
##ENVELOPE
Spacely Sprockets
1234 Future St.
Flyville, NY 12345
									To Name
									To Street
									To City, St, Zip
##MESSAGE
Your message here
##END
		</textarea>
	</div>
	<div id="scriptDialog" title="Merge Command Execution">
	<textarea rows="6" cols="70">
$( "#merge" ).click(function() {
	var dragonFlyUrl="http://localhost:8080/dragonfly/Merge.html"
	var parameters = "collection=test.root&name=allReports";
	win=window.open(dragonFlyUrl + "?" + parameters, '_blank');
	win.focus();
});
	</textarea>
	</div>

	<div id="parametersDialog" title="Passing HTTP parameters">
		<p>All html parameters passed on the get or put call are added to the initial replace stack. 
		The collection and name parameters are required and should identfy the tempalte to merge. 
		A column value can also be provided with the column parameter</br>
		The url 
		</br><code>http://www.host.com/Merge?collection=root&name=test&myval=something</code></br>
		will result in the following repalce stack.</p>
		<table border="1">
			<thead><tr>
				<th>From</th>
				<th>To</th>
			</tr></thead>
			<tbody>
				<tr>
					<td>{collection}</td>
					<td>root</td>
				</tr><tr>
					<td>{name}</td>
					<td>test</td>
				</tr><tr>
					<td>{myval}</td>
					<td>something</td>
				</tr>
			</tbody>	
		</table>
		<p> Note that { and } are added to the from value</p>
	</div>
	
	<div id="replaceValDialog" title="Replace Value directive">
		<p>The replace value directive is a simple "From" and "To" value pair that are added to the replace hash. 
		The values are put on the replace hash as-is with no modifcations </br>
		This means that { and } are not added to the from value</p>
	</div>
	
	<div id="replaceColDialog" title="Replace Column Directive">
		<p>The replace Columns directive will use the "fromValue" and "toValue" of the SQL result set. 
		The Replace Column directive on the test.root:allReports template contains an example.</br>
		The SQL result set</p>
		<table border="1">
			<thead>
				<tr>
					<th>fromValue</th>
					<th>toValue</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td>corpName</td>
					<td>Spacely Sprockets</td>
				</tr><tr>
					<td>corpStreet</td>
					<td>1 Future Ave.</td>
				</tr><tr>
					<td>corpCity</td>
					<td>Space City</td>
				</tr><tr>
					<td>corpState</td>
					<td>LB</td>
				</tr><tr>
					<td>corpZip</td>
					<td>33576984</td>
				</tr>
			</tbody>
		</table>
		<p>will put the following replace values on the stack. </p>
		<table border="1">
			<thead>
				<tr>
					<th>from</th>
					<th>to</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td>{corpName}</td>
					<td>Spacely Sprockets</td>
				</tr><tr>
					<td>{corpStreet}</td>
					<td>1 Future Ave.</td>
				</tr><tr>
					<td>{corpCity}</td>
					<td>Space City</td>
				</tr><tr>
					<td>{corpState}</td>
					<td>LB</td>
				</tr><tr>
					<td>{corpZip}</td>
					<td>33576984</td>
				</tr>
			</tbody>
		</table>
		<p>Notes:</p>
		<ul>
			<li>If either of the columns fromValue or toValue are not in the result set the merge fails.</li>
			<li>The curly braces { and } are added to the fromValue</li> 
		</ul>
	</div>
	
	<div id="replaceRowDialog" title="Replace Row Directive">
		<p>The replace Row directive will use the column names as "fromValue" and the column value "toValue"
		The Replace Row directive on the TBD template contains an example. For the SQL result set</p>
		<table border="1">
			<thead>
				<tr>
					<th>name</th>
					<th>street</th>
					<th>city</th>
					<th>state</th>
					<th>zip</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td>Mike</td>
					<td>3324 Sunny Side St.</td>
					<td>Bladesville</td>
					<td>MN</td>
					<td>15313</td>
				</tr>
			</tbody>
		</table>
		<p>Would result in the following replace stack</p>
		<table border="1">
			<thead>
				<tr>
					<th>from</th>
					<th>to</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td>{name}</td>
					<td>Mike</td>
				</tr><tr>
					<td>{street}</td>
					<td>3324 Sunny Side St.</td>
				</tr><tr>
					<td>{city}</td>
					<td>Bladesville</td>
				</tr><tr>
					<td>{state}</td>
					<td>MN</td>
				</tr><tr>
					<td>{zip}</td>
					<td>15313</td>
				</tr>
			</tbody>
		</table>
		<p>Notes:</p>
			<ul>
				<li>The curly braces { and } are added to the column name</li>
				<li>If the result is not exactly 1 row, the merge fails</li>
			</ul>
	</div>
	
	

<script>
$(function() {$( "#tabs" ).tabs();});
$(function() {$( "#introductionTabs" ).tabs();});

$( "#eMailDialog" ).dialog({ autoOpen: false, minHeight: 200, minWidth: 700 });
$( "#smsDialog" ).dialog({ autoOpen: false, minHeight: 200, minWidth: 900 });
$( "#bulkDialog" ).dialog({ autoOpen: false, minHeight: 200, minWidth: 700 });
$( "#scriptDialog" ).dialog({ autoOpen: false, minHeight: 200, minWidth: 700 });
$( "#parametersDialog" ).dialog({ autoOpen: false, minHeight: 200, minWidth: 700 });
$( "#replaceValDialog" ).dialog({ autoOpen: false, minHeight: 200, minWidth: 700 });
$( "#replaceColDialog" ).dialog({ autoOpen: false, minHeight: 200, minWidth: 700 });
$( "#replaceRowDialog" ).dialog({ autoOpen: false, minHeight: 200, minWidth: 700 });

$( "#eMail" ).click(function() {  $( "#eMailDialog" ).dialog( "open" );});
$( "#SMS" ).click(function() {  $( "#smsDialog" ).dialog( "open" );});
$( "#bulk" ).click(function() {  $( "#bulkDialog" ).dialog( "open" );});
$( "#show" ).click(function() {  $( "#scriptDialog" ).dialog( "open" );});
$( "#parameters" ).click(function() {  $( "#parametersDialog" ).dialog( "open" );});
$( "#replaceVal" ).click(function() {  $( "#replaceValDialog" ).dialog( "open" );});
$( "#replaceCol" ).click(function() {  $( "#replaceColDialog" ).dialog( "open" );});
$( "#replaceRow" ).click(function() {  $( "#replaceRowDialog" ).dialog( "open" );});


$( "#merge" ).click(function() {
	var parameters = "collection=test.root&name=allReports";
	win=window.open(dragonFlyUrl + "?" + parameters, '_blank');
	win.focus();
});
</script>

</html>