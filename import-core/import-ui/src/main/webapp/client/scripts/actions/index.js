'use strict';

var Reflux = require('reflux');
var appActions = Reflux.createActions([
  'initSession',
  'initDestinationSession',
  'loadMenuData',
  'loadFileData',
  'loadProjectData',
  'loadDestinationFieldsData',
  'loadSourceFieldsData',
  'loadMappingFieldsData',
  'loadFilterData',
  'loadLanguageData',
  'loadSourceValuesData',
  'loadDestinationValuesData',
  'loadValueMappingData'
]);

module.exports = appActions;
