# IBM Data Merge Utility v4.0.0 - Users Guide

## Contents
1. [Overview](#overview)
1. [Setting up a Template Development environment](#setting-up-a-template-development-environment)
  1. [Using IDMU-REST](#using-idmu-rest)
  1. [JSON Editors](#json-editors) - until idmu-rest gets a UI
1. [Template Developers Guide](#template-developers-guide)
  1. [Developing Templates](#developing-templates)
  1. [The Data Manager](#the-data-manager)
  1. [Templates Reference](#templates)
  1. Directive Reference
     1. [Enrich](#enrich)
     1. [Replace](#replace)
     1. [Insert](#insert)
     1. [Parse](#parse)
     1. [Save](#save)
  1. [Merge Processing](#merge-processing)
1. [IDMU Developers Guide](#idmu-developers-guide)
   1. [Extending Parsing Capabilities](#extending-parsing-capabilities)
   1. [Extending Provider Capabilities](#extending-provider-capabilities)
1. [IDMU Rest Administrators Guide](#idmu-rest-administrators-guide)
   1. [Building a Production Ready WAR](#building-a-production-ready-war) 
   1. [Deploying IDMU on Docker](#deploying-idmu-on-docker) 
   1. [Deploying IDMU on BlueMix](#deploying-idmu-on-bluemix) 
   1. [Deploying IDMU on WebSphere Liberty](#deploying-idmu-on-websphere-liberty) 
   1. [Deploying IDMU on Tomcat](#deploying-idmu-on-tomcat) 
1. [Configuring IDMU](#configuring-idmu)
   1. [Using the idmu-config Environment Variable](#using-the-idmu-config-environment-variable)  
   1. [About Enrichment Providers](#about-enrichment-providers)
   1. [Configuring Provider Environment Variables](#configuring-provider-environment-variables)
  
---
## Overview
A surprisingly large segment of Information Processing can be thought of as consisting of Transformation Services (take this data and transform it into another format) or Enrichment Services (take this data and go get me some more data). The IDMU utilizes a template based approach to these services, in which Merging Templates produces Transformed/Enriched output. The merge process is similar to the familiar Mail Merge feature in most word processors. Potential uses for this tool include:
- Simple transformations of XML to JSON or the other way around
- Enriching a JSON request with additional data from a database
- Enriching a JSON request with additional data from a Rest data source 
- Generating a XML or HTML document with data from a database
- Generating Configurations, Script or Code based on options specified as parameters.  

---
## Setting up a Template Development environment
### Using IDMU-REST 
The fastest way to get started working with templates is to use the Docker IDMU image.
You'll need Docker: https://docs.docker.com/engine/installation/ 
Then you can run this command to start and IDMU instance
```
docker run -d -p 9080:9080 -p 9443:9443 --name idmu flatballflyer/idmu:4.0.0.Beta1
```
See [The IDMU-REST Wiki](https://github.com/FlatBallFlyer/IBM-Data-Merge-Utility-REST/wiki/Curl-Command-cheat-sheet) for a curl cheat sheet

### JSON Editors
Until the IDMU-REST project has a Web-UI you'll need a good JSON editor. I'm still looking for a good Windows JSON editor. For users on Mac I can recommend [Power Json Editor](https://itunes.apple.com/us/app/power-json-editor/id499768540?mt=12)

---
## Template Developers Guide
### Developing Templates
Developing Templates is the main skill needed to use IDMU. The best starting place is to have a sample input / output that your templates should create. Start with a template that is just the expected output and work out from there. When developing templates, start at without any bookmarks or sub-templates and then add complexity in steps. Use of the Replace with Json options can be helpful if the data is not what you are expecting. 

---
### The Data Manager
IDMU has an internal Object Data store called the Data Manager. You can think of this as a smiple JSON object structure that only supports a String Primitive. Technically the data manager supports three Element types:
- Primitive: A simple string, IDMU does not have numeric or boolean primitives.
- Object: A list of unique attribute names and an Element value for each
- List: A list of Elements
The REST API will automatically place the HTTP Request Parameters in the data manager at the address **idmuParameters**, and the HTTP Request Payload at **idmuPayload**. The payload will be a simple primitive, and the parameters are an object of primitive lists.

#### Data Addresses
This object store will be used to store data that is fetched from an external data source (by an [Enrich](#enrich) directive). This data is transient, and is released from memory after the merge is completed. Most directives will either read data from the data store and take some action, or write data to the data store for use by other directives. Data in the Data Manager is accessed via a "path" style address. For example, given this data structure:
```
{ 
	mydata: { 
		name: "fred", 
		address: "123 Anywhere",
		friends: [
			{ 	name: "allen",
		  		since: "way back" 
		  	},
			{ 	name: "betty", 
		  		since: "last week" 
		  	}
		]
	},
 	someOtherData: { ... }
 }
	.
```
the Data Manager address "mydata-friends-[0]-name" would refer to "allen". Since you can't always predict what special characters might be used you your data names, whenever you provide a data address you specify the delimiter used as a path separator. The above address uses a dash "-" as the path separator.

#### Using Replace Tags in an Address
Data Manager addresses can contain replace tags. Replace tags will be processed during execution using the current Template Replace stack. See the [Replace Directive](#replace) for details on Tags and the Replace Stack
---
### Templates
The Template is the primary configuration item used by IDMU and describes both the structure of the data to be returned by a merge and the directives that drive the merge process. 
1. [Template Attributes](#template-attributes)
1. [Directives](#directives)
   - [The Data Manager](#the-data-manager)
   - [Replace](#replace)
   - [Parse](#parse)
   - [Enrich](#enrich)
   - [Insert](#insert)
   - [Save](#save)

#### Template Attributes:

- id: Unique Template Identifier consisting of
   - group: Defines a group of related templates. NOTE: A group name can not contain a period
   - name: The template name. NOTE: A template name can not contain a period
   - variant: The variant of the template - See the [Insert Directive](#insert) for more information on using template variants
A template Short Name can be experssed with period separators as in "group.template.variant"
- wrapper: The strings used to identify a tag/bookmark in content. These strings must exist in the content ONLY to idenify Tags and Bookmarks.
   - front: Indicates beginning of tag/bookmark
   - back: Indicates ending of tag/bookmark
- contentEncoding: Default Encoding to be used by [Replace Directives](#replace) 
			"3": "sql",
			"2": "html",
			"1": "none",
			"6": "default",
			"5": "xml",
			"4": "json"
- content: The [Template Content](#template-content)
- directives: A list of directives to execute during the merge
- description: Describes the template, used in error logging
- contentType: Used by Rest API as the return content type, does not have any effect on the Merge
- contentDisposition: Used by Rest API as the reutn content disposition, does not have any effect on the Merge 
- contentFileName: Reserved for Rest API, not currently used and does not have any effect on the Merge
- contentRedirectUrl: Reserved for Rest API, not currently used and does not have any effect on the Merge

#### Template Content

From an IDMU perspective, template content is just a block of text, it could be JSON, XML, HTML, a Bash Script, a Java Class, a NodeJs program, a CSV file.... anything you want to generate. The text in a template can have [**Replacement Tags**](#replace-tags) and [**Bookmarks**](#bookmarks) that identify where in the template data is to be placed, or sub-templates are to be inserted. Here is a sample of some JSON Template content:

```
{
	name: "<NAME>",
	address: "<ADDRESS>",
	friends: [<bookmark="friend" group="test" template="friend">]
}
```  
In this template we are using &lt; and &gt; to wrap the **tags** and **bookmarks**. The tags &lt;NAME&gt; and &lt;ADDRESS&gt; will be replaced with data during the merge process (by a [Replace](#replace) directive). Sub-Templates for each "friend" will be inserted at the **friend bookmark** by an [Insert](#insert) directive. 
     
### Directives

Each template will have a list of directives that are executed during the merge process. Most directives interact with the [Data Manager](#the-data-manager), and there are currently five directive types:
- [Enrich](#enrich) - Fetch Data and put it in the Data Manager
- [Parse](#parse) - Parse Data and put it in the Data Manager
- [Replace](#replace) - Replace **tags** with Data Values from the Data Manager
- [Insert](#insert) - Insert Sub-Templates based on values from the Data Manager
- [Save](#save) - Save a template output to the Merge Archive
 
#### Replace
The replace directive is used to replace Tags in the template with data values from the Data Manager. 
##### The Replace Stack
The replace directive does not directly replace the Tags in the template with data values, rather it loads a Replace Stack which is a list of "From" and "To" values and then optionally processes that stack over the template. This is useful if you want to place default values in the replace stack. It is also important to know that different directives can use the values in the replace stack during processing. The JDBC and JNDI data providers process replacement tags in a SQL statement from the templates Replace stack. It is also important to know that sub-templates inherit their parent's replacement stack.

##### Replace Tags
Replace Tags are any wrapped strings that are not bookmarks, and conform to this pattern
```
tag = "***name***" encode = "***encode***" format = "***formatString***" parseFirst
```
All fields are optional - using the wrappers { and } {foo} is the same as {tag=foo}. Attributes are:
- tag name is the "From" value used in the Replace Processing
- encode will override the default template encoding for this one tag - supported values are:
   - none - Do not encode
   - sql - Encode special characters and " marks
   - json - Encode new lines, tabs and slashes
   - xml - Encode &gt;, &lt; and &amp;
   - default - Use the template default encoding
- format is used to format values with Java String formatting features
- parseFirst if present will cause the replace To value to be parsed for embedded tags

##### Replace Directive Attributes
- name: The name of the directive - used in Logging
- dataSource: Path of source data in the Data Manager
- dataDelimeter: Delimiter used in data source path"
- type: The Directive Type - always 4 for Replace Directive
- ifMissing: Action to take if the specified dataSource is not in the Data Manager
   - 1: Throw - Throw a merge exception
   - 2: Ignore - Ignore this directive and continue processing
   - 3: Replace - Replace with the specified to value below
   - toValue: To value to be used if source is missing
   - fromValue: If provided will override the From value from the source path, if not provided the last element of the path is used as the "From" value.
- ifPrimitive: Action to take if source is a Primitive type
   - 1: Throw - Throw a merge exception
   - 2: Ignore - Ignore this directive and continue processing
   - 3: Replace - Add Replace values with the last element of the dataSource path Value used for From and the Primitive value for To
   - 4: Replace with JSON - Acts just like a regular Replace for a Primitive
- ifObject: Action to take if source is a Object, see Config for options"
   - 1: Throw - Throw a merge exception
   - 2: Ignore - Ignore this directive and continue processing
   - 4: Replace as List - Treat this as a List of 1 Object and perform List Replace
   - 5: Replace with JSON - Replace with JSON of Value
   - 3: Replace Object - Add the object attribute names / values to the replace stack
   - objectAttrPrimitive: Action to take if source is an Object and Attribute is Primitive
	   - 1: Throw - Throw a merge exception
   	   - 2: Ignore - Ignore this attribute and continue processing
       - 3: Replace - Add the attribute and value to the Replace Stack
   - objectAttrList: Action to take if source is a Object and Attribute is List
	   - 1: Throw - Throw a merge exception
   	   - 2: Ignore - Ignore this attribute and continue processing
	   - 3: Use first primitive as replace Value
	   - 4: Use last primitive as replace Value
   - objectAttrObject: Action to take if source is a Object and Attribute is Object
	   - 1: Throw - Throw a merge exception
   	   - 2: Ignore - Ignore this attribute and continue processing
- ifList: Action to take if source is a List
   - 1: Throw - Throw a merge exception
   - 2: Ignore - Ignore this directive and continue processing
   - 3: Replace - Add replace values based on the "From Attribute" and the "To Attribute" of each member of the list
   - 4: Use First - Add replace values like 3 but only the first member of the list 
   - 5: Last only - Add replace values like 3 but only the last member of the list
   - 6: Replace with JSON - Replace with JSON Structure
   - fromAttribute: Attribute name with From value
   - toAttribute: Attribute name with To value
   - listAttrMissing: Action to take if a List From / To attribute are missing
	   - 1: Throw - Throw a merge exception
   	   - 2: Ignore - Ignore this directive and continue processing
   - listAttrNotPrimitive: Action to take if a List From / To attribute is not a primitive
	   - 1: Throw - Throw a merge exception
   	   - 2: Ignore - Ignore this directive and continue processing
- processAfter: Boolean indicating that after adding values the merge should Process the Replace Stack over the Content
- processRequire: Boolean indicating that a Merge Exception should be thrown if a tag is not replaced
  
#### Parse
The Parse directive will take a Primitive String in the data manager, parse it and place the resulting structure in the Data Manager at a specified target. 

##### Parse Directive Attributes
- name: The name of the directive - used in Logging
- dataSource: Path of source data in the Data Manager
- dataDelimeter: Delimiter used in data source path"
- type: The Directive Type - always 3 for Parse Directive
- ifMissing: Action to take if the specified dataSource is not in the Data Manager
   - 1: Throw - Throw a merge exception
   - 2: Ignore - Ignore this directive and continue processing
   - staticData: Static Data used 
- ifPrimitive: Action to take if the specified dataSource is a Primitive
   - 1: Throw - Throw a merge exception
   - 2: Ignore - Ignore this directive and continue processing
   - 3: Parse the data
- ifObject: Action to take if the specified dataSource is an Object
   - 1: Throw - Throw a merge exception
   - 2: Ignore - Ignore this directive and continue processing
- ifList: Action to take if the specified dataSource is a List
   - 1: Throw - Throw a merge exception
   - 2: Ignore - Ignore this directive and continue processing
   - 3: Parse first primitive
   - 4: Parse last primitive
- dataTarget: The target where data will be placed in the Data Manager
- dataTargetDelimiter: The dataTarget address delimiter
- parseFormat: Parse Format
- parseOptions: Parse Options
 
##### Default Parsing Engines
Parsing engines can be added to the IDMU tool. To find out what parsing formats are supported in your implementation check the HTTP GET http://host/idmu/Config output. If you need to add a new parser, see the [IDMU Developers Guide](#idmu-developers-guide) below. 

#### Enrich
The enrich directive will retrieve data from an external data source, and place that data into the Data Manager. 

##### Enrich Directive Attributes
- name: The name of the directive - used in Logging
- type: Always 1 for Enrich Directives
- targetDataName: The path where data will be put
- targetDataDelimiter: The path delimeter
- parseFormat: Parse Format
- parseOptions: Parse Options  
The use of the following fields will be defined by the provider being used. See [Default Providers](#default-providers) below for details.
- enrichClass: The Enrichment Provider Class. 
- enrichSource: The name of the data source
- enrichParameter: The data source configuration parameter
- enrichCommand: The enrichment Command to execute

##### Default Providers
The default providers are shown below:
- com.ibm.util.merge.template.directive.enrich.provider.JndiProvider
   - enrichSource: Source specifies a JNDI Name
   - enrichParameter: Database name
   - enrichCommand: A SQL Select Statement, can contain replace tags
   - Data Returned: Returns a List of Object data structure
   - parsing: Not Supported
   
- com.ibm.util.merge.template.directive.enrich.provider.RestProvider
   - enrichSource: The configured Rest Source. The following environment variables are used: 
      - {SourceName}.HOST - The Host Name
      - {SourceName}.PORT - The Port
   - enrichParameter: N/A
   - enrichCommand: The URL to make a http get request to
   - Data Returned: Returns a List of Object data structure
   - parsing: Will parse the entire response

- com.ibm.util.merge.template.directive.enrich.provider.CacheProvider": {
   - enrichSource:  N/A
   - enrichParameter: N/A
   - enrichCommand: N/A
   - Data Returned: Always returns an object containing Cache Statistics
   - parsing: N/A

- com.ibm.util.merge.template.directive.enrich.provider.CloudantProvider
   - enrichSource: The Cloudant Source. The following environment varables are used:
      - {SourceName}.URL - The database connection URL
      - {SourceName}.USER - The database User ID
      - {SourceName}.PW - The database Password
   - enrichParameter: The datbase name
   - enrichCommand: A cloudant Query JSON string - Replace Tags are supported and jSon encoded
   - Data Returned: This provider always returns a List of Objects
   - parsing: N/A

- com.ibm.util.merge.template.directive.enrich.provider.FileSystemProvider
   - enrichSource: The following environment variables are expected
      - {SourceName}.PATH - The Path where files are to be read from.
   - enrichParameter: N/A
   - enrichCommand: A Java RegEx file selector
   - Data Returned: returns an object of FileName: String if not parsed, and FileName, Element if parsed
   - parsing: file content is parsed in the return object

- com.ibm.util.merge.template.directive.enrich.provider.MongoProvider
   - enrichSource: The following environment variables are expected
      - {SourceName}.URI - The database connection URL
      - {SourceName}.USER - The database User ID, if empty Mongo Anonymous Auth is used, otherwise ScramSha1 authentication is used.
      - {SourceName}.PW - The database Password
      - {SourceName}.DB - The database name  
   - enrichParameter: The Collection Name 
   - enrichCommand: Json Mongo Query Object
   - Data Returned: List of Mongo Document Objects
   - parsing: N/A

- com.ibm.util.merge.template.directive.enrich.provider.JdbcProvider
   - enrichSource: The following environment variables are expected
      - {source}.URI - Database Connection URI, without UserName/PW components
      - {source}.USER - The Database User ID to use
      - {source}.PW - The Password for the User ID 
   - enrichParameter: The Database Name
   - enrichCommand: A SQL Select Statement - Replace Tags are supported and SQL encoded
   - Data Returned: Always returns a List of Object
   - parsing: N/A

- com.ibm.util.merge.template.directive.enrich.provider.StubProvider
   - enrichSource: N/A
   - enrichParameter: N/A
   - enrichCommand: N/A
   - Data Returned: Primitive with TemplateJson if not parsed, Template object if parsed
   - parsing: JSON Parsing Supported

Providers can be added to the IDMU tool. To find out what providers are supported and the environment variable requirements of themcheck the HTTP GET http://host/idmu/Config output. If you need to add a new parser, see the [IDMU Developers Guide](#idmu-developers-guide) below. 

#### Insert
The Insert directive will insert sub-templates at bookmarks within the content. 

##### Bookmarks
A Bookmark Segment marks a location within the Content where sub-templates can be inserted
Bookmarks are wrapped strings that start with "bookmark" and conform to this pattern
```
bookmark = "***name***" group = "***group***" template = "***template***" varyby = "***varyBy Attribute***" insertAlways
```
All fields are required except the varyby field and insertAlways indicator. 
- name is the bookmark name, specified by the "bookmark pattern" attribute of an Insert directive
- group is the template group that a template will be inserted from
- template is the template name to be inserted
- varyby is the attribute within the insert context that identifies a sub-template variant to use. 
- insertAlways will allow bookmarks to insert sub-templates even when the varyby attr is missing or not-primitive, without this parameter an exception is thrown on missing or non-primitive varyby attribute values.

##### Insert Context
Sub templates are inserted in the context of some data in the Data Manager, typically a member of a list, or an attribute of an object. The Data Manager address "idmuContext" will always point to the insert context object within the Data Manager. 

##### Insert Directive Attributes
- name: The name of the directive - used in Logging
- type: The Directive Type - always 2 for Insert Directive
- dataSource: Path of source data in the Data Manager
- dataDelimeter: Delimiter used in data source path"
- ifMissing: Action to take if source is missing, see Config for options
   - 1: Throw - Throw a merge exception
   - 2: Ignore - Ignore this directive and continue processing
   - 3: Insert one sub-template
- ifPrimitive: Action to take if source is a Primitive, see Config for options
   - 1: Throw - Throw a merge exception
   - 2: Ignore - Ignore this directive and continue processing
   - 3: Insert one sub template with a primitive context
   - 4: Insert if - operator / value
   - ifOperator: operator for Insert If
     - 1: String equals
     - 2: String is empty
     - 3: String not empty
     - 4: String &gt;
     - 5: String &lt;
     - 6: Value =
     - 7: Value &gt;
     - 8: Value &lt;
   - ifValue: Values used with If Operator
- ifObject: Action to take if source is a Object, see Config for options
   - 1: Throw - Throw a merge exception
   - 2: Ignore - Ignore this directive and continue processing
   - 3: Insert a sub template for each attribute.
   - 4: Insert one sub-template in the context of the object
- ifList: Action to take if source is a List, see Config for options
   - 1: Throw - Throw a merge exception
   - 2: Ignore - Ignore this directive and continue processing
   - 3: Insert a sub-template for each member of the list
   - 4: Insert  a sub-template for the first member of the list
   - 5: Insert a sub-template for the last member of the list
- bookmarkPattern: The variant of the template
- notFirst: Replace tags that will be blank on the first insertion in a list
- notLast: Replace tags that will be blank on the last insertion in a list
- onlyFirst: Replace tags that will be blank on all but the first insertion in a list
- onlyLast: Replace tags that will be blank on all but the last insertion in a list
 
#### Save
The save directive will write the contents of the tempalte out to an entry in the Merge Archive. The default archive type is tar, you can change this by specifying a value for the parameter ***idmuArchiveType*** with one of the following:
- zip
- tar
- jar
- gzip
The generated archive will have a GUID name, you can override this name by providing a value for the parameter ***idmuArchiveName***  

##### Save Directive Attributes
- name: The name of the directive - used in Logging
- type: The Directive Type - always 5 for Save Directive
- fileName: The name of the file to be written to the archive
- clearAfter: Clear Content after saving"

##### Working with Archives
If you are using the IDMU-REST interface, the GET http://host/idmu/Archive/archiveName will retrieve the archive from the server and remove it from the temporary folder. If you are running the CLI you can find the archives in the output folder which defaults to /opt/ibm/idmu/v4/archives. You can overide this value in the idmu-config environment variable.

### Merge Processing
This diagram shows an overview of Merge Processing
![Processing Overview](http://flatballflyer.github.io/IBM-Data-Merge-Utility/WebContent/images/overview.png "Processing Overview")


---
## IDMU Developers Guide
### Extending Parsing Capabilities
### Extending Provider Capabilities
---
## IDMU Rest Administrators Guide
### Building a Production Ready WAR

### Deploying IDMU on Docker

### Deploying IDMU on BlueMix

### Deploying IDMU on WebSphere Liberty

### Deploying IDMU on Tomcat


## Configuring IDMU
### Using the idmu-config Environment Variable  
### About Enrichment Providers
### Configuring Provider Environment Variables
 