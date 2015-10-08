'use strict';

var Reflux = require('reflux');
var request = require('superagent');
var appConfig = require('./../conf');
var appActions = require('./../actions');
var formActions = require('./../actions/form');


var ValueMappingTemplateStore = Reflux.createStore({
  init: function() {    
    this.listenTo(formActions.saveValueMappingsTemplate, this.handleSaveValueMappingTemplate);
    this.listenTo(appActions.loadValueMappingsTemplateList, this.handleLoadValueMappingsTemplateList);
    this.listenTo(appActions.loadValueMappingsById, this.handleLoadValueMappingsById);
    this.listenTo(appActions.deleteValueMappingsTemplate, this.handleDeleteValueMappingsTemplate);
    
  }, 
  handleSaveValueMappingTemplate: function(data){	
	  var self = this;
	    $.ajax({
	      headers: { 
	        'Accept': 'application/json',
	        'Content-Type': 'application/json' 
	      },
	      url: '/importer/valuemappingtemplate/save',
	      data: JSON.stringify(data),
	      error: function() {
	    	formActions.saveValueMappingsTemplate.failed();
	      },
	      dataType: 'json',
	      success: function(data) {
	    	 formActions.saveValueMappingsTemplate.completed(data);
	      },
	      type: 'POST'
	    });
  },
  
  handleLoadValueMappingsTemplateList: function(data){	  
	  var self = this;
	    $.ajax({
	      headers: { 
	        'Accept': 'application/json',
	        'Content-Type': 'application/json' 
	      },
	      url: '/importer/valuemappingtemplate/list',
	      data: JSON.stringify(data),
	      error: function() {
	    	  appActions.loadValueMappingsTemplateList.failed();
	      },
	      dataType: 'json',
	      success: function(data) {
	    	  appActions.loadValueMappingsTemplateList.completed(data);
	      },
	      type: 'GET'
	    });
  },
  handleLoadValueMappingsById: function(id){	  
	  var self = this;
	    $.ajax({
	      headers: { 
	        'Accept': 'application/json',
	        'Content-Type': 'application/json' 
	      },
	      url: '/importer/valuemappingtemplate/'+ id,	      
	      error: function() {
	    	  appActions.loadValueMappingsById.failed();
	      },
	      dataType: 'json',
	      success: function(data) {
	    	  appActions.loadValueMappingsById.completed(data);
	      },
	      type: 'GET'
	    });
  },
  handleDeleteValueMappingsTemplate: function(id){	  
	  var self = this;
	    $.ajax({
	      headers: { 
	        'Accept': 'application/json',
	        'Content-Type': 'application/json' 
	      },
	      url: '/importer/valuemappingtemplate/delete/'+ id,	      
	      error: function() {
	    	  appActions.deleteValueMappingsTemplate.failed();
	      },
	      dataType: 'json',
	      success: function(data) {
	    	  appActions.deleteValueMappingsTemplate.completed(data);
	      },
	      type: 'DELETE'
	    });
  }
});

module.exports = ValueMappingTemplateStore;