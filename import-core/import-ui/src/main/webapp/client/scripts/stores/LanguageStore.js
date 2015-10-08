'use strict';

var Reflux = require('reflux');
var request = require('superagent');
var appConfig = require('./../conf');
var appActions = require('./../actions');


var LanguageStore = Reflux.createStore({

  init: function() {
    this.listenTo(appActions.loadLanguageData, this.handleLoadLanguageData);
  },

  handleLoadLanguageData: function() {
    var self = this;    
    $.ajax({
        url: '/importer/data/source/languages',        
        error: function() {
        	appActions.loadLanguageData.failed();        	
        },
        dataType: 'json',
        success: function(data) {  
        	appActions.loadLanguageData.completed(data);       	
        },
        type: 'GET'
     }); 
  }

});

module.exports = LanguageStore;