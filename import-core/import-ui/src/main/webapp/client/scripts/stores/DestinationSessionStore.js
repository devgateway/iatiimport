'use strict';

var Reflux = require('reflux');
var request = require('superagent');
var appConfig = require('./../conf');
var appActions = require('./../actions');


var DestinationSessionStore = Reflux.createStore({

  init: function() {
    this.listenTo(appActions.initDestinationSession, this.handleInitSession);
    this.listenTo(appActions.refreshDestinationSession, this.handleRefreshSession);

  }, 
  handleInitSession: function() {
    var self = this;
    $.ajax({
        url: appConfig.DESTINATION_API_HOST + appConfig.DESTINATION_USER_INFO_ENDPOINT,
        timeout: appConfig.REQUEST_TIMEOUT,
        error: function() {
        	appActions.initDestinationSession.failed();
        },
        dataType: 'json',
        success: function(data) {
          appActions.initDestinationSession.completed(data);
        },
        type: 'GET'
     });
  },
  handleRefreshSession: function() {
    var self = this;
    $.ajax({
        url: appConfig.DESTINATION_API_HOST + appConfig.DESTINATION_USER_INFO_ENDPOINT,
        timeout: appConfig.REQUEST_TIMEOUT,
        error: function() {
        	appActions.refreshDestinationSession.failed();
        },
        dataType: 'json',
        success: function(data) {
          appActions.refreshDestinationSession.completed(data);
        },
        type: 'GET'
     });
  }

});

module.exports = DestinationSessionStore;
