INSERT INTO `MergeTool`.`template` (`idtemplate`,`idcollection`,`columnValue`,`name`,`type`,`output`,`description`,`content`) VALUES 
(18,1,'','default',1,'','The default template ','DEFAULT TEMPLATE - \nThis template was used because the requested template was not found!\n{tkReplaceValues}'),
(19,2,'','testRoot',1,'','Root Testing Template','<!DOCTYPE html>\n<html>\n	<head>\n		<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\" />	\n		<title>Situation Generator</title>\n	</head>\n	<body>\n	<p>This is the Test report of Template Definitions</p>\n	<table border=\"1\" width=\"1000px\">\n		<tr>\n			<th>Collection</th>\n			<th>Name</th>\n			<th>Column Value</th>\n			<th>Type</th>\n		</tr>\n<tkBookmark name=\"template\"/>\n<t kBookmark name=\"saveFile1\"/>\n<t kBookmark name=\"saveFile2\"/>\n	</table>\n	</body>\n</html>'),
(20,2,'','template',1,'','Template testing Template','<tr>\n	<td>{collectionName}</td>\n	<td>{name}</td>\n	<td>{columnValue}</td>\n	<td>{typeName}</td>\n</tr>\n<tr>\n	<td></td>\n	<td colspan=\"3\">\n	<textarea cols=\"132\" rows=\"10\">{content}</textarea>\n	</td>\n</tr>\n<tr>\n	<td></td>\n	<td colspan=\"3\"><table border=\"1\">\n		<tr>\n			<th>Type</th>\n			<th colspan=\"3\">Info</th>\n		</tr>\n<tkBookmark name=\"directive\"/>\n	</table></td>\n</tr>'),
(21,2,'','directive',1,'','Default Directive Testing Template','<tr>\n	<td>{directiveType}</td>\n	<td colspan=\"3\">SELECT {selectColumns} FROM {fromTables} WHERE {whereCondition}</td>\n</tr>'),
(22,3,'Insert','directive',1,'','Insert Directive Test Template','<tr>\n	<td>{directiveType}</td>\n	<td>FROM {selectColumns} FROM {fromTables} WHERE {whereCondition}</td>\n	<td>Not Last Tags: {notLast}</td>\n	<td>Only Last Tags: {onlyLast}</td>\n</tr>'),
(23,3,'ReplaceVal','directive',1,'','ReplaceVal Directive Test Template','<tr>\n	<td>{directiveType}</td>\n	<td colspan=\"3\">Replace {fromValue} with {toValue}</td>\n</tr>'),
(27,2,'','saveFile1',1,'{name}.file1.txt','Output File #1','This is the output file for situtuation {name}\n{tkReplaceValues}\n'),
(28,2,'','saveFile2',1,'{name}.text2.txt','Output File#2','This is output file #2\n{tkReplaceValues}');

INSERT INTO `MergeTool`.`directive` (`iddirective`,`idtemplate`,`idcollection`,`type`,`description`,`selectColumns`,`fromTables`,`whereCondition`,`fromValue`,`toValue`,`notLast`,`onlyLast`) VALUES 
(15,19,2,1,'Insert Templates','*','templatefull','','','','',''),
(16,20,3,1,'Insert Directives','*','directivefull','idtemplate = {idtemplate}','','','',''),
(17,20,1,4,'and','','','','{and}','and','{and}','and'),
(18,20,1,4,'or','','','','{or}','or','{or}','or'),
(19,20,1,4,'coma','','','','{coma}',',','{coma}',','),
(20,20,1,4,'semicolin','','','','{semi}',';','{semi}',';'),
(21,20,1,4,'period','','','','{period}','.','{period}','.'),
(22,20,1,5,'Require idtemplate','','','','{idtemplate}','','{idtemplate}',''),
(23,21,1,2,'get collection','*','collection','idcollection = {idcollection}','','','',''),
(24,21,1,3,'Get Special Replace Values','fromValue, toValue','reportReplace','idreport = \'1\'','','','','');

