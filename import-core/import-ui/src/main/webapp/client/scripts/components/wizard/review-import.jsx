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
		return {importSummary:{}};
	 },
	componentDidMount: function() {
		this.props.eventHandlers.updateCurrentStep(constants.REVIEW_IMPORT);
		this.listenTo(importSummaryStore, this.updateLanguages);
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
			}.bind(this)).catch(function(err) {       
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
    render: function () {
    	var statusMessage = this.props.statusMessage.length > 0 ? <div className="alert alert-info" role="alert">{this.props.statusMessage}</div> : "";
        return (
            <div className="panel panel-default">
                <div className="panel-heading"><strong>{this.props.i18nLib.t('wizard.review_import.review_import')}</strong></div>
                <div className="panel-body">
                   {statusMessage}
                    <div className="row">
                        <div className="col-sm-3 col-md-3"></div>
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
                        <div className="col-sm-3 col-md-3"></div>
                    </div>
                    <div className="buttons">
                    
                    <div className="row">                          
                    <div className="col-md-6">                
                       <button ref="previousButton"   className="btn btn-success navbar-btn btn-custom btn-previous" type="button" onClick={this.handlePrevious}>{this.props.i18nLib.t('wizard.review_import.previous')}</button>
                     </div>
                   <div className="col-md-6">                
                   <button className="btn btn-warning navbar-btn btn-custom" type="button" onClick={this.goHome}>{this.props.i18nLib.t('wizard.review_import.restart')}</button>&nbsp;
                   <button className="btn btn-success navbar-btn btn-custom" type="button" onClick={this.props.eventHandlers.launchImport}>{this.props.i18nLib.t('wizard.review_import.proceed_import')}</button>               
                   </div>
                   </div>                        
                   </div>
                </div>
            </div>

        );
    }
});

module.exports = ReviewImport;