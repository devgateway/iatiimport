'use strict';

var Reflux = require('reflux');
var appActions = Reflux.createActions([
  'initSession',
  'initDestinationSession',
  'loadMenuData',   
  'loadSourceValuesData',
  'loadDestinationValuesData'
]);

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

appActions.loadFieldMappingsTemplateList = Reflux.createAction({ asyncResult: true });
appActions.loadFieldMappingsById = Reflux.createAction({ asyncResult: true });
appActions.deleteMappingTemplate   = Reflux.createAction({ asyncResult: true });

appActions.loadValueMappingsTemplateList = Reflux.createAction({ asyncResult: true });
appActions.loadValueMappingsById = Reflux.createAction({ asyncResult: true });
appActions.deleteValueMappingsTemplate   = Reflux.createAction({ asyncResult: true });


module.exports = appActions;
