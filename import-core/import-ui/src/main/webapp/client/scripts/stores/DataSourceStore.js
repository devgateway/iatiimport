'use strict';

var Reflux = require('reflux');
var request = require('superagent');
var appConfig = require('./../conf');
var formActions = require('./../actions/form');


var DataSourceStore = Reflux.createStore({

  init: function() {
    this.listenTo(formActions.loadDataSource, this.handleLoadDataSource);
    this.listenTo(formActions.updateDataSource, this.handleUpdateDataSource);
    this.listenTo(formActions.loadReportingOrganizations, this.handleLoadReportingOrganizations);
  },
  handleUpdateDataSource: function(data) {
    var self = this;
    $.ajax({
    	headers: { 
    		'Accept': 'application/json',
    		'Content-Type': 'application/json' 
    	},
    	url: '/importer/data-source',
    	data: JSON.stringify(data),
    	error: function() {    	    	  
    		formActions.updateDataSource.failed(data);
    	},
    	dataType: 'json',
    	success: function(data) {
    		formActions.updateDataSource.completed(data);    	        
    	},
    	type: 'POST'
    }); 
  },
  handleLoadDataSource: function() {
    var self = this;    
    $.ajax({
        url: '/importer/data-source',        
        error: function() {        	
        	formActions.loadDataSource.failed();
        },
        dataType: 'json',
        success: function(data) {        	
        	formActions.loadDataSource.completed(data);
        },
        type: 'GET'
     }); 
  },
  handleLoadReportingOrganizations: function() {
	    var self = this;    
	    $.ajax({
	        url: '/importer/data-source/reporting-orgs',        
	        error: function() {        	
	        	formActions.loadReportingOrganizations.failed();
	        },
	        dataType: 'json',
	        success: function(data) {        	
	        	formActions.loadReportingOrganizations.completed(data);
	        },
	        type: 'GET'
	     }); 
	  }
});

module.exports = DataSourceStore;