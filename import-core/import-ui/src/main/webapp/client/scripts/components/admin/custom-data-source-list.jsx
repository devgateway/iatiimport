var React = require('react');
var Reflux = require('reflux');
var Router = require('react-router');
var _ = require('lodash/dist/lodash.underscore');

var CustomDataSourceList = React.createClass({ 
  mixins: [
        Reflux.ListenerMixin
    ],
  getInitialState: function() {
    return {};
  },  
  componentWillMount: function () {     
  },
  edit: function(reportingOrgId, event) {
   this.props.edit(reportingOrgId);
  },
  remove: function(reportingOrgId, event) {
      this.props.remove(reportingOrgId);       
  },
  getName: function(id) {
     var name = '';
     var org = _.find(this.props.reportingOrgs, {'orgId': id});
     if (org) {
         name = org.name;
     }
     return name;
  },
  render: function() {    
    return (
     <div>       
     {this.props.dataSource.customDataSources && this.props.dataSource.customDataSources.length > 0 &&
              <table className="table file-list">
                     <thead>
                         <tr>
                            <th>
                                  {window.i18nLib.t('data_source.reporting_org')}
                             </th>
                             <th>
                                  {window.i18nLib.t('data_source.exception_url')}
                             </th>
                             <th>
                                  {window.i18nLib.t('data_source.actions')}
                             </th>
                            </tr>
                     </thead>
                     <tbody>
                     {this.props.dataSource.customDataSources.map(function(ds, i) {                                 
                         return(<tr key={i}><td>{this.getName(ds.reportingOrgId)}</td><td>{ds.url}</td><td>                         
                         <span className="glyphicon glyphicon-edit glyphicon-custom" title="Edit" onClick={this.edit.bind(this, ds.reportingOrgId)} ></span>
                         <span className="glyphicon glyphicon-remove glyphicon-custom" title="Delete" onClick={this.remove.bind(this, ds.reportingOrgId)}></span>
                         </td></tr>)}.bind(this))
                     } 
                     </tbody>
                  </table>
        }                                      
      </div>
      );
  }
});
module.exports = CustomDataSourceList;