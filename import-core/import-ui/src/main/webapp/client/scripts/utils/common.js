'use strict';
var _ = require('lodash/dist/lodash.underscore');

module.exports = {
  getMultilangString: function(multilangFields, field, i18nLib){
	  var language = i18nLib.lng() || 'en';
	  var fieldData = multilangFields[field];
	  var result = fieldData[language];
	  if(result === null || result.length === 0){
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
	  
	  if (field && field.possibleValues) {
		  name = _.find(field.possibleValues, function(value) { 
			  return value.code === code;
		  }).value;		  
	  }
	  
	  return name; 
 }
};