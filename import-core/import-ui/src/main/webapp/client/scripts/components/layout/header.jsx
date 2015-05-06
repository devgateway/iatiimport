var React = require('react');
var Menu = require('./../navigation/menu');
var Header = React.createClass({
  render: function() {
    return (
      <nav className="navbar navbar-default navbar-iati">
      <div className="container">
         <div className="navbar-header">
           <button type="button" className="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1">
            <span className="sr-only">Toggle navigation</span>
            <span className="icon-bar"></span>
            <span className="icon-bar"></span>
            <span className="icon-bar"></span>
           </button>
           <a className="navbar-brand" href="#">IATI Import Tool</a>
          </div>         
          <Menu/>         
        </div>        
      </nav>
    );
  }
});

module.exports = Header;
