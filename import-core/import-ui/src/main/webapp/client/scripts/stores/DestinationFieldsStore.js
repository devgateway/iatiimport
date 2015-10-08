'use strict';

var Reflux = require('reflux');
var request = require('superagent');
var appConfig = require('./../conf');
var appActions = require('./../actions');


var DestinationFieldsStore = Reflux.createStore({
  init: function() {
    this.listenTo(appActions.loadDestinationFieldsData, this.handleLoadDestinationFieldsData);
  },
  handleLoadDestinationFieldsData: function() {
    var self = this;    
    $.ajax({
        url: '/importer/data/destination/field',        
        error: function() {  
        	appActions.loadDestinationFieldsData.failed();       	
        },
        dataType: 'json',
        success: function(data) {
        	appActions.loadDestinationFieldsData.completed(data);        	
        },
        type: 'GET'
     }); 
  }
});

module.exports = DestinationFieldsStore;