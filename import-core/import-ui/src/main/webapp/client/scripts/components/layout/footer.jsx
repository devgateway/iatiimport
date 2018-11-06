var React = require('react');
var Footer = React.createClass({
  render: function() {
    var year = new Date().getFullYear();
    return (
    <div className="container">
        <footer className="footer">
          @@buildSource@@ <p>Developed by Development Gateway, Inc - {year} </p>
       </footer>
    </div>
      
    );
  }
});

module.exports = Footer;
