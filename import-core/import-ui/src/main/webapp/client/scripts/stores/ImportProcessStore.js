'use strict';

var Reflux = require('reflux');
var request = require('superagent');
var appConfig = require('./../conf');
var appActions = require('./../actions');

var ImportProcessStore = Reflux.createStore({

  init: function() {
    this.listenTo(appActions.loadImportProcessData, this.handleLoadImportProcessData);
  },
  handleLoadImportProcessData: function() {
    var self = this;    
    $.ajax({
        url: 'importer/process/list',        
        error: function() {
        	appActions.loadImportProcessData.failed();
        },
        dataType: 'json',
        success: function(data) {
        	appActions.loadImportProcessData.completed(data);
        },
        type: 'GET'
     }); 
  }

});

module.exports = ImportProcessStore;