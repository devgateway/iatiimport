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
        url: '/mockup/languages.json',        
        error: function() {        	
        	self.trigger({            
                languageData: []
              });
        },
        dataType: 'json',
        success: function(data) {        	
        	self.trigger({            
                languageData: data
              });
        },
        type: 'GET'
     }); 
  }

});

module.exports = LanguageStore;