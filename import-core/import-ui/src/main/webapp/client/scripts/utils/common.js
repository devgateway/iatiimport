'use strict';
var _ = require('lodash/dist/lodash.underscore');
var moment = require('moment');
var Cookies = require('js-cookie');

module.exports = {
  getMultilangString: function(multilangFields, field, i18nLib){
	  var language = i18nLib.lng() || 'en';
	  var fieldData = multilangFields[field];
	  var result = fieldData[language] ? fieldData[language] : null;
	  if(result === null){
	     for (var key in multilangFields[result]) {
           if (multilangFields[field].hasOwnProperty(key)) {
              if(result === null || result.length === 0){
            	  result = multilangFields[field][key];
              }
           }
         }
	 }
	  
	 return result;
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
 }
 
};