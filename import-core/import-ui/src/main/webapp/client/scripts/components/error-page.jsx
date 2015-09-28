var React = require('react');
var Reflux = require('reflux');
var Content = require('./content');
var Router = require('react-router');
var ErrorPage = React.createClass({  
  componentDidMount: function() {    
  },  
  render: function() { 
   return (
     <div id="container">    
     <div className="clear">      
        <div className="alert alert-danger server-status-message" role="alert" ><span className="glyphicon glyphicon-exclamation-sign error-box" aria-hidden="true"></span><span className="sr-only">Error:</span><span >Unable to connect to the Import Tool Server.</span> </div>;      
       </div>
      </div>
    );
  }
});

module.exports = ErrorPage;