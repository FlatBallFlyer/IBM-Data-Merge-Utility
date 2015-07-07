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

Copy /build/sample-context.xml to /build/context.xml and adapt to your preference. This is the Tomcat context file that will be used by the maven tomcat7 plugin to run. Of course you can run on an external Tomcat as well, just mold the context.xml according to the sample based on your environment.

You can run by invoking:

    mvn clean install
    mvn tomcat7:run

This will launch the app on http://localhost:9090

## REST services available
There are 2 servlets:
- InitializeServlet: /Initialize
  GET will return a Text status, POST will reinitalize the application
- RestServlet: /idmu
  Leverages RequestHandlers to handle requests for the active RuntimeContext instance

## Supported URLs
    GET    /idmu/directives (full url example http://localhost:9090/idmu/directives)
    [
      {
        "notLast": [
          "empty"
        ],
        "onlyLast": [],
        "sequence": 0,
        "type": 2,
        "softFail": false,
        "description": "TestInsertSubsTag",
        "provider": {
          "type": 2
        }
      },
      {
        "notLast": [],
        "onlyLast": [],
        "sequence": 1,
        "type": 2,
        "softFail": false,
        "description": "Create Postal Message File",
        "provider": {
          "type": 2
        }
      }, ... ]

    GET    /idmu/templates
        [
          {
            "collection": "contact",
            "columnValue": "",
            "name": "cmd",
            "description": "Command Default Template (EMPTY)",
            "outputFile": "",
            "content": "",
            "directives": []
          },
          {
            "collection": "contact",
            "columnValue": "SMS",
            "name": "cmd",
            "description": "Send SMS Command",
            "outputFile": "",
            "content": "\ncurl http://textbelt.com/text -d number{phone} -d message\u003dTime to Renew!\u003ctkBookmark collection\u003d\"special\" name\u003d\"SMTP\"/\u003e",
            "directives": [
              {
                "sequence": 0,
                "type": 2,
                "softFail": false,
                "description": "TestInsertSubsTag",
                "provider": {
                  "type": 2
                }
              }
            ]
          }, ... ]

    GET    /idmu/templates/{collectionName}
    (similar payloads)

    GET    /idmu/templates/{collectionName}/{type}
    (similar payloads)

    GET    /idmu/templates/{collectionName}/{type}?columnValue={columnValue}
    (similar payloads)

    GET    /idmu/template/{templateFullName}
    {
        "collection": "contact",
        "columnValue": "SMS",
        "name": "cmd",
        "description": "Send SMS Command",
        "outputFile": "",
        "content": "\ncurl http://textbelt.com/text -d number{phone} -d message\u003dTime to Renew!\u003ctkBookmark collection\u003d\"special\" name\u003d\"SMTP\"/\u003e",
        "directives": [
          {
            "sequence": 0,
            "type": 2,
            "softFail": false,
            "description": "TestInsertSubsTag",
            "provider": {
              "type": 2
            }
          }
        ]
      }

    PUT    /idmu/template/{templateFullName} with new version template JSON
    reponse:status OK or FORBIDDEN, payload similar to GET /template/{fullName}

    DELETE /idmu/template/{templateFullName} Warning: really works and deletes on filesystem
    response: status OK or NOT_FOUND

    GET/POST /idmu/merge?{requestParameters} triggers a merge
    response: HTML representation of result


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
