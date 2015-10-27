'use strict';

var Reflux = require('reflux');
var request = require('superagent');
var appConfig = require('./../conf');
var appActions = require('./../actions');
var formActions = require('./../actions/form');


var LanguageStore = Reflux.createStore({

  init: function() {
    this.listenTo(appActions.loadLanguageData, this.handleLoadLanguageData);
    this.listenTo(formActions.updateLanguages, this.handleUpdateLanguages);
    
  },
  
  handleUpdateLanguages: function(data) {	   
	    var self = this;
	    $.ajax({
	      headers: { 
	        'Accept': 'application/json',
	        'Content-Type': 'application/json' 
	      },
	      url: '/importer/data/source/languages',
	      data: JSON.stringify(data),
	      error: function() {    	  
	    	  formActions.updateLanguages.failed();        
	      },
	      dataType: 'json',
	      success: function(data) {
	    	  formActions.updateLanguages.completed(data);        
	      },
	      type: 'POST'
	    });
	  },

  handleLoadLanguageData: function() {
    var self = this;    
    $.ajax({
        url: '/importer/data/source/languages',        
        error: function() {
        	appActions.loadLanguageData.failed();        	
        },
        dataType: 'json',
        success: function(data) {  
        	appActions.loadLanguageData.completed(data);       	
        },
        type: 'GET'
     }); 
  }

});

module.exports = LanguageStore;