'use strict';

var Reflux = require('reflux');
var request = require('superagent');
var appConfig = require('./../conf');
var appActions = require('./../actions');


var SourceFieldsStore = Reflux.createStore({

  init: function() {
    this.listenTo(appActions.loadSourceFieldsData, this.handleLoadSourceFieldsData);
  },

  handleLoadSourceFieldsData: function() {
    var self = this;    
    $.ajax({
        url: '/mockup/source_fields.json',        
        error: function() {        	
        	self.trigger({            
                sourceFieldsData: []
              });
        },
        dataType: 'json',
        success: function(data) {        	
        	self.trigger({            
                sourceFieldsData: data
              });
        },
        type: 'GET'
     }); 
  }

});

module.exports = SourceFieldsStore;