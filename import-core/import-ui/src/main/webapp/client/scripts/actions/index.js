'use strict';

var Reflux = require('reflux');
var appActions = Reflux.createActions([
  'initSession',
  'loadMenuData',
  'loadSourceValuesData',
  'loadDestinationValuesData'
]);


appActions.initDestinationSession = Reflux.createAction({ asyncResult: true });
appActions.refreshDestinationSession = Reflux.createAction({ asyncResult: true });
appActions.initializeMapping = Reflux.createAction({ asyncResult: true });
appActions.loadProjectData = Reflux.createAction({ asyncResult: true });
appActions.loadFileData = Reflux.createAction({ asyncResult: true });
appActions.loadFilterData = Reflux.createAction({ asyncResult: true });
appActions.loadLanguageData = Reflux.createAction({ asyncResult: true });

appActions.loadDestinationFieldsData = Reflux.createAction({ asyncResult: true });
appActions.loadSourceFieldsData = Reflux.createAction({ asyncResult: true });
appActions.loadMappingFieldsData = Reflux.createAction({ asyncResult: true });

appActions.loadValueMappingData = Reflux.createAction({ asyncResult: true });
appActions.checkBackendStatus = Reflux.createAction({ asyncResult: true });
appActions.loadImportListData = Reflux.createAction({ asyncResult: true });
appActions.loadImportLog = Reflux.createAction({ asyncResult: true });
appActions.deleteImport = Reflux.createAction({ asyncResult: true });

appActions.loadFieldMappingsTemplateList = Reflux.createAction({ asyncResult: true });
appActions.loadFieldMappingsById = Reflux.createAction({ asyncResult: true });
appActions.deleteMappingTemplate   = Reflux.createAction({ asyncResult: true });

appActions.loadValueMappingsTemplateList = Reflux.createAction({ asyncResult: true });
appActions.loadValueMappingsById = Reflux.createAction({ asyncResult: true });
appActions.deleteValueMappingsTemplate   = Reflux.createAction({ asyncResult: true });

appActions.loadImportSummary  = Reflux.createAction({ asyncResult: true });
appActions.loadWorkflowData = Reflux.createAction({ asyncResult: true });

appActions.loadSettings = Reflux.createAction({ asyncResult: true });


module.exports = appActions;
