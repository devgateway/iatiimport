var React = require('react');
var _ = require('lodash/dist/lodash.underscore');
var fileStore = require('./../../stores/FileStore');
var moment = require('moment');
var reactAsync = require('react-async');
var Reflux = require('reflux');
var appActions = require('./../../actions');
var appConfig = require('./../../conf');
var DataSourceStore = require('./../../stores/DataSourceStore');
var constants = require('./../../utils/constants');
var formActions = require('./../../actions/form');
var AutoComplete = require('./autocomplete');

var SelectDataSource = React.createClass({
    mixins: [Reflux.ListenerMixin
    ],
    getInitialState: function() {
       return {
           reportingOrgId: null,
           reportingOrgName: null,
       };
    },
    componentDidMount: function() {  
        this.props.eventHandlers.updateCurrentStep(constants.SELECT_DATASOURCE);       
    }, 
    handlePrevious: function(){
       this.props.eventHandlers.goHome();
    },
    handleNext: function() {
       this.props.eventHandlers.fetchData(this.state.reportingOrgId);
    },
    onReportingOrgSelect: function(datum) {        
       this.setState({reportingOrgId: datum.orgId, reportingOrgName:datum.name });        
    },
    render: function() {      
        var language = this.props.i18nLib.lng() || 'en';
        return (
            <div className="panel panel-default">
                <div className="panel-heading"><strong>{this.props.i18nLib.t('data_source.select_data_source')}</strong></div>
                <div className="panel-body">
                <label> {this.props.i18nLib.t('data_source.select_data_reporting_org')}</label>   
                <AutoComplete context={constants.SELECT_DATASOURCE} options={[]} display="title" language={language} placeholder={this.props.i18nLib.t('data_source.reporting_org_placeholder')} refId="reportingOrgSearch" onSelect={this.onReportingOrgSelect.bind(this)} value={this.state.reportingOrgName ? this.state.reportingOrgName : ''}/>                                  
                </div>
                <br /><br /><br />
                <div className="buttons">
                    <div className="col-md-6"><button className="btn btn-success navbar-btn btn-custom btn-previous" type="button" onClick={this.handlePrevious}>{this.props.i18nLib.t('wizard.previous')}</button></div>
                    <button disabled = {this.state.reportingOrgId ? "" : "disabled"} className="btn btn-success navbar-btn btn-custom" type="button" onClick={this.handleNext}>{this.props.i18nLib.t('wizard.next')}</button>
                </div>
            </div>
            );
    } 
}); 
module.exports = SelectDataSource;
