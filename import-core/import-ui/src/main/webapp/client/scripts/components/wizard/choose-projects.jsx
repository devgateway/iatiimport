var React = require('react');
var typeaheadimpl = require('./../../utils/typeaheadimpl');
var projectStore = require('./../../stores/ProjectStore');
var reactAsync = require('react-async');
var Reflux = require('reflux');
var appActions = require('./../../actions');
var Router = require('react-router');
var Link = Router.Link;
var AutoComplete = require('./autocomplete');
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
    render: function () {
        var newProjects = [];
        var existingProjects = [];        
        if (this.state.projectData) {
           $.map(this.state.projectData, function (item, i) {
                if (item.operation == 'INSERT') {
                    newProjects.push(<tr key={i}>
                        <td>
                            <input aria-label="Source" className="source" value={item.sourceDocument.identifier} type="checkbox" onChange={this.selectProject} />
                        </td>
                        <td>
                            {item.sourceDocument.title}
                        </td>
                        <td>                            
                            <AutoComplete url="/importer/data/destination/project" display="title" placeholder="Destination Project Name 1" refId="destSearch" onSelect={this.props.eventHandlers.updateProjects} /> 
                        </td>
                    </tr>);
                } else {
                    existingProjects.push(<tr key={i}>
                        <td>
                            <input aria-label="Field1"  className="source" type="checkbox" value={item.sourceDocument.identifier} onChange={this.selectProject}/>
                        </td>
                        <td>
                            {item.sourceDocument.title}
                        </td>
                        <td>
                            {item.destinationDocument.title}
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
                    <button className="btn btn-success navbar-btn btn-custom" type="button" onClick={this.props.eventHandlers.chooseProjects}>{this.props.i18nLib.t('wizard.choose_projects.next')}</button>
                </div>
                </div>
            ); } }); 
            
            module.exports = ChooseProjects;