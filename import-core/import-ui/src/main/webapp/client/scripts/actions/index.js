'use strict';

var Reflux = require('reflux');
var appActions = Reflux.createActions([
  'loadMenuData',
  'loadFileData'
]);

module.exports = appActions;