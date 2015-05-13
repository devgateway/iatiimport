'use strict';

var Reflux = require('reflux');
var appActions = Reflux.createActions([
  'loadMenuData',
  'loadFileData',
  'loadProjectData'
]);

module.exports = appActions;