'use strict';

module.exports = {
  TOOL_HOST: 'http://localhost:9010',
  TOOL_REST_PATH: '/importer/import',
  TOOL_START_ENDPOINT: '/importer/import/new/<%=sourceProcessor%>/<%=destinationProcessor%>/<%=authenticationToken%>',
  DESTINATION_API_HOST: 'http://localhost:8080',
  DESTINATION_AUTH_TOKEN_ENDPOINT: '/mockup/session_info_alt.json',
  USE_AUTHENTICATION_TOKEN: true,
  DESTINATION_AUTH_TOKEN: ''
};