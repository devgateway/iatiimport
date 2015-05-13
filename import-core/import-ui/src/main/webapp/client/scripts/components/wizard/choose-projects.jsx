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
    projectSelected: function(selected){
      console.log(selected);
    },
    updateProject: function (data) {
        this.setState({
            projectData: data.projectData
        });
    },
    render: function () {
        var newProjects = [];
        var existingProjects = [];        
        if (this.state.projectData) {
           $.map(this.state.projectData, function (item, i) {
                if (item.status == 'NEW') {
                    newProjects.push(<tr key={i}>
                        <td>
                            <input aria-label="Field1" type="checkbox"/>
                        </td>
                        <td>
                            {item.title}
                        </td>
                        <td>                            
                            <AutoComplete url="/mockup/activity_list.json" display="title" placeholder="Destination Project Name 1" refId="destSearch" onSelect={this.projectSelected} /> 
                        </td>
                    </tr>);
                } else {
                    existingProjects.push(<tr key={i}>
                        <td>
                            <input aria-label="Field1" type="checkbox"/>
                        </td>
                        <td>
                            {item.title}
                        </td>
                        <td>
                           <AutoComplete url="/mockup/activity_list.json" display="title" placeholder="Destination Project Name 1" refId="destSearch" onSelect={this.projectSelected} />                            
                        </td>
                    </tr>);
                }
            }.bind(this));
        };
        return (
            <div className="panel panel-default">
                <div className="panel-heading"><strong>Choose Projects</strong></div>
                <div className="panel-body">
                
                    <div className="panel panel-success">
                        <div className="panel-heading">New Projects</div>
                        <div className="panel-body">
                            <table className="table">
                                <thead>
                                    <tr>
                                        <th>
                                            Import
                                        </th>
                                        <th>
                                            Source Project
                                        </th>
                                        <th>
                                            Destination Project
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
                        <div className="panel-heading">Existing Projects</div>
                        <div className="panel-body">
                            <table className="table">
                                <thead>
                                    <tr>
                                        <th>
                                            Update
                                        </th>
                                        <th>
                                            Source Project
                                        </th>
                                        <th>
                                            Destination Project
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
                    <button className="btn btn-success navbar-btn btn-custom" type="button">Next >></button>
                </div>
                </div>
            ); } }); 
            
            module.exports = ChooseProjects;