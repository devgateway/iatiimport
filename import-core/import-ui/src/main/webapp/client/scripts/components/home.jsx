var React = require('react');
var Reflux = require('reflux');
var Header = require('./layout/header');
var Footer = require('./layout/footer');
var Content = require('./content');
var Router = require('react-router');
var RouteHandler = Router.RouteHandler;
var appActions = require('./../actions');
var translations = require('../../i18n/translations');
var systemInfoStore = require('./../stores/SystemInfoStore');
var Home = React.createClass({
   mixins: [Reflux.ListenerMixin],
   getInitialState: function() {                    
         i18n.init({ resStore:translations.resources ,fallbackLng: 'en',load: 'unspecific'});
         return {
             i18nLib: i18n,
             systemInfo:{status:"UNKNOWN"}           
         }
   },
  componentDidMount: function() {
     this.listenTo(systemInfoStore, this.updateSystemInfo); 
     appActions.checkBackendStatus.triggerPromise().then(function(data) {                             
	    this.updateSystemInfo(data);	    
      }.bind(this)).catch(function(err) {
        this.updateSystemInfo({status:"DOWN"});	
      }.bind(this)); 
  },
  updateSystemInfo: function(data){
     this.setState({
        systemInfo: data
     });       
      
  },
  switchLanguage: function(language){      
    var i18nLib = this.state.i18nLib;   
    i18nLib.setLng(language); 
    this.setState ({i18nLib: i18nLib} );           	
  },
  closeImportTool: function(){
	  if (confirm("Close Import Tool Window?")) {
		  window.location.href = '/aim/default/showDesktop.do'
	  }  
  },
  render: function() { 
    var content; 
    if(this.state.systemInfo.status == "OK"){
	    content = <RouteHandler i18nLib = {this.state.i18nLib}/>;   
    }else if(this.state.systemInfo.status == "DOWN"){
       content = <div className="alert alert-danger server-status-message" role="alert" ><span className="glyphicon glyphicon-exclamation-sign error-box" aria-hidden="true"></span><span className="sr-only">Error:</span><span >Unable to connect to the Import Tool Server.</span> </div>;
          
    }
    
    return (
     <div id="container">    
     <div className="clear">          
       <Header i18nLib = {this.state.i18nLib} switchLanguage={this.switchLanguage} closeImportTool = {this.closeImportTool}/>
        {content}
       <Footer i18nLib = {this.state.i18nLib} />
       </div>
      </div>
    );
  }
});

module.exports = Home;
