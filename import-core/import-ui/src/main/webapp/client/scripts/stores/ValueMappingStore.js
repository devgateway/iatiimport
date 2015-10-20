'use strict';

var Reflux = require('reflux');
var request = require('superagent');
var appConfig = require('./../conf');
var appActions = require('./../actions');
var formActions = require('./../actions/form');


var ValueMappingStore = Reflux.createStore({

  init: function() {
    this.listenTo(appActions.loadValueMappingData, this.handleLoadValueMappingData);
    this.listenTo(formActions.updateSelectedValues, this.handleUpdateSelectedValues);
  },
  handleUpdateSelectedValues: function(data) {
    var self = this;
    $.ajax({
    	headers: { 
    		'Accept': 'application/json',
    		'Content-Type': 'application/json' 
    	},
    	url: '/importer/data/source/field/valuemapping',
    	data: JSON.stringify(data),
    	error: function() {    	    	  
    		formActions.updateSelectedValues.failed({
    			mappingValuesData: []
    		});

    	},
    	dataType: 'json',
    	success: function(data) {
    		formActions.updateSelectedValues.completed({
    			mappingValuesData: data
    		});    	        
    	},
    	type: 'POST'
    }); 
  },
  handleLoadValueMappingData: function() {
    var self = this;    
    $.ajax({
        url: '/importer/data/source/field/valuemapping',        
        error: function() {        	
        	appActions.loadValueMappingData.failed();
        },
        dataType: 'json',
        success: function(data) {        	
        	appActions.loadValueMappingData.completed(data);
        },
        type: 'GET'
     }); 
  }
});

module.exports = ValueMappingStore;