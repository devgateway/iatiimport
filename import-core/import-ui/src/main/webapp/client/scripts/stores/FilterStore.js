'use strict';

var Reflux = require('reflux');
var request = require('superagent');
var appConfig = require('./../conf');
var appActions = require('./../actions');


var FilterStore = Reflux.createStore({

  init: function() {
    this.listenTo(appActions.loadFilterData, this.handleLoadFilterData);
  },

  handleLoadFilterData: function() {
    var self = this;    
    $.ajax({
        url: '/importer/data/source/filters',        
        error: function() {        	
        	self.trigger({            
                filterData: []
              });
        },
        dataType: 'json',
        success: function(data) {        	
        	self.trigger({            
                filterData: data
              });
        },
        type: 'GET'
     }); 
  }

});

module.exports = FilterStore;