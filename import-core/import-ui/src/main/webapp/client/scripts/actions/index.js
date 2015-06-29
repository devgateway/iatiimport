'use strict';

var Reflux = require('reflux');
var appActions = Reflux.createActions([
  'loadMenuData',
  'loadFileData',
  'loadProjectData',
  'loadDestinationFieldsData',
  'loadSourceFieldsData',
  'loadRecipientData',
  'loadLanguageData',
  'loadSourceValuesData',
  'loadDestinationValuesData'
  
]);

module.exports = appActions;