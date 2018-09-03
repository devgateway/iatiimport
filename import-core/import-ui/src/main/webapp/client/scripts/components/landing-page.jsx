var React = require('react');
var workflowStore = require('./../stores/WorkflowStore');
var Reflux = require('reflux');
var constants = require('./../utils/constants');
var formActions = require('./../actions/form');
var LandingPage = React.createClass({
    mixins: [Reflux.ListenerMixin
    ],
    getInitialState: function() {
		return {
		    reportingOrgsWithUpdates: []
		};
	}, 
	updateReportingOrgsWithUpdates: function(data) {        
	     this.setState({reportingOrgsWithUpdates: data})
	},       
	loadData: function(){       
	     formActions.loadReportingOrgsWithUpdates().then(function(data) {   
	          this.updateReportingOrgsWithUpdates(data);                
	     }.bind(this))["catch"](function(err) {       
	          console.log('Error loading reporting orgs');
	     }.bind(this));      
	},
    componentDidMount: function() {
       this.loadData();
	   this.resetSession();	   
	},
	resetSession: function() {
	    $.get('/importer/import/wipeall', function(){}); 
	},
	selectImportType: function(importType, event){
	     if (importType === constants.IMPORT_TYPE_MANUAL) {
	        window.location = '#/manual';
	      } else {
	        window.location = '#/wizard/automatic/AMP/selectdatasource';
	      }  
	},
    render: function () {
        return (<div className="container">        		
        		 <div className="row">
                 <div className="col-md-2">
                 </div>
                  <div className="col-md-4 landing-page-left">
                   <button className="btn btn-success navbar-btn btn-custom" type="button"
                           onClick={this.selectImportType.bind(this, constants.IMPORT_TYPE_AUTOMATIC)}
                           data-toggle="tooltip" data-placement="bottom"
                           title={this.props.i18nLib.t('import_type.tooltip_' + constants.IMPORT_TYPE_AUTOMATIC)}
                   >
                     {this.props.i18nLib.t('import_type.automatic')}</button>
                    <br/> <br/>
                   <span>{this.props.i18nLib.t('import_type.description_' + constants.IMPORT_TYPE_AUTOMATIC)} </span><br/>
                   <br/>
                   <br/>
                   {this.state.reportingOrgsWithUpdates && this.state.reportingOrgsWithUpdates.length > 0 &&
                       <div className="reporting-orgs-with-updates">                      
                         {this.state.reportingOrgsWithUpdates.map(function(org, index){
                             return (<span>{org.name}{index < this.state.reportingOrgsWithUpdates.length -1 ? ", " : " "}</span>)
                            }.bind(this))
                         }
                         {this.props.i18nLib.t('import_type.data_updated')}                   
                       </div>                      
                   }
                   
               </div>
               <div className="col-md-4 landing-page-right">
                   <button className="btn btn-success navbar-btn btn-custom" type="button"
                           onClick={this.selectImportType.bind(this, constants.IMPORT_TYPE_MANUAL)}
                           data-toggle="tooltip" data-placement="bottom"
                           title={this.props.i18nLib.t('import_type.tooltip_' + constants.IMPORT_TYPE_MANUAL)}
                   >
                     {this.props.i18nLib.t('import_type.manual')}</button> <br/> <br/>
               <span>{this.props.i18nLib.t('import_type.description_' + constants.IMPORT_TYPE_MANUAL)} </span> 
               </div>
                <div className="col-md-2">
               </div>
                </div>        		   
        	  </div>          
        	
        );
    }
});

module.exports = LandingPage;
