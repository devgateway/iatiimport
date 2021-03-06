var React = require('react');
var typeaheadimpl = require('./../../utils/typeaheadimpl');
var projectStore = require('./../../stores/ProjectStore');
var reactAsync = require('react-async');
var Reflux = require('reflux');
var appActions = require('./../../actions');
var appConfig = require('./../../conf');
var Router = require('react-router');
var Link = Router.Link;
var AutoComplete = require('./autocomplete');
var SimilarProjectsDialog = require('./similar-projects-dlg');
var ProjectPreview = require('./project-preview/main');
var _ = require('lodash/dist/lodash.underscore');
var constants = require('./../../utils/constants');
var sourceFieldsStore = require('./../../stores/SourceFieldsStore');
var Tooltip = require('./tooltip');
var common = require('./../../utils/common');
var OPERATION_INSERT = 'INSERT';
var OPERATION_UPDATE = 'UPDATE';

var ChooseProjects = React.createClass({
    mixins: [
        Reflux.ListenerMixin
    ],
    getInitialState: function() {
       return {projectData: [],
           destinationProjects: [],
           statusMessage: "",
           showSimilarProjects: false,
           projectMapping: null,
           showSourceProjectPreview: false,
           sourceFieldsData:[],
           similarProjectSelected: ""
          };
    },
    initializeFailed:false,
    componentWillMount: function () {
     this.showSimilarProjectsDialog = this.showSimilarProjectsDialog.bind(this);
     this.mapSelectedProject = this.mapSelectedProject.bind(this);
     this.updateSimilarProjectSelected = this.updateSimilarProjectSelected.bind(this);
     this.resetMapping = this.resetMapping.bind(this);
     this.props.eventHandlers.updateCurrentStep(constants.CHOOSE_PROJECTS);
     this.listenTo(projectStore, this.updateProject);
     this.listenTo(sourceFieldsStore, this.updateSourceFields);
     this.loadData();
    },
    updateProject: function (data) {
        this.setState({
            projectData: data
        });
    },
    updateSourceFields: function(data) {
        this.setState({
            sourceFieldsData: data
        });
    },
    loadData: function(){
     this.loadProjects();
      var id;
      this.props.eventHandlers.showLoadingIcon();
      appActions.loadSourceFieldsData.triggerPromise().then(function(data) {
          this.updateSourceFields(data);
      }.bind(this))["catch"](function(err) {
          this.props.eventHandlers.displayError(this.props.i18nLib.t('wizard.map_fields.msg_error_retrieving_source_fields'));
      }.bind(this));

      this.initializeMapping();
      var self = this;
      id = setInterval(function(){
         if(self.initializeFailed){
            clearInterval(id);
         }else{
            self.loadSourceProjects(id);
         }
      }, 3000);
      this.loadProjects();
    },
    selectAll: function(){
    },
    selectProject: function(event) {
       this.props.eventHandlers.selectProject(event);
    },
    selectAllNew: function(event){
        this.selectAll(event.target.checked, OPERATION_INSERT);
    },
    selectAllExisting: function(){
        this.selectAll(event.target.checked, OPERATION_UPDATE);
    },
    selectAll: function(checked, operation){
    	_.each(this.state.projectData, function(item){
    	    if(item.operation === operation && ((operation == OPERATION_UPDATE && item.destinationDocument.allowEdit === true) || operation == OPERATION_INSERT)){
    		    item.selected = checked;
    		}
    	});
    	this.forceUpdate();
    },
    handleToggle: function(item, event) {
        item.selected = event.target.checked;
        this.forceUpdate();
    },
    handleOverrideTitle: function(item, event){
    	 item.overrideTitle = event.target.checked;
         this.forceUpdate();
    },
    overrideTitleAll: function(operation, event){
         var projects = _.where(this.state.projectData, {operation: operation});
    	_.each(projects, function(item){
    	    if((operation == OPERATION_UPDATE && item.destinationDocument.allowEdit === true) || operation == OPERATION_INSERT){
    	        item.overrideTitle = event.target.checked;
            }
    	});
    	this.forceUpdate();
    },
    handleAutocompleteToggle: function(item, datum) {
        item.destinationDocument = datum;
        this.forceUpdate();
    },
  mapSelectedProject: function() {
       var projectData = this.state.projectData;
       var pm = this.state.projectMapping;
       var projectMapping = _.find(projectData, function(m) { return m.id === pm.id});
       projectMapping.destinationDocument = this.state.similarProjectSelected;
       this.setState({projectData: projectData});
    },
    resetSimilarProjectSelected: function() {
      this.updateSimilarProjectSelected(null);
    },
    updateSimilarProjectSelected: function(project) {
      this.setState({similarProjectSelected: project});
    },
    checkAll: function(operation){
        var projects = _.where(this.state.projectData, {operation: operation});
        var projectsSelected = _.where(this.state.projectData, {operation: operation, selected: true});
        if(projects.length === projectsSelected.length)
            return true;
        else
            return false;
    },
    overrideAll: function(operation){
        var projects = _.where(this.state.projectData, {operation: operation});
        var projectsSelected = _.where(this.state.projectData, {operation: operation, overrideTitle: true});
        if(projects.length === projectsSelected.length)
            return true;
        else
            return false;
    },
    handleNext: function() {
    	if(_.where(this.state.projectData, {selected: true}).length > 0){
    		  var processedData = this.state.projectData;
    	        _.each(processedData, function(item){
                 //item.sourceDocument.dateFields = {};
    	        });
    	        this.props.eventHandlers.chooseProjects(processedData, constants.DIRECTION_NEXT);
    	}else{
    		this.props.eventHandlers.displayError(this.props.i18nLib.t('wizard.choose_projects.msg_error_select_project'));
    	}

    },
    handlePrevious: function(){
    	this.props.eventHandlers.chooseProjects(this.state.projectData, constants.DIRECTION_PREVIOUS);
	},
	initializeMapping: function(){
	     var self = this;
		 $.ajax({
		        url: '/importer/import/initialize',
		        timeout:appConfig.REQUEST_TIMEOUT,
		        error: function() {
		          self.initializeFailed = true;
		        },
		        dataType: 'json',
		        success: function(data) {
		         if(data.error){
		           self.initializeFailed = true;
		           self.props.eventHandlers.hideLoadingIcon();
		           self.setState({statusMessage: ""});
		           var message = self.props.i18nLib.t('server_messages.' + data.code, data) || data.error;
		           self.props.eventHandlers.displayError(message);
		         }
		        },
		        type: 'POST'
		     });

	},
	loadSourceProjects:  function(id){
		appActions.loadProjectData.triggerPromise().then(function(data) {
		    this.setState({statusMessage: ""});
    		if(data.documentMappingStatus.status == "COMPLETED"){
    			clearInterval(id);
    			this.props.eventHandlers.hideLoadingIcon();
        		this.updateProject(data.documentMappings);
    		} else{
    		    var message = this.props.i18nLib.t('server_messages.' + data.documentMappingStatus.code, data.documentMappingStatus) || data.documentMappingStatus.message;
    		    if(data.documentMappingStatus.status === 'FAILED_WITH_ERROR'){
    		       clearInterval(id);
    		       this.initializeFailed = true;
		           this.props.eventHandlers.hideLoadingIcon();
		           this.props.eventHandlers.displayError(message);
    		    }else{
    		       this.setState({statusMessage: message});
    		    }
    		}
    	}.bind(this))["catch"](function(err) {
    	    clearInterval(id);
    		this.props.eventHandlers.hideLoadingIcon();
    		this.props.eventHandlers.displayError(this.props.i18nLib.t('wizard.choose_projects.msg_error_select_project'));
    	}.bind(this));
	},
	loadProjects: function(){
		var self = this;
		$.ajax({
	        url: '/importer/data/destination/project',
	        dataType: 'json',
	        success: function(result) {
	            self.setState({destinationProjects: result},  function() {
                    self.forceUpdate();
                });
	        },
	        type: 'GET'
	     });
	},
	showSimilarProjectsDialog: function(event) {
	   var projectMapping = _.find(this.state.projectData, function(item) { return item.id === event.target.dataset.id;});
     this.resetSimilarProjectSelected();
	   this.setState({showSimilarProjects: !this.state.showSimilarProjects, projectMapping: projectMapping});
	},
	resetMapping: function(event) {
	   var projectData = this.state.projectData;
       var projectMapping = _.find(projectData, function(m) { return m.id === event.target.dataset.id});
       projectMapping.destinationDocument = null;
       this.setState({projectData: projectData});
	},
	projectHasBeenUpdated: function(iatiIdentifier) {
	   return  _.find(this.props.projectWithUpdates, function(project) { return project.projectIdentifier === iatiIdentifier});
	},
	openProjectPreview: function(event) {
	    var projectMapping = _.find(this.state.projectData, function(m) { return m.id === event.target.dataset.id});
	    if (projectMapping.sourceDocument) {
	        this.setState({showSourceProjectPreview: !this.state.showSourceProjectPreview, projectMapping: projectMapping});
        }
	},
	closeProjectPreview: function() {
	    this.setState({showSourceProjectPreview: false, projectMapping: null});
	},
	openDestinationProject: function(event) {
	    var projectMapping = _.find(this.state.projectData, function(m) { return m.id === event.target.dataset.id});
	    if (projectMapping.destinationDocument) {
	        var win = window.open(appConfig.AMP_ACTIVITY_URL + '~activityId=' + projectMapping.destinationDocument.stringFields.id, '_blank');
	        win.focus();
	    }
	},
	render: function () {
        var newProjects = [];
        var existingProjects = [];
        var language = this.props.i18nLib.lng() || 'en';
        var statusMessage = this.state.statusMessage.length > 0 ? <div className="alert alert-info" role="alert">{this.state.statusMessage}</div> : "";

        if (this.state.projectData) {
           $.map(this.state.projectData, function (item, i) {
                if (item.operation == OPERATION_INSERT) {
                    var sourceDocumentTitle = common.getTitle(item.sourceDocument, this.props.i18nLib.lng());
                    var sourceTranslatedDocumentTitle = null;
                    if (common.shouldTranslateTitle(item.sourceDocument, this.props.i18nLib.lng())) {
                      sourceTranslatedDocumentTitle = common.getTranslation(item.sourceDocument, sourceDocumentTitle, this.props.i18nLib.lng());
                    }
                    newProjects.push(<tr key={i} className={this.projectHasBeenUpdated(item.sourceDocument.identifier) ? "updated-project" : ""}>
                        <td>
                           <input aria-label="Source" className="source"  type="checkbox" checked={item.selected} onChange={this.handleToggle.bind(this, item)} />
                        </td>
                        <td>
                            <span className="glyphicon glyphicon-eye-open glyphicon-eye-open-custom" data-id={item.id} onClick={this.openProjectPreview}></span>
                        </td>
                        <td>
                        {item.sourceDocument.identifier}
                        </td>
                        <td>
                          {sourceDocumentTitle}
                          {sourceTranslatedDocumentTitle && sourceTranslatedDocumentTitle.length > 0 &&
                          <Tooltip i18nLib={this.props.i18nLib}
                                   tooltip={sourceTranslatedDocumentTitle}
                                   image={true}
                                   classes="france-flag"
                          />}
                        </td>
                            <td className="no-left-padding">
                            {item.destinationDocument &&
                               <span className="glyphicon glyphicon-eye-open glyphicon-eye-open-custom" data-id={item.id} onClick={this.openDestinationProject}></span>
                            }
                            </td>
                        <td className="no-right-padding">
                            {(this.state.destinationProjects && this.state.destinationProjects.length > 0) &&
                                <AutoComplete context={constants.CHOOSE_PROJECTS} options={this.state.destinationProjects} display="title" language={language} placeholder="" refId="destSearch" onSelect={this.handleAutocompleteToggle.bind(this, item)} value={item.destinationDocument ? common.getTitle(item.destinationDocument) : ''}/>
                            }
                       </td>

                        <td className="no-left-padding"><span className="glyphicon glyphicon-remove glyphicon-remove-custom" data-id={item.id} onClick={this.resetMapping}></span></td>
                        <td>
                           { item.projectsWithSimilarTitles && item.projectsWithSimilarTitles.length > 0 &&
                            <span className="badge" onClick={this.showSimilarProjectsDialog} data-id={item.id} data-toggle="modal" data-target="#similarProjects">{item.projectsWithSimilarTitles.length}</span>
                           }
                        </td>
                        <td>
                            <input aria-label="override-title" className="override-title"  type="checkbox" checked={item.overrideTitle} onChange={this.handleOverrideTitle.bind(this, item)} />
                         </td>
                    </tr>);
                } else {
                    var classes = item.destinationDocument.allowEdit ? "" : "warning not-active" ;
                    if (this.projectHasBeenUpdated(item.sourceDocument.identifier)) {
                        classes += " updated-project";
                    }
                    var sourceDocumentTitle = common.getTitle(item.sourceDocument, this.props.i18nLib.lng());
                    var sourceTranslatedDocumentTitle = null;
                    if (common.shouldTranslateTitle(item.sourceDocument, this.props.i18nLib.lng())) {
                      sourceTranslatedDocumentTitle = common.getTranslation(item.sourceDocument, sourceDocumentTitle, this.props.i18nLib.lng());
                    }
                    existingProjects.push(<tr key={i} className = {classes} >
                        <td>
                          <input aria-label="Source" className="source" type="checkbox" checked={item.selected} onChange={this.handleToggle.bind(this, item)} />
                        </td>
                         <td>
                            <span className="glyphicon glyphicon-eye-open glyphicon-eye-open-custom" data-id={item.id} onClick={this.openProjectPreview}></span>
                        </td>
                        <td>
                        {item.sourceDocument.identifier}
                        </td>
                        <td>{item.destinationDocument.allowEdit ? "" : " * " }
                            {sourceDocumentTitle}
                            {sourceTranslatedDocumentTitle && sourceTranslatedDocumentTitle.length > 0 &&
                            <Tooltip i18nLib={this.props.i18nLib}
                                     tooltip={sourceTranslatedDocumentTitle}
                                     image={true}
                                     classes="france-flag"
                            />}
                        </td>
                        <td>
                            {item.destinationDocument &&
                                <span className="glyphicon glyphicon-eye-open glyphicon-eye-open-custom" data-id={item.id} onClick={this.openDestinationProject}></span>
                             }
                        </td>
                        <td>
                            {common.getTitle(item.destinationDocument)}
                        </td>
                        <td>
                          <input aria-label="override-title" className="override-title"  type="checkbox" checked={item.overrideTitle} onChange={this.handleOverrideTitle.bind(this, item)} />
                        </td>
                    </tr>);
                }
            }.bind(this));
        };

        return (
           <div>
             <div className="panel panel-default">
                <div className="panel-heading"><strong>{this.props.i18nLib.t('wizard.choose_projects.choose_projects')}</strong></div>
                <div className="panel-body">
                    {statusMessage}
                    <div className="panel panel-success">
                      <div className="panel-heading">{this.props.i18nLib.t('wizard.choose_projects.new_projects')} <i>({newProjects.length } {this.props.i18nLib.t('wizard.choose_projects.choose_projects')})</i></div>
                        <div className="panel-body">
                         <SimilarProjectsDialog projectMapping={this.state.projectMapping} getTitle={common.getTitle} mapSelectedProject={this.mapSelectedProject} updateSimilarProjectSelected={this.updateSimilarProjectSelected} similarProjectSelected={this.state.similarProjectSelected} {...this.props} />
                         {this.state.showSourceProjectPreview && this.state.projectMapping && this.state.sourceFieldsData.length > 0  &&
                            <ProjectPreview closeProjectPreview={this.closeProjectPreview.bind(this)} project={this.state.projectMapping.sourceDocument} i18nLib = {this.props.i18nLib} sourceFieldsData = {this.state.sourceFieldsData} />
                         }
                         <table className="table">
                                <thead>
                                    <tr>
                                        <th>
                                            <input type="checkbox" checked={this.checkAll(OPERATION_INSERT)} onChange={this.selectAllNew} />
                                            {this.props.i18nLib.t('wizard.choose_projects.import')}
                                        </th>
                                         <th></th>
                                         <th className="id-column-width">
                                          <Tooltip i18nLib={this.props.i18nLib} tooltip={this.props.i18nLib.t('wizard.choose_projects.iati_id_tooltip')}/> {this.props.i18nLib.t('wizard.choose_projects.iati_id')}
                                        </th>
                                        <th>
                                          <Tooltip i18nLib={this.props.i18nLib} tooltip={this.props.i18nLib.t('wizard.choose_projects.source_project_tooltip')}/> {this.props.i18nLib.t('wizard.choose_projects.source_project')}
                                        </th>
                                          <th></th>
                                        <th>
                                          <Tooltip i18nLib={this.props.i18nLib} tooltip={this.props.i18nLib.t('wizard.choose_projects.destination_project_tooltip')}/> {this.props.i18nLib.t('wizard.choose_projects.destination_project')}
                                        </th>
                                        <th></th>
                                          <th><Tooltip i18nLib={this.props.i18nLib} tooltip={this.props.i18nLib.t('wizard.choose_projects.similar_titles_tooltip')}/>{this.props.i18nLib.t('wizard.choose_projects.similar_titles')}</th>
                                        <th>
                                        <Tooltip i18nLib={this.props.i18nLib} tooltip={this.props.i18nLib.t('wizard.choose_projects.override_title_tooltip')}/> <input type="checkbox" checked={this.overrideAll(OPERATION_INSERT)} onChange={this.overrideTitleAll.bind(this, OPERATION_INSERT)} />
                                        {this.props.i18nLib.t('wizard.choose_projects.override_title')}
                                        </th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {newProjects}
                                </tbody>
                            </table>
                        </div>
                    </div>
                    <div className="panel panel-danger">
                        <div className="panel-heading">{this.props.i18nLib.t('wizard.choose_projects.existing_projects')} <i>({existingProjects.length } {this.props.i18nLib.t('wizard.choose_projects.choose_projects')})</i></div>
                        <div className="panel-body">
                              <div className="alert alert-info" role="alert">{this.props.i18nLib.t('wizard.choose_projects.msg_project_not_editable')}</div>
                            <table className="table">
                                <thead>
                                    <tr>
                                        <th>
                                            <input type="checkbox" checked={this.checkAll(OPERATION_UPDATE)} onChange={this.selectAllExisting} />
                                            {this.props.i18nLib.t('wizard.choose_projects.update')}
                                        </th>
                                        <th> </th>
                                         <th className="id-column-width">
                                            <Tooltip i18nLib={this.props.i18nLib} tooltip={this.props.i18nLib.t('wizard.choose_projects.iati_id_tooltip')}/>{this.props.i18nLib.t('wizard.choose_projects.iati_id')}
                                        </th>
                                        <th>
                                            <Tooltip i18nLib={this.props.i18nLib} tooltip={this.props.i18nLib.t('wizard.choose_projects.source_project_tooltip')}/> {this.props.i18nLib.t('wizard.choose_projects.source_project')}
                                        </th>
                                         <th></th>
                                        <th>
                                            <Tooltip i18nLib={this.props.i18nLib} tooltip={this.props.i18nLib.t('wizard.choose_projects.destination_project_tooltip')}/> {this.props.i18nLib.t('wizard.choose_projects.destination_project')}
                                        </th>
                                        <th>
                                            <Tooltip i18nLib={this.props.i18nLib} tooltip={this.props.i18nLib.t('wizard.choose_projects.override_title_tooltip')}/><input type="checkbox" checked={this.overrideAll(OPERATION_UPDATE)} onChange={this.overrideTitleAll.bind(this, OPERATION_UPDATE)} />
                                        {this.props.i18nLib.t('wizard.choose_projects.override_title')}
                                        </th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {existingProjects}
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
                <div className="buttons">
                    <div className="row">
                        <div className="col-md-6">
                           <button ref="previousButton"   className="btn btn-success navbar-btn btn-custom btn-previous" type="button" onClick={this.handlePrevious}>{this.props.i18nLib.t('wizard.choose_projects.previous')}</button>
                         </div>
                       <div className="col-md-6">
                          <button ref="nextButton"  disabled = { _.where(this.state.projectData, {selected: true}).length > 0 ? "" : "disabled"} className="btn btn-success navbar-btn btn-custom" type="button" onClick={this.handleNext}>{this.props.i18nLib.t('wizard.choose_projects.next')}</button>
                       </div>
                   </div>
                </div>
                </div>
                </div>
            ); } });

module.exports = ChooseProjects;
