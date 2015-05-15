'use strict';

var Reflux = require('reflux');
var request = require('superagent');
var appConfig = require('./../conf');
var appActions = require('./../actions');


var RecipientStore = Reflux.createStore({

  init: function() {
    this.listenTo(appActions.loadRecipientData, this.handleLoadRecipientData);
  },

  handleLoadRecipientData: function() {
    var self = this;    
    $.ajax({
        url: '/mockup/recipients.json',        
        error: function() {        	
        	self.trigger({            
                recipientData: []
              });
        },
        dataType: 'json',
        success: function(data) {        	
        	self.trigger({            
                recipientData: data
              });
        },
        type: 'GET'
     }); 
  }

});

module.exports = RecipientStore;