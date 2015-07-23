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

var ChooseProjects = React.createClass({
    mixins: [
        reactAsync.Mixin, Reflux.ListenerMixin
    ],
    componentDidMount: function () {
        this.listenTo(projectStore, this.updateProject);
    },
    getInitialStateAsync: function () {
        appActions.loadProjectData();

        projectStore.listen(function (data) {
            try {
                return cb(null, {
                    projectData: data.projectData
                });
            } catch (err) {}
        });
    },
    updateProject: function (data) {
        this.setState({
            projectData: data.projectData
        });
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
    handleNext: function() {
        debugger;
        var processedData = this.state.projectData;
        _.each(processedData, function(item){
//            item.sourceDocument.dateFields = {};
        });
        this.props.eventHandlers.chooseProjects(processedData);
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
                            <input aria-label="Source" className="source" value={item.sourceDocument.identifier} type="checkbox" onChange={this.selectProject} />
                            <input aria-label="Source" type="checkbox" checked={item.selected} onChange={this.handleToggle.bind(this, item)} />
                        </td>
                        <td>
                            {item.sourceDocument.multilangFields.title[language]} 
                        </td>
                        <td>                            
                            <AutoComplete url="/importer/data/destination/project" display="title" language={language} placeholder="Destination Project Name 1" refId="destSearch" onSelect={this.handleAutocompleteToggle.bind(this, item)} value={item.destinationDocument}/> 
                            
                        </td>
                    </tr>);
                } else {
                    existingProjects.push(<tr key={i}>
                        <td>
                            <input aria-label="Field1"  className="source" type="checkbox" value={item.sourceDocument.identifier} onChange={this.selectProject}/>
                            <input aria-label="Source"  type="checkbox" checked={item.selected} onChange={this.handleToggle.bind(this, item)} />
                        </td>
                        <td>
                            {item.sourceDocument.multilangFields.title[language]}
                        </td>
                        <td>
                            {item.destinationDocument.multilangFields.title[language]}
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
                    <button className="btn btn-success navbar-btn btn-custom" type="button" onClick={this.handleNext}>{this.props.i18nLib.t('wizard.choose_projects.next')}</button>
                </div>
                </div>
            ); } }); 
            
module.exports = ChooseProjects;