var React = require('react');
var Reflux = require('reflux');
var Router = require('react-router');
var validationUtils = require('./../../utils/validationUtils');
var CustomDataSourceForm = React.createClass({ 
  mixins: [
        Reflux.ListenerMixin
    ],
  getInitialState: function() {
    return {validationErrors:[]};
  },  
  componentWillMount: function () {     
  }, 
  cancel: function() {
      this.setState({validationErrors:[]});
      this.props.cancel();       
  },
  updateField: function(field, event) {
     this.props.updateField(field, event);       
  },
  updateCutomDataSource: function() {
      if (this.validate()) {
          this.setState({validationErrors:[]});
          this.props.updateCutomDataSource();
          this.props.closeDialog(); 
      }
     
  },
  closeDialog: function(field, event) {
      this.setState({validationErrors:[]});
      this.props.closeDialog();       
  },
  validate: function() {
      var validationErrors = [];      
      if (this.props.customDataSource.url == null || this.props.customDataSource.url.length == 0) {          
          validationErrors.push('data_source.validation_default_url_required');        
      }
      
      if (this.props.customDataSource.url) {
          if (!validationUtils.validateUrl(this.props.customDataSource.url)) {
              validationErrors.push('data_source.validation_invalid_url');  
          }
          
      }
      
      if (this.props.customDataSource.reportingOrgId == null || this.props.customDataSource.reportingOrgId.length == 0) {
          validationErrors.push('data_source.validation_reporting_org_required');           
      }
      
      this.setState({validationErrors: validationErrors});      
      return validationErrors.length == 0;
  },
  render: function() { 
    var sortedOrgs = this.props.reportingOrgs.sort(function(org1, org2) {
        var name1 = org1.name.toUpperCase();
        var name2 = org2.name.toUpperCase();
        if (name1 < name2) {
          return -1;
        }
        if (name1 > name2) {
          return 1;
        }

        return 0;
      });
    
    return (
     <div>       
           
         {this.props.customDataSource &&                
                <div className="modal fade in" id="addCutomDataSource" tabindex="-1" role="dialog" aria-labelledby="titleaddCutomDataSource" aria-hidden="true" style={{display: 'block'}} >
                    <div ref="addCutomDataSourceDialog" className="modal-dialog">
                      <div className="modal-content">
                        <div className="modal-header">
                          <button type="button" className="close" data-dismiss="modal" aria-label="Close" onClick={this.closeDialog}><span aria-hidden="true">x</span></button>
                          <h4 className="modal-titleaddCutomDataSource" id="titleaddCutomDataSource">{window.i18nLib.t('data_source.add_custom_data_source')}</h4>              
                        </div>
                         <div className="modal-body">   
                        {this.state.validationErrors.length > 0 &&
                         <div className="alert alert-danger " role="alert" ref="messageBox">
                          <span className="sr-only">{window.i18nLib.t('data_source.error')}</span>
                          {this.state.validationErrors.map(function(key) {
                            return <div> {window.i18nLib.t(key)}</div>
                          })}               
                         </div> 
                        }
                        <label for="reportingOrgId">{window.i18nLib.t('data_source.reporting_org')}</label>                          
                          <select id="reportingOrgId" className="form-control" onChange={this.updateField.bind(this, 'reportingOrgId')} value={this.props.customDataSource.reportingOrgId}> 
                           <option value="" >{window.i18nLib.t('data_source.select_reporting_org')}</option>
                           {sortedOrgs.map(function(org){
                               return (<option value={org.orgId}>{org.name} </option>)
                           })                               
                           }
                          </select>
                           <br/>
                          <label for="exceptionUrl">{window.i18nLib.t('data_source.exception_url')}</label>
                          <textarea className="form-control" rows="3" id="exceptionUrl" onChange={this.updateField.bind(this, 'url')} value={this.props.customDataSource.url}></textarea>               
                         <div className="modal-footer">  
                           
                           <button className="btn btn-success  btn-custom" type="button" onClick={this.updateCutomDataSource} >{window.i18nLib.t('data_source.update')}</button>
                           <button className="btn btn-warning  btn-custom" type="button" onClick={this.cancel} >{window.i18nLib.t('data_source.cancel')}</button>
                        </div>
                      </div>
                    </div>
                  </div>       
                 </div>
             }                                      
      </div>
      );
  }
});
module.exports = CustomDataSourceForm;