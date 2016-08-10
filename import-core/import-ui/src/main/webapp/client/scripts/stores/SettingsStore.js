'use strict';

var Reflux = require('reflux');
var request = require('superagent');
var appConfig = require('./../conf');
var appActions = require('./../actions');

var SettingsStore = Reflux.createStore({
  init: function() {
    this.listenTo(appActions.loadSettings, this.handleLoadSettings);
  },
  handleLoadSettings: function() {		
    var self = this;    
    $.ajax({
        url: appConfig.SETTINGS_ENDPOINT,
        timeout: appConfig.REQUEST_TIMEOUT,
        error: function() {
          self.trigger([]);
        },
        dataType: 'json',
        success: function(data) {
          self.trigger(data);
        },
        type: 'GET'
     }); 
  }

});

module.exports = SettingsStore;