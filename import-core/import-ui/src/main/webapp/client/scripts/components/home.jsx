var React = require('react');
var Header = require('./header');
var Footer = require('./footer');
var Content = require('./content');
var Home = React.createClass({
  render: function() {
    return (
     <div>     
       <Header />
       <Content/>
       <Footer/>
      </div>
    );
  }
});

module.exports = Home;
