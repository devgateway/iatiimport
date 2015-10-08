'use strict';

var Reflux = require('reflux');
var request = require('superagent');
var appConfig = require('./../conf');
var appActions = require('./../actions');
var formActions = require('./../actions/form');


var FieldMappingTemplateStore = Reflux.createStore({
  init: function() {    
    this.listenTo(formActions.saveFieldMappingsTemplate, this.handleSaveFieldMappingTemplate);
    this.listenTo(appActions.loadFieldMappingsTemplateList, this.handleLoadFieldMappingsTemplateList);
    this.listenTo(appActions.loadFieldMappingsById, this.handleLoadFieldMappingsById);
    this.listenTo(appActions.deleteMappingTemplate, this.handleDeleteMappingTemplate);
    
  }, 
  handleSaveFieldMappingTemplate: function(data){	
	  var self = this;
	    $.ajax({
	      headers: { 
	        'Accept': 'application/json',
	        'Content-Type': 'application/json' 
	      },
	      url: '/importer/fieldmappingtemplate/save',
	      data: JSON.stringify(data),
	      error: function() {
	    	formActions.saveFieldMappingsTemplate.failed();
	      },
	      dataType: 'json',
	      success: function(data) {
	    	 formActions.saveFieldMappingsTemplate.completed(data);
	      },
	      type: 'POST'
	    });
  },
  
  handleLoadFieldMappingsTemplateList: function(data){	  
	  var self = this;
	    $.ajax({
	      headers: { 
	        'Accept': 'application/json',
	        'Content-Type': 'application/json' 
	      },
	      url: '/importer/fieldmappingtemplate/list',
	      data: JSON.stringify(data),
	      error: function() {
	    	  appActions.loadFieldMappingsTemplateList.failed();
	      },
	      dataType: 'json',
	      success: function(data) {
	    	  appActions.loadFieldMappingsTemplateList.completed(data);
	      },
	      type: 'GET'
	    });
  },
  handleLoadFieldMappingsById: function(id){	  
	  var self = this;
	    $.ajax({
	      headers: { 
	        'Accept': 'application/json',
	        'Content-Type': 'application/json' 
	      },
	      url: '/importer/fieldmappingtemplate/'+ id,	      
	      error: function() {
	    	  appActions.loadFieldMappingsById.failed();
	      },
	      dataType: 'json',
	      success: function(data) {
	    	  appActions.loadFieldMappingsById.completed(data);
	      },
	      type: 'GET'
	    });
  },
  handleDeleteMappingTemplate: function(id){	  
	  var self = this;
	    $.ajax({
	      headers: { 
	        'Accept': 'application/json',
	        'Content-Type': 'application/json' 
	      },
	      url: '/importer/fieldmappingtemplate/delete/'+ id,	      
	      error: function() {
	    	  appActions.deleteMappingTemplate.failed();
	      },
	      dataType: 'json',
	      success: function(data) {
	    	  appActions.deleteMappingTemplate.completed(data);
	      },
	      type: 'DELETE'
	    });
  }
});

module.exports = FieldMappingTemplateStore;