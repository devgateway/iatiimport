'use strict';

var Reflux = require('reflux');
var request = require('superagent');
var appConfig = require('./../conf');
var appActions = require('./../actions');


var SourceFieldsStore = Reflux.createStore({

  init: function() {
    this.listenTo(appActions.loadSourceFieldsData, this.handleLoadSourceFieldsData);
  },

  handleLoadSourceFieldsData: function() {
    var self = this;    
    $.ajax({
        url: '/importer/data/source/field',        
        error: function() {        	
        	appActions.loadSourceFieldsData.failed();
        },
        dataType: 'json',
        success: function(data) {        	
        	appActions.loadSourceFieldsData.completed(data);
        },
        type: 'GET'
     }); 
  }

});

module.exports = SourceFieldsStore;