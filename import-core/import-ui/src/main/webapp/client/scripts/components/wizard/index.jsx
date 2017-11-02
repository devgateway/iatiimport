var React = require('react');
var _ = require('lodash/dist/lodash.underscore');
var Header = require('./../layout/header');
var Footer = require('./../layout/footer');
var WizardSteps = require('./wizard-steps');
var ImportReport = require('./import-report');
var UploadFile = require('./upload-file');
var appConfig = require('./../../conf');
var appActions = require('./../../actions');
var Router = require('react-router');
var RouteHandler = Router.RouteHandler;
var Navigation = Router.Navigation;
var formActions = require('./../../actions/form');
var Cookies = require('js-cookie');
var constants = require('./../../utils/constants');
var destinationSessionStore = require('./../../stores/DestinationSessionStore');

var Wizard = React.createClass({
	mixins: [Navigation],
	setIntervalId: null,
	getInitialState: function() {
		return {
			info: {},
			results: [],
			currentStep: constants.UPLOAD_FILE,
			completedSteps: [],
			statusMessage: ""
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
		appActions.initDestinationSession.triggerPromise().then(function(data) {
			  appConfig.DESTINATION_AUTH_TOKEN = data.token;
		    appConfig.DESTINATION_USERNAME = data['user-name'];
				appConfig.DESTINATION_AUTH_TOKEN_EXPIRATION = data["token-expiration"];
		    Cookies.set("DESTINATION_AUTH_TOKEN", data.token);
		    Cookies.set("DESTINATION_USERNAME", data['user-name']);
		    // Added true always for now, the API returns wrong value
		    Cookies.set("CAN_ADD_ACTIVITY", true || data['add-activity']);
		    Cookies.set("WORKSPACE", data['team']);

				this.initImportSession(sourceProcessor, destinationProcessor);

				// Timer for token expiration
				var self = this;
				self.setIntervalTokenId = setInterval(function(){
					self.checkTokenStatus();
				}, 1000);

	      }.bind(this))["catch"](function(err) {
	      }.bind(this));

	},
	checkTokenStatus: function() {
		var currentTime = (new Date).getTime();
		var expirationTime = appConfig.DESTINATION_AUTH_TOKEN_EXPIRATION;
		var secondsToExpire = (appConfig.DESTINATION_AUTH_TOKEN_EXPIRATION - currentTime)/1000;
		if(secondsToExpire < 0) {
			appActions.refreshDestinationSession.triggerPromise().then(function(data) {
				  appConfig.DESTINATION_AUTH_TOKEN = data.token;
			    appConfig.DESTINATION_USERNAME = data['user-name'];
					appConfig.DESTINATION_AUTH_TOKEN_EXPIRATION = data["token-expiration"];
			    Cookies.set("DESTINATION_AUTH_TOKEN", data.token);
			    Cookies.set("DESTINATION_USERNAME", data['user-name']);
			    // Added true always for now, the API returns wrong value
			    Cookies.set("CAN_ADD_ACTIVITY", true || data['add-activity']);
			    Cookies.set("WORKSPACE", data['team']);
					$.get(appConfig.TOOL_REST_PATH + '/refresh/' + data.token, function(){});

		      }.bind(this))["catch"](function(err) {
		      }.bind(this));

		}
	},
	updateCurrentStep : function(step){
	  var completedSteps = this.state.completedSteps;
	  completedSteps.push(step);
	  this.setState({currentStep:step, completedSteps: completedSteps})
	},
	hideLoadingIcon: function(){
		if(!_.isEmpty(this.state.info)){
			$(this.refs.loadingIcon.getDOMNode()).hide();
		}
	},

	showLoadingIcon:  function(){
		$(this.refs.loadingIcon.getDOMNode()).show();
	},

	displayError: function(msg){
		$(this.refs.message.getDOMNode()).html(msg);
		var box = $(this.refs.messageBox.getDOMNode());
		box.show();
		box.fadeOut({duration:10000});
	},
	// Steps and transitions
	uploadFile: function() {
		this.transitionTo('filter', this.props.params);
	},

	filterData: function(languageData, filterData, direction) {
		var languagesUpdated = false;
		var filtersUpdated = false;
		formActions.updateLanguages.triggerPromise(languageData).then(function() {
			languagesUpdated = true;
			if(languagesUpdated && filtersUpdated){
				if(constants.DIRECTION_NEXT === direction){
					this.transitionTo('projects', this.props.params);
				}else{
					this.transitionTo('upload', this.props.params);
				}
			}
		}.bind(this));

		formActions.updateFilters.triggerPromise(filterData).then(function() {
			filtersUpdated = true;
			if(languagesUpdated && filtersUpdated){
				if(constants.DIRECTION_NEXT === direction){
					this.transitionTo('projects', this.props.params);
				}else{
					this.transitionTo('upload', this.props.params);
				}
			}
		}.bind(this));
	},



	chooseProjects: function(data,direction) {
		formActions.updateSelectedProjects(data).then(function() {
			if(constants.DIRECTION_NEXT === direction){
			    this.transitionTo('fields', this.props.params);
			}else{
				this.transitionTo('filter', this.props.params);
			}
		}.bind(this));
	},

	chooseFields: function(data, direction) {
		formActions.updateSelectedFields.triggerPromise(data).then(function() {
			if(constants.DIRECTION_NEXT === direction){
				this.transitionTo('mapvalues', this.props.params);
			}else{
				this.transitionTo('projects', this.props.params);
			}
		}.bind(this))["catch"](function(err) {
			console.log("Error retrieving values");
		})
	},

	mapValues: function(data, direction) {
		formActions.updateSelectedValues.triggerPromise(data).then(function() {
			if(constants.DIRECTION_NEXT === direction){
				this.transitionTo('import', this.props.params);
			}else{
				this.transitionTo('fields', this.props.params);
			}
		}.bind(this));
	},
	navigateBack: function(){
		if(this.setIntervalId){
			clearInterval(this.setIntervalId);
		}
    	this.transitionTo('mapvalues', this.props.params);
    },
    goHome: function(){
        window.location = "#";
    },
	launchImport: function(importOption) {
		var self = this;
		this.showLoadingIcon();
		$.ajax({
	    	url: '/importer/import/execute',
	        dataType: 'json',
	        contentType: "application/json; charset=utf-8",
	        data: JSON.stringify({importOption: importOption}),
	        success: function(data) {
	        },
	        type: 'POST'
	     });

		var self = this;
		self.setIntervalId = setInterval(function(){
    		self.checkImportStatus();
    	}, 3000);

	},
	checkImportStatus: function(){
		var self = this;
		$.ajax({
	    	url: '/importer/import/execute/status',
	        dataType: 'json',
	        success: function(data) {
	        if(data.importStatus){
	          if(data.importStatus.status == "COMPLETED"){
	    			clearInterval(self.setIntervalId);
	    			self.setState({results: data.results});
		        	self.hideLoadingIcon();
		            $("#modalResults").modal("show");
		            self.setState({statusMessage: ""});
	        	}else{
	        	   var message = self.props.i18nLib.t('server_messages.' + data.importStatus.code, data.importStatus);
		           if(message){
		             self.setState({statusMessage: message})
		           }else{
		             self.setState({statusMessage: data.importStatus.message})
		           }
	        	}
	         }else{
	           clearInterval(self.setIntervalId);
	           self.hideLoadingIcon();
	         }

	        },
	        error: function (xhr, ajaxOptions, thrownError) {
	           self.displayError("Server Error");
	           self.hideLoadingIcon();
               clearInterval(self.setIntervalId);
            },
	        type: 'GET'
	     });
	}
	,
	initImportSession: function(sourceProcessor, destinationProcessor) {
		this.showLoadingIcon();
		var compiledURL = _.template(appConfig.TOOL_START_ENDPOINT);
		var url = compiledURL({
			'sourceProcessor': sourceProcessor,
			'destinationProcessor': destinationProcessor,
			'authenticationToken': Cookies.get("DESTINATION_AUTH_TOKEN"),
			'username': Cookies.get("DESTINATION_USERNAME"),
			'host': appConfig.DESTINATION_API_HOST
		});

		var self = this;
		$.ajax({
	        url: url,
	        timeout:appConfig.REQUEST_TIMEOUT,
	        error: function(result) {
	        	self.setState({
					info: {
						status:'FAIL'
					}
				});
	        	self.hideLoadingIcon();
	        	self.displayError("Error loading state of session.");
	        },
	        dataType: 'json',
	        success: function(result) {
	        	self.setState({
					info: {
						authenticationToken: result.authenticationToken,
						sourceProcessorName: result.sourceProcessorName,
						sourceProcessor: sourceProcessor,
						destinationProcessorName: result.destinationProcessorName,
						destinationProcessor: destinationProcessor,
						status:'SUCCESS'
					}
				});
	        	self.hideLoadingIcon();
	        },
	        type: 'GET'
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
    eventHandlers.hideLoadingIcon     = this.hideLoadingIcon;
    eventHandlers.showLoadingIcon     = this.showLoadingIcon;
    eventHandlers.displayError = this.displayError;
    eventHandlers.updateCurrentStep = this.updateCurrentStep;
    eventHandlers.navigateBack  = this.navigateBack;
    eventHandlers.goHome = this.goHome;

    var error;
    if(Cookies.get("DESTINATION_AUTH_TOKEN") == "null" || Cookies.get("DESTINATION_AUTH_TOKEN") == "undefined"){
    	return (<div className="container"><br/><div className="alert alert-danger server-status-message" role="alert" ><span className="glyphicon glyphicon-exclamation-sign error-box" aria-hidden="true"></span><span className="sr-only">Error:</span><span > Session information for the destination system could not be retrieved. Verify if backend services are working correctly.</span> </div></div>);
    }

    if(Cookies.get("CAN_ADD_ACTIVITY") == "false"){
    	return (<div className="container"><br/><div className="alert alert-danger server-status-message" role="alert" ><span className="glyphicon glyphicon-exclamation-sign error-box" aria-hidden="true"></span><span className="sr-only">Error:</span><span > Access Denied. {Cookies.set("DESTINATION_USERNAME")} does not have permission to import activities into {Cookies.set("WORKSPACE")} workspace.</span> </div></div>);
    }

    return (
      <div>
      <div className="wizard-container" >
      <h2>{this.props.i18nLib.t('wizard.import_process')}&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
      <small>{this.state.info.sourceProcessorName} {window.i18nLib.t('header.nav.menu.submenu.to')} {this.state.info.destinationProcessorName} </small></h2>
      <div className="row">
      <WizardSteps {...this.props} currentStep = {this.state.currentStep} completedSteps= {this.state.completedSteps} />
      <div className="col-sm-10 col-md-10 main " >
      <div className="alert alert-danger message-box" role="alert" ref="messageBox">
       <span className="glyphicon glyphicon-exclamation-sign error-box" aria-hidden="true"></span>
       <span className="sr-only">Error:</span>
         <span ref="message"></span>
       </div>
      <div className="loading-icon" ref="loadingIcon"></div>
      <RouteHandler eventHandlers={eventHandlers} {...this.props} statusMessage = {this.state.statusMessage}/>
      </div>
      </div>
      </div>
      <ImportReport results={this.state.results} {...this.props} />
      </div>
      );
  }
});
module.exports = Wizard;
