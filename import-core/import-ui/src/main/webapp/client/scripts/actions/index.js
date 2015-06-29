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
  'loadFilterData',
  'loadLanguageData',
  'loadSourceValuesData',
  'loadDestinationValuesData'
]);

module.exports = appActions;