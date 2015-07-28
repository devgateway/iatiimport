'use strict';

var Reflux = require('reflux');
var appActions = Reflux.createActions([
  'initSession',
  'initDestinationSession',
  'loadMenuData',
  'loadDestinationFieldsData',
  'loadSourceFieldsData',
  'loadMappingFieldsData', 
  'loadSourceValuesData',
  'loadDestinationValuesData',
  'loadValueMappingData'
]);

appActions.loadProjectData = Reflux.createAction({ asyncResult: true });
appActions.loadFileData = Reflux.createAction({ asyncResult: true });
appActions.loadFilterData = Reflux.createAction({ asyncResult: true });
appActions.loadLanguageData = Reflux.createAction({ asyncResult: true });
module.exports = appActions;
