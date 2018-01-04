# IBM Data Merge Utility v4.0.0

## What Is IDMU
The IBM Data Merge Utility is an open source Java Utility that provides high performance transformation and enrichment services through a simple template based process. If you can use your word processors Mail Merge feature, then you can use the IBM Data Merge utility. The use of templates makes this transformation tool uniquely well suited to creating Scripts, Code or other complex documents. Optimized performance of small payload transformations makes it well suited to doing JSON or XML microservice transformations. When combined with enrichment services this becomes a powerful tool for the composition of complex microservice structures from a variety of sources. While this utility does not expose a Rest API directly it is designed with that in mind. See [IDMU-REST Project](https://github.com/FlatBallFlyer/IBM-Data-Merge-Utility-REST)  

## Related Pages
1. [IDMU-CLI Project](https://github.com/FlatBallFlyer/IBM-Data-Merge-Utility-CLI) - Command Line merge tool
1. [IDMU-REST Project](https://github.com/FlatBallFlyer/IBM-Data-Merge-Utility-REST) - Rest Interface merge tool
1. [IDMU Users Guide](https://flatballflyer.github.io/IBM-Data-Merge-Utility/USERS_GUIDE)

---
## Overview
A surprisingly large segment of Information Processing can be thought of as consisting of Transformation Services (take this data and transform it into another format) or Enrichment Services (take this data and go get me some more data). The IDMU utilizes a template based approach to these services, in which Merging Templates produces Transformed/Enriched output. The merge process is similar to the familiar Mail Merge feature in most word processors. Potential uses for this tool include:
- Simple transformations of XML to JSON or the other way around
- Enriching a JSON request with additional data from a database
- Enriching a JSON request with additional data from a Rest data source 
- Generating a XML or HTML document with data from a database
- Generating Configurations, Script or Code based on options specified as parameters.  

From an IDMU perspective, template content is just a block of text, it could be JSON, XML, HTML, a Bash Script, a Java Class, a NodeJs program, a CSV file.... anything you want to generate. The text in a template can have **Replacement Tags** and **Bookmarks** that identify where in the template data is to be placed, or sub-templates are to be inserted. A template also has other attributes, including a list of **Directives** that are the instructions which drive the merge process. Here is a sample of some JSON Template content:
```
{
	name: "<NAME>",
	address: "<ADDRESS>",
	friends: [<bookmark="friend" group="test" template="friend">]
}
```  
In this template we are using &lt; and &gt; to wrap the **tags** and **bookmarks**. The tags &lt;NAME&gt; and &lt;ADDRESS&gt; will be replaced with data during the merge process (by a **Replace** directive). Sub-Templates for each "friend" will be inserted at the **friend bookmark** by an **Insert** directive. 

IDMU has an internal Object Data store called the Data Manager. This object store will be used to store request data and parameters as well as data that is fetched from an external data source (by an **Enrich** directive). This data is transient, and is released from memory after the merge is completed. Most directives will either read data from the data store and take some action, or write data to the data store for use by other directives. Data in the Data Manager is accessed via a "path" style address. For example, given this data structure:
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
the Data Manager address "mydata-friends-[0]-name" would refer to "allen". 

Merge Directives are specified as part of the template. Each Directive is executed in order and the resulting template content is then returned as the output of the Merge. The following directives are used to drive merge processing, and any number of directives can be used.   
1. Enrich - Get some data from an external source and put it in the DataManager
1. Replace - Take some data from the data manager and Replace **Tags** with data values
1. Insert - Insert sub-templates at **Bookmarks** based on some data in the Data Manager. Sub-Templates are full-fledged templates and can contain their own directives.
1. Parse - Get some marked-up data from the DataManager, parse it and place the resulting data structure back in the Data Manager
1. Save - Save the output of the current template to an entry in the Merge Archive. (for archive creation - tar, zip, jar, gzip...)

---

## Processing Overview
![alt text](http://flatballflyer.github.io/IBM-Data-Merge-Utility/WebContent/images/overview.png "Logo Title Text 1")

---

## Example Templates and Merges
In the /src/test/resources folder find:
1. functional - sample templates and expected output that run stand alone - using File System provider for provider testing
1. integration - sample integration templates for default providers - see instructions on running a testing container in All_Integration_Tests.java

See the Provider Interface for Enrich Provider feature framework, and the individual providers for more detailed configuration options
See Config.get() for a json string with all configuration options - including template and directive enums

---
## From the Maven central repository (for Users of IDMU)

```
<dependency>
	<groupId>com.ibm.util.merge</groupId>
	<artifactId>idmu</artifactId>
	<version>4.0.0</version>
</dependency>
```

## Requirements (for Contributors to IDMU)
1. maven package manager - see (https://maven.apache.org/install.html)

### Start Here

```
> git clone https://github.com/FlatBallFlyer/IBM-Data-Merge-Utility.git
> mvn install 

```
Javadoc will be located in target/site/apidocs/index.html

### Integration Testing
There are several integration tests that touch JDBC, Cloudant, Mongo and rest services. If you want to run the integration testing, use the scripts located in /src/test/resources/integration/containers to start Docker containers with test data.
 - Use pull.sh to download and run the 5 containers
 - Use stop.sh to stop the containers
 - Use start.sh to start the containers
 - Use remove.sh to delete the containers from your system. 

---

## Usage

#### Simple

```
// Get a cache and load templates from default folders (using a Default Configuration)
Cache cache = new Cache();

while (processing) {
  // Get a Merger
  Merger merger = new Merger(cache, "your.template.name"); 
	 
  // Merge the template 
  Template template = merger.merge();
	
  // Get Output as a i/o Stream
  template.getMergedOutput().streamOutput(Your Output Stream)
}
```

#### Using a custom configuration

```

// Get a custom configuration
Config config = new Config(String, File or URL)
  
// Get a cache using the Configuration and a custom load folder
Cache cache = new Cache(config, Folder); 

```

#### Passing Request-Parameters and Request-Payload into the merge

```
Merger merger = new Merger(cache,"some.template.name", 
	parameters, payload);

```

#### Manually Pre-Loading custom data for a merge
Useful in highly customized applications
```
Merger merger = new Merger(cache,"some.template.name");
DataElement myData = new DataObject(); 
// (or Primitive or List - see merge.data.DataManager class and DataProxyJson for more options)

merger.getMergeData.set("some-path", "-", myData); 
// the datasource "some-path" (with a - path separator) will be available to the template when it's merged

merger.merge();

```

#### Manually creating templates
Your much better off using a JSON editor (see the JSON Schema the sample template and config files)
```
Cache cache = new Cache();
Template template = new Tempalte(....);
template.set....
cache.get|put|post|delete Template(template)

```

---

## See Also
1. Generated JavaDoc
1. [Template JSON Schema](https://github.com/FlatBallFlyer/IBM-Data-Merge-Utility/blob/master/WebContent/jsonSchema/schema.template.json)
1. [Config JSON Schema](https://github.com/FlatBallFlyer/IBM-Data-Merge-Utility/blob/master/WebContent/jsonSchema/schema.config.json)
1. [Sample Template with All Directive](https://github.com/FlatBallFlyer/IBM-Data-Merge-Utility/blob/master/src/test/resources/system.sample.json) - NOT MERGABLE
1. [Sample Config - Options](https://github.com/FlatBallFlyer/IBM-Data-Merge-Utility/blob/master/src/test/resources/config.sample.json)

---

  
