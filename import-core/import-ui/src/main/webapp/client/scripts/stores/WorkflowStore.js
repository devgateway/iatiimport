'use strict';

var Reflux = require('reflux');
var request = require('superagent');
var appConfig = require('./../conf');
var appActions = require('./../actions');

var WorkflowStore = Reflux.createStore({

  init: function() {
    this.listenTo(appActions.loadWorkflowData, this.handleLoadWorkflowData);
  },
  handleLoadWorkflowData: function() {
    var self = this;    
    $.ajax({
        url: '/importer/workflow/list',        
        error: function() {
        	appActions.loadWorkflowData.failed();
        },
        dataType: 'json',
        success: function(data) {
        	appActions.loadWorkflowData.completed(data);
        },
        type: 'GET'
     }); 
  }

});

module.exports = WorkflowStore;