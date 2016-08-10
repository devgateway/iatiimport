var React = require('react');
var workflowStore = require('./../stores/WorkflowStore');
var Reflux = require('reflux');
var appActions = require('./../actions');
var Router = require('react-router');
var Link = Router.Link;
var WorkflowSelector = React.createClass({
    mixins: [Reflux.ListenerMixin
    ],
    getInitialState: function() {
		return {workflowData: []};
	},
    componentDidMount   : function () {  
    	this.listenTo(workflowStore, this.updateWorkflowData);
    	this.loadData();
    },
    updateWorkflowData: function(data){
       this.setState({workflowData:data});
    },
    loadData: function(){
       $.get('/importer/import/wipeall', function(){}); 
    	appActions.loadWorkflowData.triggerPromise().then(function(data) {
    		this.updateWorkflowData(data);
    	}.bind(this));
     },
    render: function () {  
    	var links = [];    	
    	if (this.state.workflowData) {    		
            $.map(this.state.workflowData, function(workflow, i) { 
            	if(workflow.enabled){            		
            		var link  = <li className="workflow-link"><a  href={"#/wizard/" + workflow.sourceProcessor.name + "/" + workflow.destinationProcessor.name} >{window.i18nLib.t(workflow.translationKey)}</a></li>;
            		links.push(link);	
            	}            	
            }.bind(this));
            
    	}
    	
        return (        		
        		  <div className="jumbotron">
        		    <h3>{window.i18nLib.t("header.select_import_process")}</h3> 
        		    <ul className="list-unstyled workflow-selector">
        		    {links}
        		    </ul>        		    
        	      </div>       		
        );
    }
});

module.exports = WorkflowSelector;