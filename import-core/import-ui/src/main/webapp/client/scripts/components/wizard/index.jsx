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

var formActions = require('./../../actions/form');

var Wizard = React.createClass({
  mixins: [Navigation],
  getInitialState: function() {
    return {
      info: {},
      results: []
    };
  },

  componentWillReceiveProps: function(nextProps) {
    if(this.props.params.src !== nextProps.params.src || this.props.params.dst !== nextProps.params.dst) {
      this.initImportSession(nextProps.params.src, nextProps.params.dst);
    }
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

  filterData: function(data) {
    formActions.updateFilters(data).then(function() { 
      this.transitionTo('projects', this.props.params);
    }.bind(this));
  },

  chooseProjects: function(data) {
    formActions.updateSelectedProjects(data).then(function() { 
      this.transitionTo('fields', this.props.params);
    }.bind(this));
  },

  chooseFields: function(data) {
    formActions.updateSelectedFields.triggerPromise(data).then(function() { 
      this.transitionTo('mapvalues', this.props.params);
    }.bind(this)).catch(function(err) {
      console.log("Error retrieving values");
    })
  },

  mapValues: function(data) {
    formActions.updateSelectedValues(data).then(function() { 
      this.transitionTo('import', this.props.params);
    }.bind(this));
  },

  launchImport: function() {
    $.get('/importer/import/execute', function(result) {
      this.setState({results: result});
      $("#modalResults").modal("show");
    }.bind(this));
  },

  initImportSession: function(sourceProcessor, destinationProcessor) {
    var compiledURL = _.template(appConfig.TOOL_START_ENDPOINT);
    var token = appConfig.DESTINATION_AUTH_TOKEN || "default_token";
    var url = compiledURL({
        'sourceProcessor': sourceProcessor,
        'destinationProcessor': destinationProcessor,
        'authenticationToken': appConfig.DESTINATION_AUTH_TOKEN,
        'host': appConfig.DESTINATION_API_HOST
      });

    $.get(url, function(result) {
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
    eventHandlers.launchImport        = this.launchImport;

    return (
      <div>
      <div className="container">
      <h2>{this.props.i18nLib.t('wizard.import_process')}&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
      <small>{this.state.info.sourceProcessorName} to {this.state.info.destinationProcessorName} </small></h2>
      <div className="row">
      <WizardSteps {...this.props}/>
      <div className="col-sm-9 col-md-9 main">
      <RouteHandler eventHandlers={eventHandlers} {...this.props}/>
      </div>
      </div>
      </div>
      <ImportReport results={this.state.results} />
      </div>
      );
  }
});
module.exports = Wizard;