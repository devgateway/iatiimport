var React = require('react');
var constants = require('./../../utils/constants');

var ReviewImport = React.createClass({
	componentDidMount: function() {
		this.props.eventHandlers.updateCurrentStep(constants.REVIEW_IMPORT);		
	}, 
    render: function () {
        return (
            <div className="panel panel-default">
                <div className="panel-heading"><strong>{this.props.i18nLib.t('wizard.review_import.review_import')}</strong></div>
                <div className="panel-body">
                    <div className="row">
                        <div className="col-sm-3 col-md-3"></div>
                        <div className="col-sm-6 col-md-6">
                            <div className="form-group has-success has-feedback">
                            
                                <input aria-describedby="inputSuccess2Status" className="form-control" id="inputSuccess2" type="text" value={this.props.i18nLib.t('wizard.review_import.files_uploaded')} readOnly="readonly"/>
                                <span aria-hidden="true" className="glyphicon glyphicon-ok form-control-feedback"></span>
                            </div>
                            <div className="form-group has-success has-feedback">
                                <input aria-describedby="inputSuccess2Status" className="form-control" id="inputSuccess2" type="text" value={this.props.i18nLib.t('wizard.review_import.data_filtered')} readOnly="readonly"/>
                                <span aria-hidden="true" className="glyphicon glyphicon-ok form-control-feedback"></span>
                            </div>
                            <div className="form-group has-success has-feedback">
                                <input aria-describedby="inputSuccess2Status" className="form-control" id="inputSuccess2" type="text" value={this.props.i18nLib.t('wizard.review_import.projects_selected')} readOnly="readonly"/>
                                <span aria-hidden="true" className="glyphicon glyphicon-ok form-control-feedback"></span>
                            </div>
                            <div className="form-group has-success has-feedback">
                                <input aria-describedby="inputSuccess2Status" className="form-control" id="inputSuccess2" type="text" value={this.props.i18nLib.t('wizard.review_import.fields_selected')} readOnly="readonly"/>
                                <span aria-hidden="true" className="glyphicon glyphicon-ok form-control-feedback"></span>
                            </div>
                            <div className="form-group has-success has-feedback">
                                <input aria-describedby="inputSuccess2Status" className="form-control" id="inputSuccess2" type="text" value={this.props.i18nLib.t('wizard.review_import.values_mapped')} readOnly="readonly"/>
                                <span aria-hidden="true" className="glyphicon glyphicon-ok form-control-feedback"></span>
                            </div>
                        </div>
                        <div className="col-sm-3 col-md-3"></div>
                    </div>
                    <div className="buttons">
                        <button className="btn btn-warning navbar-btn btn-custom" type="button">{this.props.i18nLib.t('wizard.review_import.close')}</button>&nbsp;
                        <button className="btn btn-success navbar-btn btn-custom" type="button" onClick={this.props.eventHandlers.launchImport}>{this.props.i18nLib.t('wizard.review_import.proceed_import')}</button>
                    </div>
                </div>
            </div>

        );
    }
});

module.exports = ReviewImport;