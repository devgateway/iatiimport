var React = require('react');
var Reflux = require('reflux');
var Router = require('react-router');
var Link = Router.Link;
var _ = require('lodash/dist/lodash.underscore');
var moment = require('moment');
var Header = require('./../layout/header');
var Footer = require('./../layout/footer');
var appConfig = require('./../../conf');
var Router = require('react-router');
var RouteHandler = Router.RouteHandler;
var formActions = require('./../../actions/form');
var DataSourceStore = require('./../../stores/DataSourceStore');
var CustomDataSourceList = require('./custom-data-source-list');
var CustomDataSourceForm = require('./custom-data-source-form');
var validationUtils = require('./../../utils/validationUtils');
var DataSource = React.createClass({ 
  mixins: [
        Reflux.ListenerMixin
    ],
  getInitialState: function() {
    return {     
      dataSource: {},
      dsValidationErrors:[],
      reportingOrgs: []
    };
  },  
  componentWillMount: function () {
     this.listenTo(DataSourceStore, this.updateDataSource);     
     this.loadData();
  },
  updateDataSource: function (data) {  
        this.setState({
            dataSource: data, 
            customDataSource: null
        });
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
      
       formActions.loadDataSource().then(function(data) {                                       
        this.updateDataSource(data);                
      }.bind(this))["catch"](function(err) {       
        console.log('Error loading datasource');
     }.bind(this));
  },
  addCutomDataSource: function() {
      this.setState({
          customDataSource: {}
      });
  },
  closeDialog: function() {
      this.setState({
          customDataSource: null
      });
  },
  onDefaultUrlChange: function(event) {
     var ds = this.state.dataSource || {};
     ds.defaultUrl = event.target.value;
     this.setState({dataSource: ds});     
  },
  save: function() {
      if (this.validateDataSource()) {
          formActions.updateDataSource(this.state.dataSource).then(function(data) {  
              this.updateDataSource(data);                
          }.bind(this))["catch"](function(err) {       
            console.log('Error loading datasource');
         }.bind(this));
      }
     
  },
  updateField: function(field, event) {        
      var customDataSource = this.state.customDataSource || {};      
      customDataSource[field] = event.target.value;
      this.setState({customDataSource: customDataSource});
  },
  updateCutomDataSource: function() {
      var ds = this.state.dataSource;
      if (!ds.customDataSources) {
          ds.customDataSources =  [];       }
           
      var found = _.find(ds.customDataSources, { 'reportingOrgId': this.state.customDataSource.reportingOrgId});
      if (found) {
          found.url = this.state.customDataSource.url;
          found.reportingOrgId = this.state.customDataSource.reportingOrgId;
      } else {
          ds.customDataSources.push(this.state.customDataSource);
      }
      
      this.setState({dataSource: ds});
  },
  cancel: function() {
      this.setState({customDataSource: null});
  },
  validateDataSource: function() {
      var validationErrors = [];      
      if (this.state.dataSource.defaultUrl == null || this.state.dataSource.defaultUrl.length == 0) {          
          validationErrors.push('data_source.validation_default_url_required');        
      }
      if (this.state.dataSource.defaultUrl) {
          if (!validationUtils.validateUrl(this.state.dataSource.defaultUrl)) {
              validationErrors.push('data_source.validation_invalid_url');  
          }
          
      }
      
      this.setState({dsValidationErrors: validationErrors});      
      return validationErrors.length == 0;
  },
  edit: function(reportingOrgId, event) {
      var ds = this.state.dataSource;
      var foundDataSource = _.find(ds.customDataSources, { 'reportingOrgId': reportingOrgId});
      if (foundDataSource) {
          var copy = Object.assign({}, foundDataSource);
          this.setState({customDataSource: copy});
      }
  },
  remove: function(reportingOrgId, event) {
      var ds = this.state.dataSource;          
      ds.customDataSources = _.reject(ds.customDataSources, { 'reportingOrgId': reportingOrgId});
      this.setState({dataSource: ds});       
  },
 
  render: function() {   
    return (
     <div className="container " >
       <h2>{window.i18nLib.t('data_source.title')}</h2> 
       <br/>        
       {this.state.dsValidationErrors.length > 0 &&
           <div className="alert alert-danger " role="alert" ref="messageBox">
            <span className="sr-only">Error:</span>
             {this.state.dsValidationErrors.map(function(key) {
                 return <span ref="message"> {window.i18nLib.t(key)}</span>
             })}               
           </div> 
       }       
       <div className="form-group">
       <label for="defaultUrl">{window.i18nLib.t('data_source.default_url')}</label>
       <textarea className="form-control" rows="3" id="defaultUrl" onChange={this.onDefaultUrlChange} value={this.state.dataSource.defaultUrl}></textarea>
       <div>
         <button className="btn btn-success navbar-btn btn-custom" type="button" onClick={this.addCutomDataSource} >{window.i18nLib.t('data_source.add_custom_data_source')}</button>
       </div>       
     </div>
          
     <CustomDataSourceForm customDataSource={this.state.customDataSource} reportingOrgs = {this.state.reportingOrgs} cancel={this.cancel} 
         updateField={this.updateField} 
         updateCutomDataSource={this.updateCutomDataSource}
         cancel = {this.cancel}
         closeDialog = {this.closeDialog}/>      
     <CustomDataSourceList dataSource={this.state.dataSource} reportingOrgs = {this.state.reportingOrgs} remove={this.remove} edit={this.edit}/>     
     <div className="buttons">
           <button className="btn btn-success navbar-btn btn-custom" type="button" onClick={this.save} >{window.i18nLib.t('data_source.save')}</button>
      </div>               
      </div>
      );
  }
});
module.exports = DataSource;