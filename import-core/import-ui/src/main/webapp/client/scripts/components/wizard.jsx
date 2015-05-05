var React = require('react');
var Header = require('./header');
var Footer = require('./footer');
var Content = require('./content');
var Wizard = React.createClass({
  componentDidMount: function () {
      // from the path '/wizard/:id'
      var id = this.props.params.id;    
  },
  render: function() {  
    return (
     <div>     
       <Header />
       <div className="container">
       <h1>Wizard {this.props.params.id}</h1>
       </div>
       <Footer/>
      </div>
    );
  }
});

module.exports = Wizard;
