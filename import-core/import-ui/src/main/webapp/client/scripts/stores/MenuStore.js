'use strict';

var Reflux = require('reflux');
var request = require('superagent');
var appConfig = require('./../conf');
var appActions = require('./../actions');


var MenuStore = Reflux.createStore({

  init: function() {
    this.listenTo(appActions.loadMenuData, this.handleLoadMenuData);
  },

  handleLoadMenuData: function() {
    var self = this;    
    $.ajax({
        url: '/mockup/menu_items.json',        
        error: function() {        	
        	self.trigger({            
                menuData: []
              });
        },
        dataType: 'json',
        success: function(data) {        	
        	self.trigger({            
                menuData: data
              });
        },
        type: 'GET'
     }); 
  }

});

module.exports = MenuStore;