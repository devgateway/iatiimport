'use strict';

var Reflux = require('reflux');
var request = require('superagent');
var appConfig = require('./../conf');
var appActions = require('./../actions');
var formActions = require('./../actions/form');


var FieldMappingStore = Reflux.createStore({

  init: function() {
    this.listenTo(appActions.loadMappingFieldsData, this.handleLoadMappingFieldsData);
    this.listenTo(formActions.updateSelectedFields, this.handleUpdateSelectedFields);
  },
  handleUpdateSelectedFields: function(data) {
    var self = this;
    $.ajax({
      headers: { 
        'Accept': 'application/json',
        'Content-Type': 'application/json' 
      },
      url: '/importer/data/source/field/mapping',
      data: JSON.stringify(data),
      error: function() {
        formActions.updateSelectedFields.failed();
      },
      dataType: 'json',
      success: function(data) {
        formActions.updateSelectedFields.completed(data);
      },
      type: 'POST'
    });
  },
  handleLoadMappingFieldsData: function() {
    var self = this;    
    $.ajax({
        url: '/importer/data/source/field/mapping',        
        error: function() {        	
        	appActions.loadMappingFieldsData.failed();
        },
        dataType: 'json',
        success: function(data) {        	
        	appActions.loadMappingFieldsData.completed(data);
        },
        type: 'GET'
     }); 
  }

});

module.exports = FieldMappingStore;