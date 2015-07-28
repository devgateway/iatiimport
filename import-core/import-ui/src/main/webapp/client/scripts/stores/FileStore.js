'use strict';

var Reflux = require('reflux');
var request = require('superagent');
var appConfig = require('./../conf');
var appActions = require('./../actions');


var FileStore = Reflux.createStore({

  init: function() {
    this.listenTo(appActions.loadFileData, this.handleLoadFileData);
  },

  handleLoadFileData: function() {
    var self = this;    
    $.ajax({
        url: '/importer/import/uploaded',        
        error: function() {        	
        	appActions.loadFileData.failed();
        },
        dataType: 'json',
        success: function(data) {        	
        	appActions.loadFileData.completed(data);
        },
        type: 'GET'
     }); 
  }

});

module.exports = FileStore;