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
      appConfig.DESTINATION_USERNAME = data['user-name'];
      Cookies.set('DESTINATION_USERNAME', data['user-name']);
      // Added true always for now, the API returns wrong value
      Cookies.set('CAN_ADD_ACTIVITY', true || data['add-activity']);
      Cookies.set('IS_ADMIN', data['is-admin']);
      Cookies.set('WORKSPACE', data.team);
 },
 resetAuthCookies: function() {
      appConfig.DESTINATION_USERNAME = null;
      Cookies.set('DESTINATION_USERNAME', null);
      // Added true always for now, the API returns wrong value
      Cookies.set('CAN_ADD_ACTIVITY', null);
      Cookies.set('WORKSPACE', null);
 },

 refreshSession: function() {
   var self = this;
   self.setIntervalTokenId = setInterval(function() {
     self.checkSessionStatus();
   }, Constants.SESSION_VERIFICATION_INTERVAL);
 },

 checkSessionStatus: function() {
   var self = this;
   appActions.refreshDestinationSession.triggerPromise().then(function(data) {
      this.setAuthCookies(data);
      $.get(appConfig.DESTINATION_API_HOST + appConfig.DESTINATION_USER_INFO_ENDPOINT, function(){});
   }.bind(this))['catch'](function(err) {
      this.resetAuthCookies();
   }.bind(this));
 },

 hasValidSession: function() {
	return (appConfig.DESTINATION_USERNAME && appConfig.DESTINATION_USERNAME.length > 0) === true;
 } ,
  getTitle: function (document) {
    let multilangFields = document.multilangFields;
    let language = this.props.i18nLib.lng() || 'en';
    let title = document.multilangFields.title[language];

    if (title == null || title.length == 0) {
      for (let key in multilangFields.title) {
        if (multilangFields.title.hasOwnProperty(key)) {
          title = multilangFields.title[key];
        }
      }
    }
    return title;
  },
  shouldTranslateTitle: function(document){
    if (this.props.i18nLib.lng() !== 'en' && document.multilangFields && document.multilangFields.title[this.props.i18nLib.lng()]) {
      return false;
    }

    return true;
  },
  getTranslation: function(document, value) {
    if (this.props.i18nLib.lng() !== 'en') {
      for (trn of document.translations) {
        if (trn.srcLang === 'en' && trn.dstLang === this.props.i18nLib.lng() && trn.srcText === value) {
          return trn.dstText;
        }
      }
    }
    return null;
  }
};
