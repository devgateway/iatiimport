'use strict';
var _ = require('lodash/dist/lodash.underscore');
var moment = require('moment');
var Cookies = require('js-cookie');
var appConfig = require('./../conf');
var appActions = require('./../actions');

var Constants = require('./constants');

module.exports = {
  getMultilangString: function(multilangFields, field, i18nLib){
	  var language = i18nLib.lng();
	  var fieldData = multilangFields[field];
	  var result = fieldData[language] ? fieldData[language] : null;
	  
	  if (result === null){
		  for (var i = 0; i < Constants.LANGUAGE_PREFERENCE.length; i++) {
				 var langKey = Constants.LANGUAGE_PREFERENCE[i];
				 result = fieldData[langKey];
				 if (result) {
					 return result;
				 }				  
			 }
	  }	  
 
	 return result;
  },
  getDisplayValue: function(item, language) {
    var displayValue = item.displayName;
    if (item.multiLangDisplayName && item.multiLangDisplayName[language]) {
      displayValue = item.multiLangDisplayName[language];
    }
    return displayValue;
  },
  getFieldDisplayName: function(fieldData, fieldName) {
	  var displayName = '';
	  var field = _.find(fieldData, function(sourceField) { 
		  return sourceField.uniqueFieldName === fieldName;
	  });
	  
	  if (field) {
		  displayName = field.displayName; 
	  }
	  
	  return displayName;
 },
 getValueName: function(fieldData, fieldName, code) {
	  var name = '';
	  var field = _.find(fieldData, function(sourceField) { 
		  return sourceField.uniqueFieldName === fieldName;
	  });
	  
	  if (field && field.possibleValues && field.possibleValues.length > 0) {
		  var foundItem = _.find(field.possibleValues, function(value) { 
			  return value.code === code;
		  });
		  if (foundItem) {
			  name = foundItem.value;
		  }		 		  
	  }
	  
	  return name; 
 },
 formatNumber: function(num){
	    if (num) {	    	
	    	var n = parseFloat(num).toFixed(2).toString(), p = n.indexOf('.');
	 	    return n.replace(/\d(?=(?:\d{3})+(?:\.|$))/g, function($0, i){
	 	        return p<0 || i<p ? ($0+',') : $0;
	 	    });	 	
	 	}
 },
 formatDate: function(date) {
	 if (date) {
		 return moment(date).format('YYYY-MM-DD');
	 }
	 
 },
 isAdmin: function() {
	 return Cookies.get('IS_ADMIN') === 'true' || Cookies.get('IS_ADMIN') === true;
 },
 setAuthCookies: function(data) {
	  appConfig.DESTINATION_AUTH_TOKEN = data.token;
      appConfig.DESTINATION_USERNAME = data['user-name'];
      appConfig.DESTINATION_AUTH_TOKEN_EXPIRATION =  data['token-expiration'] || (new Date()).getTime() + (30*60*1000);
      Cookies.set('DESTINATION_AUTH_TOKEN', data.token);
      Cookies.set('DESTINATION_USERNAME', data['user-name']);
      // Added true always for now, the API returns wrong value
      Cookies.set('CAN_ADD_ACTIVITY', true || data['add-activity']);
      Cookies.set('IS_ADMIN', data['is-admin']);
      Cookies.set('WORKSPACE', data.team);      
 },
 resetAuthCookies: function() {
	  appConfig.DESTINATION_AUTH_TOKEN = null;
      appConfig.DESTINATION_USERNAME = null;
      Cookies.set('DESTINATION_AUTH_TOKEN', null);
      Cookies.set('DESTINATION_USERNAME', null);
      // Added true always for now, the API returns wrong value
      Cookies.set('CAN_ADD_ACTIVITY', null);
      Cookies.set('WORKSPACE', null);
      appConfig.DESTINATION_AUTH_TOKEN_EXPIRATION = null;
 },
  //the token expires in 30 minutes, so 2 minutes check is more than enough
 refreshToken: function() {
	 var self = this;
	 self.setIntervalTokenId = setInterval(function(){
		 self.checkTokenStatus();
	}, 12000);
 },
 checkTokenStatus: function() {
		var currentTime = (new Date()).getTime();
		var expirationTime = appConfig.DESTINATION_AUTH_TOKEN_EXPIRATION;
		var secondsToExpire = (appConfig.DESTINATION_AUTH_TOKEN_EXPIRATION - currentTime)/1000;
		
		if (secondsToExpire < 0) {
			appActions.refreshDestinationSession.triggerPromise().then(function(data) {
			      this.setAuthCookies(data);			    
			  $.get(appConfig.TOOL_REST_PATH + '/refresh/' + data.token, function(){});
		      }.bind(this))['catch'](function(err) {
		    	  this.resetAuthCookies();
		      }.bind(this));

		}
 },
 hasValidSession: function() {
	return Cookies.get('DESTINATION_USERNAME') && appConfig.DESTINATION_AUTH_TOKEN_EXPIRATION > (new Date()).getTime();
 } 
};
