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
var common = require('./../../utils/common');
var destinationSessionStore = require('./../../stores/DestinationSessionStore');

var Wizard = React.createClass({
	mixins: [Navigation],
	setIntervalId: null,
	getInitialState: function() {
	    return {
			info: {},
			results: [],
			currentStep: this.props.params.src == constants.IMPORT_TYPE_AUTOMATIC ? constants.SELECT_DATASOURCE : constants.UPLOAD_FILE,
			completedSteps: [],
			versions:[],
			processedVersions: [],
			projectWithUpdates:[],
			currentVersion: null,
			statusMessage: "",
			showDisasterResponse: false
		};
	},
	componentWillReceiveProps: function(nextProps) {
		if(this.props.params.src !== nextProps.params.src || this.props.params.dst !== nextProps.params.dst) {
			this.initImportSession(nextProps.params.src, nextProps.params.dst);
		}
	},
	componentDidMount: function() {
	    if (this.props.params.src !== constants.IMPORT_TYPE_AUTOMATIC) {
	        this.resetSession();
	        this.initManualImport();
	    }
	},
	resetSession: function() {
	      $.get('/importer/import/wipeall', function(){}); 
	},
	initManualImport: function() {
	    var sourceProcessor = this.props.params.src;
        var destinationProcessor = this.props.params.dst;    
        appActions.refreshDestinationSession.triggerPromise().then(function(data) {
            common.setAuthCookies(data);
            this.initImportSession(sourceProcessor, destinationProcessor); 
            common.refreshToken();
        }.bind(this))["catch"](function(err) {
            common.resetAuthCookies();
            this.goHome();
        }.bind(this));
           
	},
	getSourceProcessor: function(src) {
	      if (src === constants.IMPORT_TYPE_AUTOMATIC && this.state.currentVersion) {
	          return "IATI" + this.state.currentVersion.replace(".", "")
	      }
	        
	      return src;
	 },  
	 initAutomaticImport: function() {      
	      var sourceProcessor = this.props.params.src;
	      var destinationProcessor = this.props.params.dst; 
	        
	      appActions.refreshDestinationSession.triggerPromise().then(function(data) {
	           common.setAuthCookies(data);           
	           this.initImportSession(sourceProcessor, destinationProcessor).then(function(){
	                this.transitionTo('filter', this.props.params);
	           }.bind(this));
	            
	           common.refreshToken();            
	    }.bind(this))["catch"](function(err) {
	            common.resetAuthCookies();
	    }.bind(this));
	        
	                
	        
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
		console.log('error');
	},
	// Steps and transitions
	uploadFile: function() {
		this.transitionTo('filter', this.props.params);
	},
     
	selectDataSource: function() {
	    this.setState({completedSteps: [], versions:[], processedVersions: [], projectWithUpdates:[], currentVersion: null});        
	    this.transitionTo('selectdatasource', this.props.params);
	},
	
	filterData: function(languageData, filterData, direction) {
		var languagesUpdated = false;
		var filtersUpdated = false;
		var nextStep = 'projects';
		var previousStep = this.props.params.src !== constants.IMPORT_TYPE_AUTOMATIC ? 'upload' : 'selectversion';
		    
		formActions.updateLanguages.triggerPromise(languageData).then(function() {
			languagesUpdated = true;
			if(languagesUpdated && filtersUpdated){
				if(constants.DIRECTION_NEXT === direction){
					this.transitionTo(nextStep, this.props.params);
				} else {
					this.transitionTo(previousStep, this.props.params);
				}
			}
		}.bind(this));

		formActions.updateFilters.triggerPromise(filterData).then(function() {
			filtersUpdated = true;
			if(languagesUpdated && filtersUpdated){
				if(constants.DIRECTION_NEXT === direction){
					this.transitionTo(nextStep, this.props.params);
				}else{
					this.transitionTo(previousStep, this.props.params);
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
	//fetch data from iati datastore
  fetchData: function(reportingOrg){
	  this.initializeFetchData(reportingOrg);
    var id;
    const self = this;
    id = setInterval(function(){
      if(self.initializeFailed){
        clearInterval(id);
      }else{
        self.loadReportingOrganisation(id);
      }
    }, 3000);
  },
  /**
   * this should probably needs to be an refulx action
   * @param reportingOrgId
   */
  initializeFetchData: function(reportingOrgId) {
    $(this.refs.loadingIcon.getDOMNode()).show();
    var self = this;
    $.ajax({
      url: '/importer/import/fetch/initialize/' + reportingOrgId,
      error: function() {
        self.initializeFailed = true;
      },
      success: function(data) {
        if(data.error){
          self.props.eventHandlers.hideLoadingIcon();
          self.setState({statusMessage: ""});
          var message = self.props.i18nLib.t('server_messages.' + data.code, data) || data.error;
          self.props.eventHandlers.displayError(message);
        }
      },
      type: 'GET'
    });
  },
  loadReportingOrganisation:  function(id) {
    var self =this;
    $.ajax({
      url: "/importer/import/fetch/results",
      async: false,
      timeout:appConfig.REQUEST_TIMEOUT,
      error: function (error) {
        self.initializeFailed = true;
      },
      success: function (data) {
        if (data) {
          if (data.status === 'COMPLETED') {
            clearInterval(id);
            $(self.refs.loadingIcon.getDOMNode()).hide();
            self.setState({
              versions: data.versions,
              currentVersion: data.versions[0],
              processedVersions: [data.versions[0]],
              projectWithUpdates: data.projectWithUpdates
            });
            self.transitionTo('selectversion', self.props.params);
          } else if (data.status === 'FAILED_WITH_ERROR') {
            $(self.refs.loadingIcon.getDOMNode()).hide();
            self.displayError("Server Error");
            clearInterval(id);
          }
        }
      }
    });
  },
	processNextVersion: function() {
	    var processedVersions = this.state.processedVersions;
	    var currentIndex = this.state.versions.indexOf(this.state.currentVersion);
	   
	    var nextIndex = currentIndex + 1;
	    if (nextIndex <= this.state.versions.length - 1){
	        var nextVersion = this.state.versions[nextIndex]; 
	        processedVersions.push(nextVersion);
	        this.setState({currentVersion: nextVersion, processedVersions: processedVersions})
	    }	
	    
	    this.transitionTo('selectversion', this.props.params);
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
	launchImport: function(importOption, disasterResponse) {
		var self = this;
		this.showLoadingIcon();
		$.ajax({
	    	url: '/importer/import/execute',
	        dataType: 'json',
	        contentType: "application/json; charset=utf-8",
	        data: JSON.stringify({importOption: importOption, disasterResponse: disasterResponse}),
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
	},

	initImportSession: function(sourceProcessor, destinationProcessor) {
	    this.showLoadingIcon();
		var compiledURL = _.template(appConfig.TOOL_START_ENDPOINT);
		var url = compiledURL({
			'sourceProcessor': this.getSourceProcessor(sourceProcessor),
			'destinationProcessor': destinationProcessor,
			'authenticationToken': Cookies.get("DESTINATION_AUTH_TOKEN"),
			'username': Cookies.get("DESTINATION_USERNAME"),
			'host': appConfig.DESTINATION_API_HOST
		});

		var self = this;
		return $.ajax({
      url: url,
      timeout:appConfig.REQUEST_TIMEOUT,
      dataType: 'json',
      type: 'GET',
      error: function(error) {
            var serverResponse = JSON.parse(error.responseText);
	        	self.setState({
					info: {
						status:'FAIL'
					}
				});
	        	self.hideLoadingIcon();
	        	self.displayError(self.props.i18nLib.t('server_messages.' + serverResponse.message));
        ;
	        },
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
	        }
	     });
  },
  showDisasterResponse: function(hasDisasterResponseField) {
     this.setState({showDisasterResponse: hasDisasterResponseField});
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
    eventHandlers.fetchData = this.fetchData;
    eventHandlers.initializeFetchData = this.initializeFetchData;
    eventHandlers.initAutomaticImport = this.initAutomaticImport;
    eventHandlers.processNextVersion = this.processNextVersion;
    eventHandlers.selectDataSource = this.selectDataSource;
    eventHandlers.showDisasterResponse = this.showDisasterResponse;

    var error;
    if(common.hasValidSession() == false){
    	return (<div className="container"><br/><div className="alert alert-danger server-status-message" role="alert" ><span className="glyphicon glyphicon-exclamation-sign error-box" aria-hidden="true"></span><span className="sr-only">Error:</span><span > {window.i18nLib.t('wizard.invalid_session')}</span> </div></div>);
    }

    if(Cookies.get("CAN_ADD_ACTIVITY") == "false"){
    	return (<div className="container"><br/><div className="alert alert-danger server-status-message" role="alert" ><span className="glyphicon glyphicon-exclamation-sign error-box" aria-hidden="true"></span><span className="sr-only">Error:</span><span > Access Denied. {Cookies.set("DESTINATION_USERNAME")} does not have permission to import activities into {Cookies.set("WORKSPACE")} workspace.</span> </div></div>);
    }

    return (
      <div>
      <div className="wizard-container" > 
     <h2>{this.props.i18nLib.t('wizard.import_process')}&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
      {this.state.info.sourceProcessorName && this.state.info.destinationProcessorName &&
          
          <small>{this.state.info.sourceProcessorName} {window.i18nLib.t('header.nav.menu.submenu.to')} {this.state.info.destinationProcessorName} </small> 
      }      
      </h2>
      <div className="row">
      <WizardSteps {...this.props} currentStep = {this.state.currentStep} completedSteps= {this.state.completedSteps} />
      <div className="col-sm-10 col-md-10 main " >
      <div className="alert alert-danger message-box" role="alert" ref="messageBox">
       <span className="glyphicon glyphicon-exclamation-sign error-box" aria-hidden="true"></span>
       <span className="sr-only">Error:</span>
         <span ref="message"></span>
       </div>
      <div className="loading-icon" ref="loadingIcon"></div>
      <RouteHandler eventHandlers={eventHandlers} {...this.props}  {...this.state}/>
      </div>
      </div>
      </div>
      <ImportReport results={this.state.results} {...this.props} />
      </div>
      );
  }
});
module.exports = Wizard;
