'use strict';

var Reflux = require('reflux');
var request = require('superagent');
var appConfig = require('./../conf');
var formActions = require('./../actions/form');

var DataSourceStore = Reflux.createStore({
  init: function() {
    this.listenTo(formActions.loadReportingOrganizations, this.handleLoadReportingOrganizations);
    this.listenTo(formActions.loadReportingOrgsWithUpdates, this.handleLoadReportingOrgsWithUpdates);
  },
  handleLoadReportingOrganizations: function() {
	    var self = this;
	    $.ajax({
	        url: '/importer/data-source/reporting-orgs',
	        error: function() {
	        	formActions.loadReportingOrganizations.failed();
	        },
	        dataType: 'json',
	        success: function(data) {
	        	formActions.loadReportingOrganizations.completed(data);
	        },
	        type: 'GET'
	     });
	},
	handleLoadReportingOrgsWithUpdates: function() {
		    var self = this;
		    $.ajax({
		        url: '/importer/data-source/reporting-orgs/with-updates',
		        error: function() {
		        	formActions.loadReportingOrgsWithUpdates.failed();
		        },
		        dataType: 'json',
		        success: function(data) {
		        	formActions.loadReportingOrgsWithUpdates.completed(data);
		        },
		        type: 'GET'
		     });
		  }
});

module.exports = DataSourceStore;
