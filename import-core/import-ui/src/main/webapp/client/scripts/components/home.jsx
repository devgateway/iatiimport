var React = require('react');
var Header = require('./layout/header');
var Footer = require('./layout/footer');
var Content = require('./content');
var Router = require('react-router');
var RouteHandler = Router.RouteHandler;
var translations = require('./../utils/translations');
var Home = React.createClass({
   getInitialState: function() {             
         i18n.init({lng: window.navigator.userLanguage || window.navigator.language || 'en', resStore:translations.resources ,lng: 'en',fallbackLng: 'en',debug: true});
         return {
             i18nLib: i18n
         }
     },
  componentDidMount: function() { 
    
  }, 
  onLanguageChange: function(event){  
    var i18nLib = this.state.i18nLib;   
    i18nLib.setLng(event.target.value); 
    this.setState ({i18nLib: i18nLib} );         	
  },
  render: function() {  
    return (
     <div id="container">    
     <div >
     <select id="language-selector" onChange ={this.onLanguageChange} >
       <option value="en">English	</option>
       <option value="fr">French</option>
      </select>
     </div >  
     <div className="clear">          
       <Header i18nLib = {this.state.i18nLib}/>
       <RouteHandler i18nLib = {this.state.i18nLib}/>
       <Footer i18nLib = {this.state.i18nLib} />
       </div>
      </div>
    );
  }
});

module.exports = Home;
