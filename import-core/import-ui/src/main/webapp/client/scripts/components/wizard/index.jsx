var React = require('react');
var Header = require('./../layout/header');
var Footer = require('./../layout/footer');
var WizardSteps = require('./wizard-steps');
var UploadFile = require('./upload-file');
var Router = require('react-router');
var RouteHandler = Router.RouteHandler;
var Wizard = React.createClass({
  componentDidMount: function () {
      // from the path '/wizard/:id'
      var id = this.props.params.id;    
  },
  render: function() {  
    return (
     <div>     
       <div className="container">
         <h2>Import Process <small>IATI 1.05</small></h2>
          <div className="row">
              <WizardSteps {...this.props} />           
              <div className="col-sm-9 col-md-9 main">
             <RouteHandler {...this.props} />
            </div>         
         </div>
        </div>
       
      </div>
    );
  }
});

module.exports = Wizard;
