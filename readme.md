## What Is the IBM Data Merge Utility

The IBM Data Merge Utility is an open source general purpose Java utility, 
exposed as an HTTP service, that uses Templates with your data source to create 
one or more merge output files. The merge process is driven by a set of directives 
associated with a template. 

## Documentation

IDMU is looking for a html home, until that time you can access the documentation
in the WebContent folder of the project in Documentation.html

## Developing
You can run by invoking:

    mvn clean install
    mvn tomcat7:run

This will launch the app on http://localhost:9090

### Test the REST service with SampleResource
Rest services in place are using jersey with auto conversion of JSON through jackson (per default jersey)
Supports JSON & XML representation at this time.
SampleResource is a very simple resource type with only a 'name' field which is also the primary identifier.
After launching you can test the service by:

    GET /rest/resources

to see the empty list

    POST {"name":"jon"} to /rest/resources with headers "Accept : application/json" and "Content-Type: application/json"

to create a resource with name "jon"

    GET /rest/resources/jon with Accept header

to see the resource

    PUT {"name":"mike"} to /rest/resources/jon with Accept and Content-Type header

to change the name of the resource jon to mike

    GET /rest/resources/mike with Accept header

to see the updated resource (renamed to mike)

    GET /rest/resources

to list and see the updated resource in the list

    DELETE /rest/resources/mike

to delete the resource and get back to an empty list


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
