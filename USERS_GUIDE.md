# IBM Data Merge Utility v4.0.0 - Users Guide

## Contents
1. [Overview](#overview)
1. [Template Developers Guide](#template-developers-guide)
1. [IDMU Developers Guide](#idmu-developers-guide)
1. [IDMU Rest Administrators Guide](#idmu-rest-administrators-guide)
1. [Configuring IDMU](#configuring-idmu)
  
---
## Overview
A surprisingly large segment of Information Processing can be thought of as consisting of Transformation Services (take this data and transform it into another format) or Enrichment Services (take this data and go get me some more data). The IDMU utilizes a template based approach to these services, in which Merging Templates produces Transformed/Enriched output. The merge process is similar to the familiar Mail Merge feature in most word processors. Potential uses for this tool include:
- Simple transformations of XML to JSON or the other way around
- Enriching a JSON request with additional data from a database
- Enriching a JSON request with additional data from a Rest data source 
- Generating a XML or HTML document with data from a database
- Generating Configurations, Script or Code based on options specified as parameters.  

---
## Template Developers Guide
Developing Templates ......

### Using the IDMU-REST Developer Edition
See ... for installation instructions
See ... for a curl cheat sheet

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

From an IDMU perspective, template content is just a block of text, it could be JSON, XML, HTML, a Bash Script, a Java Class, a NodeJs program, a CSV file.... anything you want to generate. The text in a template can have **Replacement Tags** and **Bookmarks** that identify where in the template data is to be placed, or sub-templates are to be inserted. Here is a sample of some JSON Template content:

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
 
#### The Data Manager

IDMU has an internal Object Data store called the Data Manager. This object store supports three Element types:
- Primitive: A simple string, IDMU does not have numeric or boolean primitives.
- Object: A list of unique attribute names and an Element value for each
- List: A list of Elements
The REST API will automatically place the HTTP Request Parameters in the data manager at the address **idmuParameters**, and the HTTP Request Payload at **idmuPayload**. The payload will be a simple primitive, and the parameters are an object of primitive lists.

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
the Data Manager address "mydata-friends-[0]-name" would refer to "allen". Since you can't always predict what special characters might be used you your data names, you specify the delimiter used as a path separator when you provide an address, the above address uses a dash "-" as the path separator. 

#### Replace
The replace directive is used to replace Tags in the template with data values from the Data Manager. 
##### The Replace Stack
The replace directive does not directly replace the Tags in the template with data values, rather it loads a Replace Stack which is a list of "From" and "To" values and then optionally processes that stack over the template. This is useful if you want to place default values in the replace stack. It is also important to know that different directives can use the values in the replace stack during processing. The JDBC and JNDI data providers process replacement tags in a SQL statement from the templates Replace stack. It is also important to know that sub-templates inherit their parent's replacement stack.

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
##### Default Parsing Engines
#### Enrich
##### Default Providers
#### Insert
#### Save
##### Working with Archives
---
## IDMU Developers Guide
### Extending Parsing Capabilities
### Extending Provider Capabilities
---
## IDMU Rest Administrators Guide
### Deploying IDMU in a Production environment

#### Deploying IDMU on Docker

#### Deploying IDMU on BlueMix

#### Deploying IDMU on WebSphere Liberty

#### Deploying IDMU on Tomcat


## Configuring IDMU
### Using the idmu-config Environment Variable  
###  About Enrichment Providers
###  Configuring Provider Environment Variables
 