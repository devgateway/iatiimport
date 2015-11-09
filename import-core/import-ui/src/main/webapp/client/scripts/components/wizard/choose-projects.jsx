var React = require('react');
var typeaheadimpl = require('./../../utils/typeaheadimpl');
var projectStore = require('./../../stores/ProjectStore');
var reactAsync = require('react-async');
var Reflux = require('reflux');
var appActions = require('./../../actions');
var Router = require('react-router');
var Link = Router.Link;
var AutoComplete = require('./autocomplete');
var _ = require('lodash/dist/lodash.underscore');
var constants = require('./../../utils/constants');

var ChooseProjects = React.createClass({
    mixins: [
        Reflux.ListenerMixin
    ],
    getInitialState: function() {
       return {projectData: [], destinationProjects: []};
    },
    componentWillMount: function () {
     this.props.eventHandlers.updateCurrentStep(constants.CHOOSE_PROJECTS);
     this.listenTo(projectStore, this.updateProject);             
     this.loadData();
    },
    updateProject: function (data) {        
        this.setState({
            projectData: data
        });
    }, 
    loadData: function(){
    	this.props.eventHandlers.showLoadingIcon();
    	appActions.loadProjectData.triggerPromise().then(function(data) { 
    		this.props.eventHandlers.hideLoadingIcon();                       
    		this.updateProject(data);                
    	}.bind(this)).catch(function(err) {
    		this.props.eventHandlers.hideLoadingIcon();    		
    		this.props.eventHandlers.displayError(this.props.i18nLib.t('wizard.choose_projects.msg_error_select_project'));
    	}.bind(this)); 
    	this.loadProjects();
    },  
    selectAll: function(){ 
    },
    selectProject: function(event) {       
       this.props.eventHandlers.selectProject(event);
    },
    selectAllNew: function(event){
        this.selectAll(event.target.checked, 'INSERT');
    },
    selectAllExisting: function(){
        this.selectAll(event.target.checked, 'UPDATE');
    },
    selectAll: function(checked, operation){
    	_.each(this.state.projectData, function(item){ 
    		if(item.operation === operation)
    			item.selected = checked;
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
    overrideTitleAll: function(event){
    	_.each(this.state.projectData, function(item){   		
    			item.overrideTitle = event.target.checked;
    	});
    	this.forceUpdate();
    },
    handleAutocompleteToggle: function(item, datum) {
        item.destinationDocument = datum;
        this.forceUpdate();
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
	loadProjects: function(){
		var self = this;
		$.ajax({
	        url: '/importer/data/destination/project',	       
	        dataType: 'json',
	        success: function(result) { 
	        	self.setState({destinationProjects: result});	 
	        	self.forceUpdate();
	        },
	        type: 'GET'
	     });
	},	
    render: function () {  
        var newProjects = [];
        var existingProjects = [];
        var language = this.props.i18nLib.lng() || 'en';        
        if (this.state.projectData) {
           $.map(this.state.projectData, function (item, i) {
                if (item.operation == 'INSERT') {
                    newProjects.push(<tr key={i}>
                        <td>
                           <input aria-label="Source" className="source"  type="checkbox" checked={item.selected} onChange={this.handleToggle.bind(this, item)} />
                        </td>                        
                        <td>
                            {item.sourceDocument.multilangFields.title[language]} 
                        </td>
                        <td>                            
                          <AutoComplete context={constants.CHOOSE_PROJECTS} options={this.state.destinationProjects} display="title" language={language} placeholder="" refId="destSearch" onSelect={this.handleAutocompleteToggle.bind(this, item)} value={item.destinationDocument}/>                            
                        </td>
                        <td>
                            <input aria-label="override-title" className="override-title"  type="checkbox" checked={item.overrideTitle} onChange={this.handleOverrideTitle.bind(this, item)} />
                         </td>
                    </tr>);
                } else {
                    existingProjects.push(<tr key={i}>
                        <td>
                          <input aria-label="Source" className="source" type="checkbox" checked={item.selected} onChange={this.handleToggle.bind(this, item)} />
                        </td>
                        <td>
                            {item.sourceDocument.multilangFields.title[language]}
                        </td>
                        <td>
                            {item.destinationDocument.multilangFields.title[language]}
                        </td>
                        <td>
                        <input aria-label="override-title" className="override-title"  type="checkbox" checked={item.overrideTitle} onChange={this.handleOverrideTitle.bind(this, item)} />
                     </td>
                    </tr>);
                }
            }.bind(this));
        };

        return (
            <div className="panel panel-default">
                <div className="panel-heading"><strong>{this.props.i18nLib.t('wizard.choose_projects.choose_projects')}</strong></div>
                <div className="panel-body">
                
                    <div className="panel panel-success">
                        <div className="panel-heading">{this.props.i18nLib.t('wizard.choose_projects.new_projects')}</div>
                        <div className="panel-body">
                            <table className="table">
                                <thead>
                                    <tr>
                                        <th>
                                            <input type="checkbox" checked={this.checkAll('INSERT')} onChange={this.selectAllNew} />
                                            {this.props.i18nLib.t('wizard.choose_projects.import')}
                                        </th>                                        
                                        <th>
                                            {this.props.i18nLib.t('wizard.choose_projects.source_project')}
                                        </th>
                                        <th>
                                            {this.props.i18nLib.t('wizard.choose_projects.destination_project')}
                                        </th>
                                        <th>
                                        <input type="checkbox" checked={this.overrideAll('INSERT')} onChange={this.overrideTitleAll} />
                                        Override Title</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {newProjects}
                                </tbody>
                            </table>
                        </div>
                    </div>
                    <div className="panel panel-danger">
                        <div className="panel-heading">{this.props.i18nLib.t('wizard.choose_projects.existing_projects')}</div>
                        <div className="panel-body">
                            <table className="table">
                                <thead>
                                    <tr>
                                        <th>
                                            <input type="checkbox" checked={this.checkAll('UPDATE')} onChange={this.selectAllExisting} />
                                            {this.props.i18nLib.t('wizard.choose_projects.update')}
                                        </th>
                                        
                                        <th>
                                            {this.props.i18nLib.t('wizard.choose_projects.source_project')}
                                        </th>
                                        <th>
                                            {this.props.i18nLib.t('wizard.choose_projects.destination_project')}
                                        </th>
                                        <th>
                                        <input type="checkbox" checked={this.overrideAll('UPDATE')} onChange={this.overrideTitleAll} />
                                        Override Title</th>
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
            ); } }); 
            
module.exports = ChooseProjects;