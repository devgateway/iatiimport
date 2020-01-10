'use strict';

module.exports = {
  TOOL_HOST: '',
  TOOL_REST_PATH: '/importer/import',
  TOOL_START_ENDPOINT: '/importer/import/new/<%=sourceProcessor%>/<%=destinationProcessor%>/<%=username%>?host=<%=host%>',
  DESTINATION_API_HOST: '',
  DESTINATION_USER_INFO_ENDPOINT: '/rest/security/user',
  SETTINGS_ENDPOINT: '/rest/amp/settings',
  AMP_DESKTOP_ENDPOINT: '/aim/default/showDesktop.do',
  AMP_ADMIN_HOME: '/aim/admin.do',
  AMP_ACTIVITY_URL: '/aim/viewActivityPreview.do',
  DESTINATION_USERNAME: undefined,
  DEBUG: false,
  REQUEST_TIMEOUT: 300000
};
