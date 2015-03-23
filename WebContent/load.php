 <!DOCTYPE html>
<html>
 
<head>
	<script>var dragonFlyUrl="http://localhost:8080/dragonfly/Merge.html"</script>
	<meta http-equiv="content-type" content="text/html; charset=utf-8" />
		
	<title>Merge Tool Load Generation</title>
</head>

<body>
	<p></p>
	<ul>
		<li>Threads: <input type="text"/></li>
		<li>Wait Max: <input type="text"/></li>
		<button id="merge">Run Load</button>
	</ul>
	<p></p>
	
<script type="text/javascript">
$( "#merge" ).click(function() {
	// for x = 1 to threads spawn thread {wait(wait);
	var parameters = "collection=test.root&name=allReports";
	win=window.open(dragonFlyUrl + "?" + parameters, '_blank');
	win.focus();
});
</script>
</body>

</html>
