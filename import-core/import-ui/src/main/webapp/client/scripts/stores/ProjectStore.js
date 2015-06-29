'use strict';

var Reflux = require('reflux');
var request = require('superagent');
var appConfig = require('./../conf');
var appActions = require('./../actions');


var ProjectStore = Reflux.createStore({

  init: function() {
    this.listenTo(appActions.loadProjectData, this.handleLoadProjectData);
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