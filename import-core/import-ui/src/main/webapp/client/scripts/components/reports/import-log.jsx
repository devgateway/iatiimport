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
var Pagination = require('react-bootstrap/lib/Pagination');
var PreviousImports = React.createClass({ 
  mixins: [
        Reflux.ListenerMixin,Router.Navigation
    ],
   getInitialState: function() {
    return {     
      importLog: {       
      },
      pageSize:10, 
      activePage:1
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
    handlePageSelect:function(event, selectedEvent){    
	      this.setState({
	      activePage: selectedEvent.eventKey
	      });
	      this.loadData();
    },
    loadData: function(){
     var sortParams = {size:10, page:this.state.activePage - 1}  
     appActions.loadImportLog(this.props.params.id,sortParams).then(function(data) {                                      
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
        var itemsSize = this.state.importLog.totalElements > this.state.pageSize?(this.state.importLog.totalElements / this.state.pageSize).toFixed(0) : 1;               
        if (this.state.importLog.content && this.state.importLog.content.length > 0) {        
          $.map(this.state.importLog.content, function (project, i) {                      
            projects.push(<tr key={project.id}>
                   <td>
                        {project.title}
                    </td>
                    <td>
                        {project.notes}
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
      <span>File Name: {}</span>     
         <table className="table file-list">
                    <thead>
                        <tr>
                           <th>
                                 Project Name
                            </th>                            
                            <th>
                                 Note
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
         <Pagination bsSize='small' items={itemsSize} activePage={this.state.activePage} onSelect={this.handlePageSelect} />
        <div className="buttons">
          <button className="btn btn-warning navbar-btn btn-custom" type="button" onClick={this.goBackToList}>Back to Import List</button>&nbsp;         
        </div>
      </div>
      );
  }
});
module.exports = PreviousImports;