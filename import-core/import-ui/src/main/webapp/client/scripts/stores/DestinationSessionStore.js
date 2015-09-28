'use strict';

var Reflux = require('reflux');
var request = require('superagent');
var appConfig = require('./../conf');
var appActions = require('./../actions');


var DestinationSessionStore = Reflux.createStore({

  init: function() {
    this.listenTo(appActions.initDestinationSession, this.handleInitSession);
  },

  handleInitSession: function() {
    var self = this;   
    $.get('/importer/import/wipeall', function(){});    
 
    $.ajax({
        url: appConfig.DESTINATION_API_HOST + appConfig.DESTINATION_AUTH_TOKEN_ENDPOINT,
        timeout:3000,
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

module.exports = DestinationSessionStore;
