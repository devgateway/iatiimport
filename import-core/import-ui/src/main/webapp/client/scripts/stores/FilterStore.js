'use strict';

var Reflux = require('reflux');
var request = require('superagent');
var appConfig = require('./../conf');
var appActions = require('./../actions');
var formActions = require('./../actions/form');
var _ = require('lodash/dist/lodash.underscore');


function getItemByKey(list, itemKey) {
  return _.find(list, function(item) {
    return item.key === itemKey;
  });
}

var FilterStore = Reflux.createStore({
  init: function() {
    this.listenTo(appActions.loadFilterData, this.handleLoadFilterData);
    this.listenTo(formActions.updateFilters, this.handleUpdateFilters);
  },
  handleUpdateFilters: function(data) {
    var self = this;
    $.ajax({
      headers: { 
        'Accept': 'application/json',
        'Content-Type': 'application/json' 
      },
      url: '/importer/data/source/filters',
      data: JSON.stringify(data),
      error: function() {
        self.trigger({
          filterData: []
        });
      },
      dataType: 'json',
      success: function(data) {
        self.trigger({
          filterData: data
        });
      },
      type: 'POST'
    });
  },
  handleLoadFilterData: function() {
    var self = this;
    $.ajax({
      url: '/importer/data/source/filters',
      error: function() {
        self.trigger({
          filterData: []
        });
      },
      dataType: 'json',
      success: function(data) {
        self.trigger({
          filterData: data
        });
      },
      type: 'GET'
    });
  }

});

module.exports = FilterStore;