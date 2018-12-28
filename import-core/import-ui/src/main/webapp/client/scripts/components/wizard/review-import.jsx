var React = require('react');
var reactAsync = require('react-async');
var Reflux = require('reflux');
var appActions = require('./../../actions');
var Router = require('react-router');

var constants = require('./../../utils/constants');
var importSummaryStore = require('./../../stores/ImportSummaryStore');

var ReviewImport = React.createClass({
	mixins: [Reflux.ListenerMixin],
	getInitialState: function() {
		return {importSummary:{}, importOption: constants.OVERWRITE_ALL_FUNDING, disasterResponse: false};
	 },
	componentDidMount: function() {
		this.props.eventHandlers.updateCurrentStep(constants.REVIEW_IMPORT);
		this.listenTo(importSummaryStore, this.updateImportSummary);
		this.loadData();
	}, 
	updateImportSummary: function(data){
		  this.setState({importSummary: data});
	 },
	loadData: function(){
		    this.props.eventHandlers.showLoadingIcon();
			appActions.loadImportSummary.triggerPromise().then(function(data) {                            
				this.updateImportSummary(data); 			
				this.props.eventHandlers.hideLoadingIcon();
			}.bind(this))["catch"](function(err) {       
				this.hideLoadingIcon(); 
				this.props.eventHandlers.hideLoadingIcon();        
	    		this.props.eventHandlers.displayError(this.props.i18nLib.t('wizard.review_import.msg_error_retrieving_summary'));     
			}.bind(this));
	},
	handlePrevious: function(){
		this.props.eventHandlers.navigateBack();
	},	
    goHome: function(){
        if(confirm(this.props.i18nLib.t('wizard.review_import.question'))) {
            this.props.eventHandlers.goHome();
        }
    }, 
    onImportOptionChange: function(event) {
      if(event.target.checked) {
         this.setState({importOption:event.target.value})
      }
    }, 
    onDisasterReponseChange: function(event) {
        if(event.target.checked) {              
            this.setState({disasterResponse: event.target.value === constants.YES })
         }
       }, 
    import: function() {
     if (this.state.importOption === constants.OVERWRITE_ALL_FUNDING || this.state.importOption === constants.REPLACE_DONOR_FUNDING) {
       var message = this.state.importOption === constants.OVERWRITE_ALL_FUNDING ? this.props.i18nLib.t('wizard.review_import.import_option_overwrite_prompt') : this.props.i18nLib.t('wizard.review_import.import_option_replace_prompt');
       if (confirm(message)) {
           this.props.eventHandlers.launchImport(this.state.importOption, this.state.disasterResponse);
       }
     } else {
       this.props.eventHandlers.launchImport(this.state.importOption, this.state.disasterResponse);
     }       
    },  
    hasMoreVersions: function() {
        return this.props.versions &&  this.props.processedVersions && (this.props.processedVersions < this.props.versions);
    },
    processNextVersion: function() {
      this.props.eventHandlers.processNextVersion();  
    },
    render: function () {
    	var statusMessage = this.props.statusMessage.length > 0 ? <div className="alert alert-info" role="alert">{this.props.statusMessage}</div> : "";
        return (
            <div className="panel panel-default">
                <div className="panel-heading"><strong>{this.props.i18nLib.t('wizard.review_import.review_import')}</strong></div>
                <div className="panel-body">
                   {statusMessage}                  
                    <div className="row">
                        <div className="col-sm-6 col-md-6">
                         <div className="panel panel-default panel-body">
                           <label>{this.props.i18nLib.t('wizard.review_import.import_option')}</label><span className="import-option-explanation">{this.props.i18nLib.t('wizard.review_import.import_option_explanation')}</span>
                           <div className="radio">
                             <label><input type="radio" name="importOption" value={constants.OVERWRITE_ALL_FUNDING} onChange={this.onImportOptionChange} checked={constants.OVERWRITE_ALL_FUNDING === this.state.importOption}/>{this.props.i18nLib.t('wizard.review_import.import_option_overwrite_all')}</label> <br/>
                             <label className="import-option-explanation">{this.props.i18nLib.t('wizard.review_import.import_option_overwrite_all_explanation')}</label>                             
                            </div>
                            <div className="radio">
                              <label><input type="radio" name="importOption" value={constants.ONLY_ADD_NEW_FUNDING} onChange={this.onImportOptionChange} checked={constants.ONLY_ADD_NEW_FUNDING === this.state.importOption}/>{this.props.i18nLib.t('wizard.review_import.import_option_add_missing')}</label><br/>
                              <label className="import-option-explanation">{this.props.i18nLib.t('wizard.review_import.import_option_add_missing_explanation')}</label>
                            </div>
                            <div className="radio">
                               <label><input type="radio" name="importOption" value={constants.REPLACE_DONOR_FUNDING} onChange={this.onImportOptionChange} checked={constants.REPLACE_DONOR_FUNDING === this.state.importOption}/>{this.props.i18nLib.t('wizard.review_import.import_option_replace')}</label><br/>
                               <label className="import-option-explanation">{this.props.i18nLib.t('wizard.review_import.import_option_replace_explanation')}</label>
                             </div>
                               {this.props.showDisasterResponse &&
                                   <div>
                                   <label>{this.props.i18nLib.t('wizard.review_import.disaster_response')}</label>                               
                                   <div className="radio">
                                     <label><input type="radio" name="disasterResponse" value={constants.YES} onChange={this.onDisasterReponseChange} checked={true === this.state.disasterResponse}/>{this.props.i18nLib.t('wizard.review_import.yes')}</label><br/>
                                   </div>
                                   
                                   <div className="radio">
                                     <label><input type="radio" name="disasterResponse" value={constants.NO} onChange={this.onDisasterReponseChange} checked={false === this.state.disasterResponse}/>{this.props.i18nLib.t('wizard.review_import.no')}</label><br/>
                                   </div>
                                   </div>     
                               }                              
                          </div>
                        </div>
                        <div>                              
                               
                          </div>
                                                 
                        <div className="col-sm-6 col-md-6">
                            <div className="form-group has-success has-feedback">
                            
                                <input aria-describedby="inputSuccess2Status" className="form-control" id="inputSuccess2" type="text" value={this.props.i18nLib.t('wizard.review_import.files_uploaded',this.state.importSummary)} readOnly="readonly"/>
                                <span aria-hidden="true" className="glyphicon glyphicon-ok form-control-feedback"></span>
                            </div>
                            <div className="form-group has-success has-feedback">
                                <input aria-describedby="inputSuccess2Status" className="form-control" id="inputSuccess2" type="text" value={this.props.i18nLib.t('wizard.review_import.data_filtered', this.state.importSummary)} readOnly="readonly"/>
                                <span aria-hidden="true" className="glyphicon glyphicon-ok form-control-feedback"></span>
                            </div>
                            <div className="form-group has-success has-feedback">
                                <input aria-describedby="inputSuccess2Status" className="form-control" id="inputSuccess2" type="text" value={this.props.i18nLib.t('wizard.review_import.projects_selected', this.state.importSummary)} readOnly="readonly"/>
                                <span aria-hidden="true" className="glyphicon glyphicon-ok form-control-feedback"></span>
                            </div>
                            <div className="form-group has-success has-feedback">
                                <input aria-describedby="inputSuccess2Status" className="form-control" id="inputSuccess2" type="text" value={this.props.i18nLib.t('wizard.review_import.fields_selected', this.state.importSummary)} readOnly="readonly"/>
                                <span aria-hidden="true" className="glyphicon glyphicon-ok form-control-feedback"></span>
                            </div>
                            <div className="form-group has-success has-feedback">
                                <input aria-describedby="inputSuccess2Status" className="form-control" id="inputSuccess2" type="text" value={this.props.i18nLib.t('wizard.review_import.values_mapped', this.state.importSummary)} readOnly="readonly"/>
                                <span aria-hidden="true" className="glyphicon glyphicon-ok form-control-feedback"></span>
                            </div>
                        </div>                        
                    </div>
                    <div className="buttons">
                    
                    <div className="row">                          
                    <div className="col-md-6">                
                       <button ref="previousButton"   className="btn btn-success navbar-btn btn-custom btn-previous" type="button" onClick={this.handlePrevious}>{this.props.i18nLib.t('wizard.review_import.previous')}</button>
                     </div>
                   <div className="col-md-6">                
                       {this.hasMoreVersions() &&
                           <button className="btn btn-warning navbar-btn btn-custom" type="button" onClick={this.processNextVersion}>{this.props.i18nLib.t('wizard.review_import.process_next_version')}</button>
                       }
                    &nbsp;<button className="btn btn-warning navbar-btn btn-custom" type="button" onClick={this.goHome}>{this.props.i18nLib.t('wizard.review_import.restart')}</button>&nbsp;
                   <button className="btn btn-success navbar-btn btn-custom" type="button" onClick={this.import}>{this.props.i18nLib.t('wizard.review_import.proceed_import')}</button>               
                   </div>
                   </div>                        
                   </div>
                </div>
            </div>

        );
    }
});

module.exports = ReviewImport;