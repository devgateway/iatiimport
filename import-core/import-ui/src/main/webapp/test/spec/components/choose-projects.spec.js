/**
*   Upload File Spec Test
*/
'use strict';
var ChooseProjects = require('../../../client/scripts/components/wizard/choose-projects');
var React = require("react/addons");
var jasmineReact = require("jasmine-react-helpers");
var projects = require("../../stubs/projects.json");
describe('Choose Projects ', function() {
  var TestUtils = React.addons.TestUtils;
  var instance, i18n, eventHandlers;

  //review how the test setup is done.
  beforeEach(function() { 	 
	  i18n = {t:function(key){}};
	  eventHandlers = {};	  
	  jasmineReact.spyOnClass(ChooseProjects, "selectProject");	  
      instance = jasmineReact.render(<ChooseProjects i18nLib = {i18n} eventHandlers = {eventHandlers} />, document.body);
      instance.setState({
    	  projectData: projects
      }); 
    });
  it('should display project data', function() {	
	  var projects = TestUtils.scryRenderedDOMComponentsWithClass(instance, "table");
	  var columns = TestUtils.scryRenderedDOMComponentsWithTag(projects[0], "td");	
	  expect(columns[0].props.children.type).toBe("input");
	  expect(columns[1].props.children).toBe("Activity title 1");	    
  });
  
  it('should call selectProject on project checkbox change', function() {	  
	   var checks = TestUtils.scryRenderedDOMComponentsWithClass(instance, "source");	   
	   TestUtils.Simulate.change(checks[0]);
	   expect(jasmineReact.classPrototype(ChooseProjects).selectProject).toHaveBeenCalled();	  	    
  });


});
