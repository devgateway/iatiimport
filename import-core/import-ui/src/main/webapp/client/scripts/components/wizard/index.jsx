var React = require('react');
var Header = require('./../layout/header');
var Footer = require('./../layout/footer');
var WizardSteps = require('./wizard-steps');
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
         <h2>Import Process <small>IATI 1.05</small></h2>
          <div className="row">
           <WizardSteps/>         
         </div>
        </div>
       <Footer/>
      </div>
    );
  }
});

module.exports = Wizard;
