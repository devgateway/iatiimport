'use strict';

var Reflux = require('reflux');
var request = require('superagent');
var appConfig = require('./../conf');
var appActions = require('./../actions');


var SourceValuesStore = Reflux.createStore({

  init: function() {
    this.listenTo(appActions.loadSourceValuesData, this.handleLoadSourceValuesData);
  },

  handleLoadSourceValuesData: function(field) {
    var self = this;    
    var url = '/importer/data/source/field/' + field;
    $.ajax({
        url: url,        
        error: function() {        	
        	self.trigger({            
                sourceValuesData: []
              });
        },
        dataType: 'json',
        success: function(data) {        	
        	self.trigger({            
                sourceValuesData: data
              });
        },
        type: 'GET'
     }); 
  }

});

module.exports = SourceValuesStore;