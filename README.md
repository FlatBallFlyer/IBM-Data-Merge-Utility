# IBM Data Merge Utility v4.0.0

### What Is IDMU
The IBM Data Merge Utility is an open source Java Utility that provides high performance transformation and enrichment services through a simple template based process. If you can use your word processors Mail Merge feature, then you can use the IBM Data Merge utility. The use of templates makes this transformation tool uniquely well suited to creating Scripts, Code or other complex documents. Optimized performance of small payload transformations makes it well suited to doing JSON or XML microservice transformations. While this utility does not expose a Rest API directly it is designed with that in mind. See [IDMU-REST Project](https://github.com/FlatBallFlyer/IBM-Data-Merge-Utility-REST)  

---

### Start Here

```
> git clone https://github.com/FlatBallFlyer/IBM-Data-Merge-Utility.git
> mvn install 
> mvn javadoc:javadoc 
```
Javadoc will be located in target/site/apidocs/index.html

If you want to run the integration testing, start these docker images:
```
docker run -p 3306:3306 --name idmu-mysql -d flatballflyer/idmu-test-mysql:latest
docker run -p 27017:27017 --name idmu-mongo -d flatballflyer/idmu-test-mongo:latest
docker run -p 50000:50000 --name idmu-db2 -v $(pwd):/share -d flatballflyer/idmu-test-db2:latest
docker run -p 8383:80 --name idmu-rest -d flatballflyer/idmu-test-rest:latest
docker run -p 81:80 --name idmu-cloudant -d flatballflyer/idmu-test-cloudant:latest;docker exec idmu-cloudant bash /app/initialize.sh
```

### Overview
Merging Templates produces output, and the merge process is similar to the familiar Mail Merge feature in most word processors. 
A template is just a block of text that can contain Replacement Tags and Book-marks along with directives that drive the merge process.
See JavaDoc for the com.ibm.util.merge.template.content package for more details, specifically the BookmarkSegment and TagSegment mark-up definitions

There are 5 directives you can use during the merge, see the JavaDoc for the package com.ibm.util.merge.directive:
1. Enrich - Get some data and put it in the DataManager
1. Replace - Take some data from the data manager and Replace Tags with Values
1. Insert - Insert sub-templates at Book Marks based on some data in the Data Manager
1. Parse - Get some makred up data from the DataManager, parse it and place the resulting data structure back in the Data Manager
1. Save - Save the output of the current template to an entry in the Merge Archive

As you can see, the Data Manager is a key component of the Merge Utility - and it basically provides "path" style access to an object data store. See the package com.ibm.util.merge.data for more details.

---

### Processing Overview
![alt text](http://flatballflyer.github.io/IBM-Data-Merge-Utility/WebContent/images/overview.png "Logo Title Text 1")

---

### Example Template and Merges
In the /src/test/resources folder find:
1. functional - sample templates and expected output that run stand alone - using File System provider for provider testing
1. integration - sample integration templates for default providers - see instructions on running a testing container in All_Integration_Tests.java

See the Provider Interface for Enrich Provider feature framework, and the individual providers for more detailed configuration options
See Config.get() for a json string with all configuration options - including template and directive enums

---

### Usage
```
   // Get a cache (Default configuration)
   Cache cache = new Cache(File); 

   // Get a cache (Custom configuration)
   Config config = new Config(from Env, String, File or URL)
   Cache cache = new Cache(config, File); 

   // Get a Merger for your template
   Merger merger = new Merger(cache, 
  	"some.template.name", 
  	parameters, payload);
	  
   // Merge the template 
   Template template = merger.merge();
 
   // Get Output as a simple String
   template.getMergedOutput().getValue()

   // Get Output as a i/o Stream
   template.getMergedOutput().streamOutput(stream)

```

See the /src/test/java - com.ibm.util.merge.MergeTestHarness.java for a more complete use case.

---

### See Also
1. Generated JavaDoc
1. [Template JSON Schema](https://github.com/FlatBallFlyer/IBM-Data-Merge-Utility/blob/master/WebContent/jsonSchema/schema.template.json)
1. [Config JSON Schema](https://github.com/FlatBallFlyer/IBM-Data-Merge-Utility/blob/master/WebContent/jsonSchema/schema.config.json)
1. [Sample Template with All Directive](https://github.com/FlatBallFlyer/IBM-Data-Merge-Utility/blob/master/src/test/resources/system.sample.json) - NOT MERGABLE
1. [Sample Config - Options](https://github.com/FlatBallFlyer/IBM-Data-Merge-Utility/blob/master/src/test/resources/config.sample.json)

---

### Related Projects
1. [IDMU-CLI Project](https://github.com/FlatBallFlyer/IBM-Data-Merge-Utility-CLI)
1. [IDMU-REST Project](https://github.com/FlatBallFlyer/IBM-Data-Merge-Utility-REST)
1. [IDMU-DEV Project](https://github.com/FlatBallFlyer/IBM-Data-Merge-Utility-DEV)
  
