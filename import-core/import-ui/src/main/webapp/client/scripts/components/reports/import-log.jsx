var React = require('react');
var Reflux = require('reflux');
var _ = require('lodash/dist/lodash.underscore');
var moment = require('moment');
var Header = require('./../layout/header');
var Footer = require('./../layout/footer');
var appConfig = require('./../../conf');
var Router = require('react-router');
var RouteHandler = Router.RouteHandler;
var appActions = require('./../../actions');
var importLogStore = require('./../../stores/ImportLogStore');
var PreviousImports = React.createClass({ 
  mixins: [
        Reflux.ListenerMixin,Router.Navigation
    ],
   getInitialState: function() {
    return {     
      importLog: {
       projects:[]
      }
    };
   },  
   componentWillMount: function () {
     this.listenTo(importLogStore, this.updateImportLog);             
     this.loadData();
    },
    updateImportLog: function (data) {        
        this.setState({
            importLog: data
        });
    }, 
    loadData: function(){	 
     appActions.loadImportLog.triggerPromise().then(function(data) {                                      
        this.updateImportLog(data);                
      }.bind(this)).catch(function(err) {
        console.log('Error loading import logs');
     }.bind(this));
  },
  goBackToList: function(){
    this.transitionTo('previousimports');
  },
  render: function() {  
        var projects = [];        
        if (this.state.importLog && this.state.importLog.projects.length > 0) {        
          $.map(this.state.importLog.projects, function (project, i) {                      
            projects.push(<tr key={project.id}>
                   <td>
                        {project.title}
                    </td>
                    <td>
                        {project.status}
                    </td>                    
                </tr>);
            });
        }
    return (
     <div className="container " >
      <h2>Import Log </h2> 
      <span>File Name: {this.state.importLog.fileName}</span>     
         <table className="table file-list">
                    <thead>
                        <tr>
                           <th>
                                 Project Name
                            </th>
                            <th>
                                 Status
                            </th>                           
                        </tr>
                    </thead>
                    <tbody>
                        {projects}
                    </tbody>
                </table>
        <div className="buttons">
          <button className="btn btn-warning navbar-btn btn-custom" type="button" onClick={this.goBackToList}>Back to Import List</button>&nbsp;
          
        </div>
      </div>
      );
  }
});
module.exports = PreviousImports;