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
        url: appConfig.DESTINATION_AUTH_TOKEN_ENDPOINT,        
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