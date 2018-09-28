'use strict';

module.exports = {
  TOOL_HOST: '',
  TOOL_REST_PATH: '/importer/import',
  TOOL_START_ENDPOINT: '/importer/import/new/<%=sourceProcessor%>/<%=destinationProcessor%>/<%=authenticationToken%>/<%=username%>?host=<%=host%>',
  DESTINATION_API_HOST: '',
  DESTINATION_AUTH_TOKEN_ENDPOINT: '/rest/security/user',
  SETTINGS_ENDPOINT: '/rest/amp/settings',
  AMP_DESKTOP_ENDPOINT: '/aim/default/showDesktop.do',
  AMP_ADMIN_HOME: '/aim/admin.do',
  AMP_ACTIVITY_URL: '/aim/viewActivityPreview.do',
  USE_AUTHENTICATION_TOKEN: true,
  DESTINATION_AUTH_TOKEN: undefined,
  DESTINATION_USERNAME: undefined,
  DESTINATION_AUTH_TOKEN_EXPIRATION: undefined,
  DEBUG: false,
  REQUEST_TIMEOUT: 300000
};
