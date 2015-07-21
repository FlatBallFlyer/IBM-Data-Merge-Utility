## What Is the IBM Data Merge Utility

The IBM Data Merge Utility is an open source general purpose Java utility, 
exposed as an HTTP service, that uses Templates with your data source to create 
one or more merge output files. The merge process is driven by a set of directives 
associated with a template. 

## Documentation

IDMU is looking for a html home, until that time you can access the documentation
in the WebContent folder of the project in Documentation.html

## Developing
First clone the project.

Create idmu-war/src/main/webapp/WEB-INF/properties/databasePools.properties by copying the example in the same directory

Modify databasePools.properties to contain your JDBC datasources

Ensure nodejs is installed : http://nodejs.org/

Ensure java 1.8 is installed and available on the path

Ensure Maven is installed and that 'mvn' is exposed on the path : http://maven.apache.org/

Go into the `build` dir and execute following helper scripts (from inside the build directory)

    # set up bower and gulp
    ./setupTools.sh

    # build and run using tomcat7 maven plugin
    ./build.sh
    ./run.sh
    # or
    ./buildRun.sh

This will launch the app on http://localhost:9090

Build is important because it generates output from the gulp project and moves to the right place in the idmu-editor jar so it is loaded to /editor in the war.
Just having gulp build is not enough, the files need to be copied to `idmu-editor/src/main/resources/META-INF/resources/editor/`
The scripts do all that for you.

Open a browser to
- http://localhost:9090/editor/html/app-en-desktop.html
- http://localhost:9090/idmu/templates (see REST services section below)
etc



## REST services available
There are 2 servlets:
- InitializeServlet: /Initialize
  GET will return a Text status, POST will reinitalize the application
- RestServlet: /idmu
  Leverages RequestHandlers to handle requests for the active RuntimeContext instance

## Supported URLs
    GET /idmu/merge?DragonFlyFullName=fullname&{additional requestParameters} triggers a merge
    	response: HTML representation of merge output

    GET /idmu/directives (full url example http://localhost:9090/idmu/directives)
    	[{"type": 0,"name": "Require Tags"},{"type": 1,"name": "Replace Value"},...]
  
    GET /idmu/collections (full url example http://localhost:9090/idmu/collections)
    	{["root","test",.....]}
    
    GET /idmu/templates/{collectionName} (full url example http://localhost:9090/idmu/templates/root)
        {["root.default.","root.allDirectives",....]}

    GET /idmu/template/{templateFullName}
	    {Template json}

    PUT /idmu/template/{Template json}
    	reponse:status OK or FORBIDDEN

    DELETE /idmu/template/{templateFullName} 
    	response: status OK or NOT_FOUND


## License

Licensed as Open Source under the Apache License, Version 2.0

/*
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
 *
 */
