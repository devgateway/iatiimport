var React = require('react');
var workflowStore = require('./../../stores/WorkflowStore');
var Reflux = require('reflux');
var NAVBAR_LEFT = 'navbar-left';
var appActions = require('./../../actions');
var SubMenu = require('./sub-menu');
var Router = require('react-router');
var Link = Router.Link;
var WorkflowMenu = React.createClass({
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
    	appActions.loadWorkflowData.triggerPromise().then(function(data) {
    		this.updateWorkflowData(data);
    	}.bind(this));
     },
    render: function () {  
    	var items = [];    	
    	if (this.state.workflowData) {    		
            $.map(this.state.workflowData, function(workflow, i) { 
            	if(workflow.enabled){
            		var label = this.props.i18nLib.t(workflow.translationKey)
                	items.push({name: workflow.label, url: "#/wizard/" + workflow.sourceProcessor.name + "/" + workflow.destinationProcessor.name + '/upload', label: label, type: "workflow-selector-item"});	
            	}            	
            }.bind(this));
            
    	}
    	
        return (
        		<li className="dropdown" role="button" key={this.props.i18nLib.t("header.nav.menu.import_process")}>
        		<Link aria-expanded="true" className="dropdown-toggle"  data-toggle="dropdown"  role="button" to="/">
        		<div className="glyphicon glyphicon-import"></div>{this.props.i18nLib.t("header.nav.menu.import_process")}
        		<span className="caret"></span>                               
        		</Link>
        		  <SubMenu items={items} {...this.props} />
        		</li>
        );
    }
});

module.exports = WorkflowMenu;