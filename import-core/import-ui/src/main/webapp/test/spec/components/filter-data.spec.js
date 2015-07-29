/**
*   Upload File Spec Test
*/
'use strict';

var FilterData = require('../../../client/scripts/components/wizard/filter-data');
var React = require("react/addons");
var jasmineReact = require("jasmine-react-helpers");
var filters = require("../../stubs/filters.json");
var mocks;
describe('Filter Data ', function() {
  var TestUtils = React.addons.TestUtils;
  var instance, i18n,eventHandlers;

  //review how the test setup is done.
  beforeEach(function() {  
	  	  
	  jasmineReact.spyOnClass(FilterData, "handleToggle");
	  jasmineReact.spyOnClass(FilterData, "handleNext");
	  i18n = {t:function(key){}};
	  eventHandlers = {};
      instance = jasmineReact.render(<FilterData i18nLib = {i18n} eventHandlers = {eventHandlers} />, document.body);
      instance.setState({
    	  filterData: filters
      });
      
      
    });
  it('should display all filters in the data', function() {	
	var groups = TestUtils.scryRenderedDOMComponentsWithClass(instance, "filter-group"); 
    var groupLabels = TestUtils.scryRenderedDOMComponentsWithClass(instance, "filter-group-title"); 
    var options = TestUtils.scryRenderedDOMComponentsWithClass(groups[0], "input-group");
    expect(groupLabels.length).toBe(2);
    expect(options.length).toBe(6)
  });
  
  it('should call handleToggle on checkbox change', function() {	    
		var groups = TestUtils.scryRenderedDOMComponentsWithClass(instance, "filter-group"); 
	    var options = TestUtils.scryRenderedDOMComponentsWithClass(groups[0], "value-select");	    
	    TestUtils.Simulate.change(options[0]);
	    expect(jasmineReact.classPrototype(FilterData).handleToggle).toHaveBeenCalled();
	    
	  });
  
  it('should call handleNext on next button click', function() {	    
		var btn = TestUtils.scryRenderedDOMComponentsWithClass(instance, "btn-next");
		TestUtils.Simulate.click(btn[0]);
	    expect(jasmineReact.classPrototype(FilterData).handleNext).toHaveBeenCalled();	    
	  });

});
