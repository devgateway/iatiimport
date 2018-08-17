var React = require('react');
var workflowStore = require('./../stores/WorkflowStore');
var Reflux = require('reflux');
var appActions = require('./../actions');
var constants = require('./../utils/constants');
var LandingPage = React.createClass({
    mixins: [Reflux.ListenerMixin
    ],
    getInitialState: function() {
		return {importType: null};
	}, 
    componentDidMount: function() {
	   this.resetSession();	   
	},
	resetSession: function() {
	    $.get('/importer/import/wipeall', function(){}); 
	},
	onChange: function(event) {    
	   this.setState({importType: event.target.value});	   
	},
	next: function() {
	  if (this.state.importType === constants.IMPORT_TYPE_MANUAL) {
        window.location = '#/manual';
      } else {
        window.location = '#/wizard/automatic/AMP/selectdatasource';
      }
	},
    render: function () {  
      return (   <div className="container">     		
        		  <div className="jumbotron">
        		    <h3>{window.i18nLib.t("import_type.select_import_type")}</h3> 
        		    <select onChange={this.onChange} className="form-control import-type-select" >
        		        <option value=""></option>
        		        <option value={constants.IMPORT_TYPE_MANUAL}>{this.props.i18nLib.t('import_type.manual')}</option>
        		        <option value={constants.IMPORT_TYPE_AUTOMATIC}>{this.props.i18nLib.t('import_type.automatic')}</option>
        		     </select>
        		       <br/>
        		       <br/>
        		   {this.state.importType &&
        		       <span>{this.props.i18nLib.t('import_type.description_' + this.state.importType)} </span> 
        		   }    
        		   
        		   <div className="buttons">
                   <button disabled = {this.state.importType ? "" : "disabled"} className="btn btn-success navbar-btn btn-custom" type="button" onClick={this.next}>{this.props.i18nLib.t('import_type.next')}</button>
                    </div>
        		   
        	      </div>                  
        	</div>
        );
    }
});

module.exports = LandingPage;