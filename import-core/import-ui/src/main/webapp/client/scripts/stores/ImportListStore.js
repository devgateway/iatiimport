'use strict';

var Reflux = require('reflux');
var request = require('superagent');
var appConfig = require('./../conf');
var appActions = require('./../actions');

var ImportListStore = Reflux.createStore({
  init: function() {
    this.listenTo(appActions.loadImportListData, this.handleLoadImportListData);    
  },
  handleLoadImportListData: function(data) {
    var self = this;    
    $.ajax({
    	url: '/importer/reports/previousimports?size='+ data.size + '&page=' + data.page,    	
        error: function() {        	
        	appActions.loadImportListData.failed();
        },
        dataType: 'json',
        success: function(data) {  
        	appActions.loadImportListData.completed(data);        	
        },
        type: 'GET'
     }); 
  }

});

module.exports = ImportListStore;