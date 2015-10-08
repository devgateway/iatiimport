'use strict';

var Reflux = require('reflux');
var request = require('superagent');
var appConfig = require('./../conf');
var appActions = require('./../actions');


var DestinationValuesStore = Reflux.createStore({

  init: function() {
    this.listenTo(appActions.loadDestinationValuesData, this.handleLoadDestinationValuesData);
  },

  handleLoadDestinationValuesData: function(field) {
    var self = this;    
    $.ajax({
        url: '/mockup/destination_activity_status_possiblevalues.json',        
        error: function() {        	
        	self.trigger({            
                destinationValuesData: []
              });
        },
        dataType: 'json',
        success: function(data) {        	
        	self.trigger({            
                destinationValuesData: data
              });
        },
        type: 'GET'
     }); 
  }

});

module.exports = DestinationValuesStore;