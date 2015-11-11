'use strict';

var Reflux = require('reflux');
var request = require('superagent');
var appConfig = require('./../conf');
var appActions = require('./../actions');
var formActions = require('./../actions/form');



var ProjectStore = Reflux.createStore({

  init: function() {
    this.listenTo(appActions.loadProjectData, this.handleLoadProjectData);
    this.listenTo(appActions.initializeMapping, this.handleInitializeMapping);    
    this.listenTo(formActions.updateSelectedProjects, this.handleUpdateSelectedProjects);
  },
  handleUpdateSelectedProjects: function(data) {
    var self = this;
    $.ajax({
      headers: { 
        'Accept': 'application/json',
        'Content-Type': 'application/json' 
      },
      async: true,      
      url: '/importer/data/source/project',
      data: JSON.stringify(data),
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
      type: 'POST'
    });
  },

  handleLoadProjectData: function() {
    var self = this;    
    $.ajax({
        url: '/importer/import/projects',        
        error: function() {        	
        	appActions.loadProjectData.failed();
        },
        dataType: 'json',
        success: function(data) {  
        	appActions.loadProjectData.completed(data);        	
        },
        type: 'GET'
     }); 
  },

  handleInitializeMapping: function() {
	    var self = this;    
	    $.ajax({
	        url: '/importer/import/initialize',        
	        error: function() {        	
	        	appActions.initializeMapping.failed();
	        },
	        dataType: 'json',
	        success: function(data) {  
	        	appActions.initializeMapping.completed(data);        	
	        },
	        type: 'POST'
	     }); 
	  }
  
});

module.exports = ProjectStore;