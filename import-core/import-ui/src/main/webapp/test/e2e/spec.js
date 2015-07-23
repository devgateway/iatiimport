var path = require('path');
describe('IATI Importer App', function() {

  it('File Upload', function() {
    browser.get('http://localhost:9010/#/');     
    element(by.linkText("Import Process")).click();  
    element(by.linkText("IATI 2.01 to AMP")).click();   
    element(by.css('.wizard-steps')).all(by.tagName('li')).get(0).click();  
    var startCount;
    browser.sleep(2000).then(function() {        	
    	element(by.css('.file-list')).all(by.tagName('tr')).count().then(function(count) {
  		  startCount = count;
  		  console.log(startCount);
  		});
     });
    
    browser.sleep(2000).then(function() {           	
        var fileToUpload = 'files/activity-standard-example-minimal7.xml',
        absolutePath = path.resolve(__dirname, fileToUpload);        
        element(by.css('.file')).sendKeys(absolutePath);    
        element(by.css('.fileinput-upload-button')).click();    
        browser.sleep(2000).then(function() {        	
           var count = element(by.css('.file-list')).all(by.tagName('tr')).count();
           expect(count).toBe(startCount+1);
        });
    });
     
    
   
    
   
  });
});