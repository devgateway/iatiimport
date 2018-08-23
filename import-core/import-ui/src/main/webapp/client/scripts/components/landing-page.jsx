var React = require('react');
var workflowStore = require('./../stores/WorkflowStore');
var Reflux = require('reflux');
var appActions = require('./../actions');
var constants = require('./../utils/constants');
var LandingPage = React.createClass({
    mixins: [Reflux.ListenerMixin
    ],
    getInitialState: function() {
		return {};
	}, 
    componentDidMount: function() {
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
                   <button className="btn btn-success navbar-btn btn-custom" type="button" onClick={this.selectImportType.bind(this, constants.IMPORT_TYPE_AUTOMATIC)}>{this.props.i18nLib.t('import_type.automatic')}</button> <br/> <br/>
                   <span>{this.props.i18nLib.t('import_type.description_' + constants.IMPORT_TYPE_AUTOMATIC)} </span> 
               </div>
               <div className="col-md-4 landing-page-right">
                   <button className="btn btn-success navbar-btn btn-custom" type="button" onClick={this.selectImportType.bind(this, constants.IMPORT_TYPE_MANUAL)}>{this.props.i18nLib.t('import_type.manual')}</button> <br/> <br/>
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