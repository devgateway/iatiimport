# iatiimport

#Overview
Tool for importing IATI data files into target systems. The target system must provide the endpoints described in the '**Destination System REST Endpoint Requirements**'.



# Target Audience
 This documentation is for developers who would like to integrate the IATI Import Tool with target systems.

# Requirements
 - Java 8 or later	
 - Apache Maven 3.2.2

# Source Control
  The github url for the project is https://github.com/devgateway/iatiimport.
  
  The latest release of the software can be found here: https://github.com/devgateway/iatiimport/releases.

# Configuration
 After pulling the source code from github, you need to make some changes to import-core/import-ui/src/main/webapp/client/scripts/conf/index.js.
 
  - DESTINATION_API_HOST - domain name or IP address of  server that hosts the destination system
  - DESTINATION_AUTH_TOKEN_ENDPOINT - use this for configuring the url on the destination system that provides the authentication token.
  - USE_AUTHENTICATION_TOKEN - used to enable or disable authentication
  - AMP_DESKTOP_ENDPOINT - optional. The url that the tool redirects to when the close button is clicked.
  
# Destination System REST Endpoint Requirements

## General Information

There are two types of endpoints required, the ones that retrieve information and the ones that commit information to the destination system. This is the list of required endpoints for the destination system for the Import Tool.

All the REST operations should return appropriate HTTP Status values:
200 OK if the operation was successful
500 if there was any error


## List of endpoints to retrieve information
### List of available fields
Returns the list of available fields in a project in the destination system.

Method: GET
Suggested REST URL: /fields 
JSON Result:
```json
[
{
	field_name: "activity_status",
	field_type: "string"
},
{
	field_name: "title",
	field_type: "string"
},
{
	field_name: "sectors",
	field_type: "array"
}
]```

### List of possible values
Returns the list of possible values for :field_name in form of an array. Leave additional_property_N for future extensions that might be needed.

Method: GET
Suggested REST URL: /fields/:field_name
JSON Result:
[
	{
		id: 1,
		value: "Completed",
		properties: [
			additional_property_1: "xx",
			additional_property_2: "xx"
		]
	},
	{
		id: 2,
		value: "Ongoing",
		properties: [
			additional_property_1: "xx",
			additional_property_2: "xx"
		]
	}
]

### Raw List of projects
Returns a list of all projects with a limited set of fields: id, title, date, no aggregations.

Method: GET
Suggested REST URL: /projects
[
	{
		amp_activity_id: 1,
		amp_id: "8822220000",
		external_id: “IATI23123123”,
		title: "Project Title 1",
		created_date: "2001-01-01 12:00:00"
	},
	{
		amp_activity_id: 2,
		amp_id: "8822220001",
		title: "Project Title 2",
		created_date: "2001-01-01 12:00:00"
	},
	{
		amp_activity_id: 3,
		amp_id: "8822220002",
		title: "Project Title 3",
		created_date: "2001-01-01 12:00:00"
	},
]

### Complete project information
Returns the complete JSON of a project
Method: GET
Suggested REST URL: /projects/:project_id

{
	amp_activity_id: 1,
	amp_id: "8822220000",
	title: "Project Title 1",
	created_date: "2001-01-01 12:00:00"
	sectors: [
		{
			id: 1,
			name: "Sector Name 1",
			percentage: 100
		}
	]
	...
}

## Endpoints that commit information
### Update project
Updates an existing project. We send a JSON with the project with fields updated. The destination systeme modifies what is appropriate in the destination.

Method: POST
Request Body: JSON
Suggested REST URL: /projects/:project_id
{
	amp_activity_id: 1,
	amp_id: "8822220000",
	title: "Project Title Changed",
	created_date: "2001-01-01 12:00:00"
	sectors: [
		{
			id: 1,
			name: "Sector Name 1",
			percentage: 100
		}
	]
	...
}

### Insert new project
Inserts a new project. We send a JSON with the new project with fields updated. The destination system creates the new project based on the JSON.

Method: POST
Request Body: JSON
Suggested REST URL: /projects
{
	amp_activity_id: 1,
	amp_id: "8822220000",
	title: "Project Title Changed",
	created_date: "2001-01-01 12:00:00",
	external_id: 
	sectors: [
		{
			id: 1,
			name: "Sector Name 1",
			percentage: 100
		}
	]
	...
}

# Building the Tool
 On the terminal, change directory to import-core. Run 'mvn clean install'. A jar file will be created in import-core/import-ui/target.
   
# Installation 
  We have provided a script for running the jar file in linux. Please check import-core/import-ui/scripts/import-tool.sh. Modify the file to suit your environment.
 



  
