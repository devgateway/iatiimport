'use strict';

var Reflux = require('reflux');
var request = require('superagent');
var appConfig = require('./../conf');
var appActions = require('./../actions');


var SessionStore = Reflux.createStore({

  init: function() {
    this.listenTo(appActions.initSession, this.handleInitSession);
  },

  handleInitSession: function() {
    var self = this;    
    $.ajax({
        url: appConfig.DESTINATION_USER_INFO_ENDPOINT,
        timeout: appConfig.REQUEST_TIMEOUT,
        error: function() {
          self.trigger({            
                sessionData: []
              });
        },
        dataType: 'json',
        success: function(data) {
          self.trigger({            
                sessionData: data
              });
        },
        type: 'GET'
     }); 
  }

});

module.exports = SessionStore;
