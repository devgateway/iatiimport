var React = require('react');
var _ = require('lodash/dist/lodash.underscore');
var Header = require('./../layout/header');
var Footer = require('./../layout/footer');
var WizardSteps = require('./wizard-steps');
var ImportReport = require('./import-report');
var UploadFile = require('./upload-file');
var appConfig = require('./../../conf');

var Router = require('react-router');
var RouteHandler = Router.RouteHandler;
var Navigation = Router.Navigation;

var Wizard = React.createClass({
  mixins: [Navigation],
  getInitialState: function() {
    return {
      info: {},
      projects: [],
      fieldMappings: [],
      valueMappings: []
    };
  },
  componentWillReceiveProps: function(nextProps) {
    // Remove if not needed
    var sourceProcessor = nextProps.params.src;
    var destinationProcessor = nextProps.params.dst;

    this.initImportSession(sourceProcessor, destinationProcessor);
  },
  componentDidMount  : function() {
    var sourceProcessor = this.props.params.src;
    var destinationProcessor = this.props.params.dst;
    this.initImportSession(sourceProcessor, destinationProcessor);
  },

  // Steps and transitions
  uploadFile: function() {
    this.transitionTo('filter', this.props.params);
  },
  filterData: function() {
    this.transitionTo('projects', this.props.params);
  },
  chooseProjects: function() {
    this.transitionTo('fields', this.props.params);
  },
  chooseFields: function() {
    this.transitionTo('mapvalues', this.props.params);
  },
  mapValues: function() {
    this.transitionTo('import', this.props.params);
  },
  reviewImport: function() {
    console.log("Call the modal");
    //Ugly ugly call.
    $("#myModal").modal("show");
  },
  // Callbacks from the steps
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
  updateProjects: function(data,selectedProject){  

    //TODO: Remove this logic from here. Push it to the store or the backend.

    if (data.sourceProjectId) {
      var projects = this.state.projects;
      var match = _.findWhere(projects, {
        'sourceProjectId': data.sourceProjectId        
      });
      if (match) {
        match.destinationProject = selectedProject;
      } else {
        projects.push({
          sourceProjectId: data.sourceProjectId,          
          destinationProject: selectedProject
        });
      }
      this.setState({
        projects: projects
      });
    }  
    
  },  
  selectProject: function(event) {
    if (event) {
      var projects = this.state.projects;
      var match = _.findWhere(projects, {
        'sourceProjectId': event.target.value
      });
      if (match) {
        match.selected = event.target.checked;
      } else {
        projects.push({
          sourceProjectId: event.target.value,
          selected       : event.target.checked
        });
      }
      this.setState({
        projects: projects
      });
      console.log(projects);
    }
  },
  initImportSession: function(sourceProcessor, destinationProcessor) {
    var compiledURL = _.template(appConfig.TOOL_START_ENDPOINT);
    $.get(compiledURL({
      'sourceProcessor': sourceProcessor,
      'destinationProcessor': destinationProcessor,
      'authenticationToken': appConfig.DESTINATION_AUTH_TOKEN
    }), function(result) {
      this.setState({
                      info: {
                        authenticationToken: result.authenticationToken,
                        sourceProcessorName: result.sourceProcessorName,
                        sourceProcessor: sourceProcessor,
                        destinationProcessorName: result.destinationProcessorName,
                        destinationProcessor: destinationProcessor
                      }
                    });
    }.bind(this)).fail(function() {
      console.log("Error loading state of session.");
    });
  },
  
  render: function() {
    var eventHandlers = {};
    eventHandlers.uploadFile          = this.uploadFile;
    eventHandlers.filterData          = this.filterData;
    eventHandlers.chooseProjects      = this.chooseProjects;
    eventHandlers.chooseFields        = this.chooseFields;
    eventHandlers.mapValues           = this.mapValues;
    eventHandlers.reviewImport        = this.reviewImport;

    eventHandlers.updateFieldMappings = this.updateFieldMappings;
    eventHandlers.selectFieldMapping  = this.selectFieldMapping;
    eventHandlers.updateValueMappings = this.updateValueMappings;
    eventHandlers.updateProjects      = this.updateProjects;
    eventHandlers.selectProject       = this.selectProject;


    var wizardData = {};
    wizardData.fieldMappings = this.state.fieldMappings;
    wizardData.valueMappings = this.state.valueMappings;
    wizardData.projects      = this.state.projects;

    return (
      <div>
      <div className="container">
      <h2>{this.props.i18nLib.t('wizard.import_process')}&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
      <small>{this.state.info.sourceProcessorName} to {this.state.info.destinationProcessorName} </small></h2>
      <div className="row">
      <WizardSteps {...this.props}/>
      <div className="col-sm-9 col-md-9 main">
      <RouteHandler eventHandlers={eventHandlers} wizardData={wizardData} {...this.props}/>
      </div>
      </div>
      </div>
      <ImportReport {...this.props} />
      </div>
      );
  }
});
module.exports = Wizard;