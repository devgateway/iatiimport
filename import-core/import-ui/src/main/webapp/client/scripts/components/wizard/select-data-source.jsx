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
var SelectDataSource = React.createClass({
    mixins: [Reflux.ListenerMixin
    ],
    getInitialState: function() {
       return {
           reportingOrgs: [],
           reportingOrgId: null
       };
    },
    updateReportingOrgs: function (data) {        
       this.setState({reportingOrgs: data})
    },       
   loadData: function(){       
       formActions.loadReportingOrganizations().then(function(data) {   
          this.updateReportingOrgs(data);                
        }.bind(this))["catch"](function(err) {       
          console.log('Error loading reporting orgs');
       }.bind(this));      
    },
    componentDidMount: function() {  
        this.props.eventHandlers.updateCurrentStep(constants.SELECT_DATASOURCE);
        this.loadData();
    },  
    onReportingOrgChange(event) {
      this.setState({reportingOrgId: event.target.value});  
    },
    handleNext: function () {
        this.props.eventHandlers.fetchData(this.state.reportingOrgId);
    },
    render: function() {      
        return (
            <div className="panel panel-default">
                <div className="panel-heading"><strong>{this.props.i18nLib.t('data_source.select_data_source')}</strong></div>
                <div className="panel-body">
                <label> {this.props.i18nLib.t('data_source.select_data_reporting_org')}</label>                              
                <select id="reportingOrgId" className="form-control reporting-org-select" onChange={this.onReportingOrgChange}> 
                 <option value="" ></option>
                   {this.state.reportingOrgs.map(function(org){
                     return (<option value={org.orgId}>{org.name} </option>)
                   })                               
                 }
                </select>                    
                </div>
                <br /><br /><br />
                <div className="buttons">
                    <button disabled = {this.state.reportingOrgId ? "" : "disabled"} className="btn btn-success navbar-btn btn-custom" type="button" onClick={this.handleNext}>{this.props.i18nLib.t('data_source.next')}</button>
                </div>
            </div>
            );
    } 
}); 
module.exports = SelectDataSource;
