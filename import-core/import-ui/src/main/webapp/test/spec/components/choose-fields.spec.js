/**
*   Upload File Spec Test
*/
'use strict';
var ChooseFields = require('../../../client/scripts/components/wizard/choose-fields');
var React = require("react/addons");
var jasmineReact = require("jasmine-react-helpers");
var sourceFields = require("../../stubs/source_fields.json");
var destinationFields = require("../../stubs/destination_fields.json");
describe('Choose Fields ', function() {
  var TestUtils = React.addons.TestUtils;
  var instance, i18n, eventHandlers;

  //review how the test setup is done.
  beforeEach(function() { 	 
	  i18n = {t:function(key){}};
	  eventHandlers = {updateCurrentStep:function(step){		  
	  },
	  showLoadingIcon:function(){		  
	  },
	  hideLoadingIcon: function(){		  
	  }
	  };	
	  
	  jasmineReact.spyOnClass(ChooseFields, "handleToggle");	  
      instance = jasmineReact.render(<ChooseFields i18nLib = {i18n} eventHandlers = {eventHandlers} />, document.body);
      instance.setState({
          destinationFieldsData: destinationFields
      });
      instance.setState({
    	  sourceFieldsData: sourceFields
      });
    });
  it('should display source fields on list', function() {	
	  var table = TestUtils.scryRenderedDOMComponentsWithClass(instance, "table");
	   var columns = TestUtils.scryRenderedDOMComponentsWithTag(table[0], "td");	
	   expect(columns[3].props.children.type).toBe("input");
	   expect(columns[4].props.children.type).toBe("div");  
	 //  expect(columns[0].props.children).toBe("String Fields");
	   	   
	  
  });
  
  it('should call selectFieldMapping on checkbox change', function() {
	  var checkboxes = TestUtils.scryRenderedDOMComponentsWithClass(instance, "source-selector");
	  TestUtils.Simulate.change(checkboxes[0]);
	  expect(jasmineReact.classPrototype(ChooseFields).handleToggle).toHaveBeenCalled();  
  });


});
