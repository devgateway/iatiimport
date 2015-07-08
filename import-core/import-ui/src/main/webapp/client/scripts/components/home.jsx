var React = require('react');
var Header = require('./layout/header');
var Footer = require('./layout/footer');
var Content = require('./content');
var Router = require('react-router');
var RouteHandler = Router.RouteHandler;
var translations = require('../../i18n/translations');
var Cookies = require('js-cookie');
var Home = React.createClass({
   getInitialState: function() {                    
         i18n.init({ resStore:translations.resources ,fallbackLng: 'en',load: 'unspecific'});
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
       <option value="en" selected={Cookies.get('i18next') === 'en' ? 'selected' : ''}>English	</option>
       <option value="fr" selected={Cookies.get('i18next') === 'fr' ? 'selected' : ''}>French</option>
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
