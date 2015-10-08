var React = require('react');
var Reflux = require('reflux');
var Router = require('react-router');
var Link = Router.Link;
var _ = require('lodash/dist/lodash.underscore');
var moment = require('moment');
var Header = require('./../layout/header');
var Footer = require('./../layout/footer');
var appConfig = require('./../../conf');
var Router = require('react-router');
var RouteHandler = Router.RouteHandler;
var appActions = require('./../../actions');
var workflowListStore = require('./../../stores/WorkflowStore');
var Pagination = require('react-bootstrap/lib/Pagination');
var WorkflowList = React.createClass({ 
  mixins: [
        Reflux.ListenerMixin
    ],
  getInitialState: function() {
    return {     
      workflowListData: []      
    };
  },  
   componentWillMount: function () {
     this.listenTo(workflowListStore, this.updateWorkflowList);             
     this.loadData();
    },
    updateWorkflowList: function (data) {                  
        this.setState({
            workflowListData: data
        });
    }, 
   loadData: function(){    
     appActions.loadWorkflowData().then(function(data) {                                       
        this.updateWorkflowList(data);                
      }.bind(this)).catch(function(err) {       
        console.log('Error loading workflows');
     }.bind(this));
  },
  render: function() {  
        var workflowList = [];               
        if (this.state.workflowListData && this.state.workflowListData.length > 0) {        
        $.map(this.state.workflowListData, function (workflow, i) {           
            workflowList.push(<tr key={i}>
                   <td>
                        {workflow.sourceProcessor.label}
                    </td>
                    <td>
                        {workflow.destinationProcessor.label}
                    </td>
                    <td>
                        {workflow.description}
                    </td>
                    <td>
                       {workflow.enabled ? "Yes"  : "No"}
                    </td>
                </tr>);
            }.bind(this));
        }
      
    return (
     <div className="container " >
      <h2>Workflows</h2>      
         <table className="table file-list">
                    <thead>
                        <tr>
                           <th>
                                 Source Processor
                            </th>
                            <th>
                                 Destination Processor
                            </th>
                            <th>
                               Description
                            </th>
                            <th>
                                Enabled
                            </th>
                        </tr>
                    </thead>
                    <tbody>
                        {workflowList}
                    </tbody>
                </table>                
      </div>
      );
  }
});
module.exports = WorkflowList;