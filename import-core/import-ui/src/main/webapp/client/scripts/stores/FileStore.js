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
        url: '/mockup/uploaded_files.json',        
        error: function() {        	
        	self.trigger({            
                fileData: []
              });
        },
        dataType: 'json',
        success: function(data) {        	
        	self.trigger({            
        		fileData: data
              });
        },
        type: 'GET'
     }); 
  }

});

module.exports = FileStore;