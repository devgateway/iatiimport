'use strict';

var Reflux = require('reflux');
var request = require('superagent');
var appConfig = require('./../conf');
var appActions = require('./../actions');

var ImportSummaryStore = Reflux.createStore({
  init: function() {
    this.listenTo(appActions.loadImportSummary, this.handleLoadImportSummary);
  },
  handleLoadImportSummary: function() {
    var self = this;    
    $.ajax({
        url: '/importer/import/summary',        
        error: function() {        	
        	appActions.loadImportSummary.failed();
        },
        dataType: 'json',
        success: function(data) {
        	appActions.loadImportSummary.completed(data);
        },
        type: 'GET'
     }); 
  }

});

module.exports = ImportSummaryStore;