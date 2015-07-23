'use strict';

var Reflux = require('reflux');
var request = require('superagent');
var appConfig = require('./../conf');
var appActions = require('./../actions');
var formActions = require('./../actions/form');



var ProjectStore = Reflux.createStore({

  init: function() {
    this.listenTo(appActions.loadProjectData, this.handleLoadProjectData);
    this.listenTo(formActions.updateSelectedProjects, this.handleUpdateSelectedProjects);
  },
  handleUpdateSelectedProjects: function(data) {
    var self = this;
    $.ajax({
      headers: { 
        'Accept': 'application/json',
        'Content-Type': 'application/json' 
      },
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
        	self.trigger({            
                projectData: []
              });
        },
        dataType: 'json',
        success: function(data) {        	
        	self.trigger({            
                projectData: data
              });
        },
        type: 'GET'
     }); 
  }

});

module.exports = ProjectStore;