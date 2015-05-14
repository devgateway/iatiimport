'use strict';

var Reflux = require('reflux');
var request = require('superagent');
var appConfig = require('./../conf');
var appActions = require('./../actions');


var DestinationFieldsStore = Reflux.createStore({

  init: function() {
    this.listenTo(appActions.loadDestinationFieldsData, this.handleLoadDestinationFieldsData);
  },

  handleLoadDestinationFieldsData: function() {
    var self = this;    
    $.ajax({
        url: '/mockup/destination_fields.json',        
        error: function() {        	
        	self.trigger({            
                destinationFieldsData: []
              });
        },
        dataType: 'json',
        success: function(data) {        	
        	self.trigger({            
                destinationFieldsData: data
              });
        },
        type: 'GET'
     }); 
  }

});

module.exports = DestinationFieldsStore;