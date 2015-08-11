'use strict';

var Reflux = require('reflux');
var request = require('superagent');
var appConfig = require('./../conf');
var appActions = require('./../actions');
var formActions = require('./../actions/form');


var ImportLogStore = Reflux.createStore({

  init: function() {
    this.listenTo(appActions.loadImportLog, this.handleLoadImportLog);    
  },
  handleLoadImportLog: function() {
    var self = this;    
    $.ajax({
        url: '/mockup/import_log.json',        
        error: function() {        	
        	appActions.loadImportLog.failed();
        },
        dataType: 'json',
        success: function(data) {  
        	appActions.loadImportLog.completed(data);        	
        },
        type: 'GET'
     }); 
  }

});

module.exports = ImportLogStore;