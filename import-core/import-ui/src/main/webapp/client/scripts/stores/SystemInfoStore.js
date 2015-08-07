'use strict';

var Reflux = require('reflux');
var request = require('superagent');
var appConfig = require('./../conf');
var appActions = require('./../actions');

var SystemInfoStore = Reflux.createStore({
  init: function() {
    this.listenTo(appActions.checkBackendStatus, this.handleCheckBackendStatus);
  },

  handleCheckBackendStatus: function() {
    var self = this;    
    $.ajax({
        url: '/system/status',
        timeout:1000,
        error: function() {        	
        	appActions.checkBackendStatus.failed();
        },
        dataType: 'json',
        success: function(data) {
        	appActions.checkBackendStatus.completed(data);
        },
        type: 'GET'
     }); 
  }

});

module.exports = SystemInfoStore;