# IATI Import Tool

This tool allows users to import data in IATI format into a target system. The target system must support the endpoints described in the '**Destination System REST Endpoint Requirements**' section in the wiki home page (https://github.com/devgateway/iatiimport/wiki).

# Requirements
 - Java 8 or later	
 - Apache Maven 3.2.2

# Configuration
 After pulling the source code from github, you need to make some changes to import-core/import-ui/src/main/webapp/client/scripts/conf/index.js.
 
  - DESTINATION_API_HOST - domain name or IP address of  server that hosts the destination system
  - DESTINATION_AUTH_TOKEN_ENDPOINT - use this for configuring the url on the destination system that provides the authentication token.
  - USE_AUTHENTICATION_TOKEN - used to enable or disable authentication
  - AMP_DESKTOP_ENDPOINT - optional. The url that the tool redirects to when the close button is clicked.

# Packaging the Tool

 There are two steps involved in packaging the import tool. First, build the UI using grunt. Then create a jar file using maven.

## Build the UI
 
```
cd import-core/import-ui/src/main/webapp
npm install --dev
grunt build
```
 
## Create jar File
 
 ```
cd import-core/
mvn clean install
```

##  Run Jar File

```
cd /import-core\import-ui\target
java import-ui-0.0.6-SNAPSHOT.jar &
```

Then go to the initial page of the app: http://localhost:8080/importer/. 
     
# Copyright

Copyright 2015-2016 Development Gateway

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
  
