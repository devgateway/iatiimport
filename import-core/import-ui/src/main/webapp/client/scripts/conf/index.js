'use strict';

module.exports = {
  TOOL_HOST: '',
  TOOL_REST_PATH: '/importer/import',
  TOOL_START_ENDPOINT: '/importer/import/new/<%=sourceProcessor%>/<%=destinationProcessor%>/<%=authenticationToken%>/<%=username%>?host=<%=host%>',
  DESTINATION_API_HOST: '',
  DESTINATION_AUTH_TOKEN_ENDPOINT: '/rest/security/user',
  AMP_DESKTOP_ENDPOINT: '/aim/default/showDesktop.do',
  USE_AUTHENTICATION_TOKEN: true,
  DESTINATION_AUTH_TOKEN: undefined,
  DESTINATION_USERNAME: undefined,
  DEBUG: true,
  SESSION_REQUEST_TIMEOUT: 30000
};