var React = require('react');
var Header = require('./layout/header');
var Footer = require('./layout/footer');
var Content = require('./content');
var Router = require('react-router');
var RouteHandler = Router.RouteHandler;
var Home = React.createClass({
  render: function() {
    return (
     <div>     
       <Header />
       <RouteHandler/>
       <Footer/>
      </div>
    );
  }
});

module.exports = Home;
