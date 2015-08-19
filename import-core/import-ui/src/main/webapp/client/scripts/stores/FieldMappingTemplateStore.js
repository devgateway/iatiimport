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
    
  }, 
  handleSaveFieldMappingTemplate: function(data){	
	  console.log('saving template');
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
	      url: '/mockup/field-mappings-templates.json',
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
});

module.exports = FieldMappingTemplateStore;