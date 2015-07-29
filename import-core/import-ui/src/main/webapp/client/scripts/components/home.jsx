var React = require('react');
var Header = require('./layout/header');
var Footer = require('./layout/footer');
var Content = require('./content');
var Router = require('react-router');
var RouteHandler = Router.RouteHandler;
var translations = require('../../i18n/translations');
var Home = React.createClass({
   getInitialState: function() {                    
         i18n.init({ resStore:translations.resources ,fallbackLng: 'en',load: 'unspecific'});
         return {
             i18nLib: i18n
         }
   },
  componentDidMount: function() {    
  },
  switchLanguage: function(language){      
    var i18nLib = this.state.i18nLib;   
    i18nLib.setLng(language); 
    this.setState ({i18nLib: i18nLib} );           	
  },
  render: function() {  
    return (
     <div id="container">    
     <div className="clear">          
       <Header i18nLib = {this.state.i18nLib} switchLanguage={this.switchLanguage}/>
       <RouteHandler i18nLib = {this.state.i18nLib}/>
       <Footer i18nLib = {this.state.i18nLib} />
       </div>
      </div>
    );
  }
});

module.exports = Home;
