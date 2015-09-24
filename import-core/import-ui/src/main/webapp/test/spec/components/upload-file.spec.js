/**
*   Upload File Spec Test
*/
'use strict';

var UploadFile = require('../../../client/scripts/components/wizard/upload-file');
var React = require("react/addons");
var stubFiles = require("../../stubs/uploaded_files.json");

describe('Upload File ', function() {
  var TestUtils = React.addons.TestUtils;
  var instance,i18n,eventHandlers;
  beforeEach(function() {   
	  i18n = {t:function(key){},lng:function(){}};
	  eventHandlers = {updateCurrentStep:function(step){		  
	  },
	  showLoadingIcon:function(){		  
	  },
	  hideLoadingIcon: function(){		  
	  }
	  };	
      instance = TestUtils.renderIntoDocument(<UploadFile i18nLib = {i18n} eventHandlers = {eventHandlers} />);
      instance.setState({
          fileData: stubFiles
      });
    });
  it('should display a file input element', function() {  
    var fileInput = TestUtils.scryRenderedDOMComponentsWithClass(instance, "file");
    expect(fileInput.length).toBe(1);
  });
  
  it('should display a list of uploaded files', function() {	    
	    var fileInput = TestUtils.scryRenderedDOMComponentsWithClass(instance, "table");
	    var columns = TestUtils.scryRenderedDOMComponentsWithTag(instance, "td");	   
	    expect(columns[0].props.children).toBe("activity-standard-example-minimal.xml");    
	  });

});
