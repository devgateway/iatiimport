var React = require('react');
var _ = require('lodash/dist/lodash.underscore');
var Header = require('./../layout/header');
var Footer = require('./../layout/footer');
var WizardSteps = require('./wizard-steps');
var UploadFile = require('./upload-file');
var Router = require('react-router');
var RouteHandler = Router.RouteHandler;
var Wizard = React.createClass({
  getInitialState    : function() {
    return {
      projects     : [],
      fieldMappings: [],
      valueMappings: []
    };
  },
  componentDidMount  : function() {
    // from the path '/wizard/:id'
    var id = this.props.params.id;
  },
  processUpload: function() {},
  updateFieldMappings: function(data, selectedDestinationField) {
    if (data.sourceField) {
      var fieldMappings = this.state.fieldMappings;
      var match = _.findWhere(fieldMappings, {
        'sourceFieldName': data.sourceField
      });
      if (match) {
        match.destinationFieldName = selectedDestinationField;
      } else {
        fieldMappings.push({
          sourceFieldName     : data.sourceField,
          destinationFieldName: selectedDestinationField
        });
      }
      this.setState({
        fieldMappings: fieldMappings
      });
    }
  },
  selectFieldMapping: function(event) {
    if (event) {
      var fieldMappings = this.state.fieldMappings;
      var match = _.findWhere(fieldMappings, {
        'sourceFieldName': event.target.value
      });
      if (match) {
        match.selected = event.target.checked;
      } else {
        fieldMappings.push({
          sourceFieldName: event.target.value,
          selected       : event.target.checked
        });
      }

      this.setState({
        fieldMappings: fieldMappings
      });
    }
  },
  updateValueMappings: function(data, selectedDestinationValue) {
    if (data.sourceValue) {
      var valueMappings = this.state.valueMappings;
      var match = _.findWhere(valueMappings, {
        'sourceFieldName': data.sourceFieldName,
        'sourceValueName': data.sourceValue
      });
      if (match) {
        match.destinationValueName = selectedDestinationValue;
      } else {
        valueMappings.push({
          sourceFieldName     : data.sourceFieldName,
          sourceValueName     : data.sourceValue,
          destinationValueName: selectedDestinationValue
        });
      }
      this.setState({
        valueMappings: valueMappings
      });
    }
  },
  render: function() {
    var eventHandlers = {};
    eventHandlers.processHandler      = this.processUpload;
    eventHandlers.updateFieldMappings = this.updateFieldMappings;
    eventHandlers.selectFieldMapping  = this.selectFieldMapping;
    eventHandlers.updateValueMappings = this.updateValueMappings;

    var wizardData = {};
    wizardData.fieldMappings = this.state.fieldMappings;
    wizardData.valueMappings = this.state.valueMappings;
    wizardData.projects      = this.state.projects;

    return (
      <div>
        <div className="container">
          <h2>Import Process
            <small>IATI 1.05</small></h2>
          <div className="row">
            <WizardSteps {...this.props}/>
            <div className="col-sm-9 col-md-9 main">
              <RouteHandler eventHandlers={eventHandlers} wizardData={wizardData} {...this.props}/>
            </div>
          </div>
        </div>
      </div>
    );
  }
});
module.exports = Wizard;