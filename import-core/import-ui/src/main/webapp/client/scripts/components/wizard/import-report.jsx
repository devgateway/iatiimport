var React = require('react');
var Router = require('react-router');
var Link = Router.Link;
var _ = require('lodash/dist/lodash.underscore');

var ImportReport = React.createClass({
  componentDidMount: function () {
  },
  
  render: function() {
    var results = this.props.results;
    var rows = [];
    _.each(results, function(result) {
        rows.push(
                <tr>
                    <td>
                        {result.id}
                    </td>
                    <td>
                        {result.operation}
                    </td>
                    <td>
                        {result.status}
                    </td>
                    <td>
                        {result.message}
                    </td>
                </tr>
                );
            });

    return (
<div className="modal fade" id="modalResults" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
  <div className="modal-dialog">
    <div className="modal-content">
      <div className="modal-header">
        <button type="button" className="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 className="modal-title" id="myModalLabel">{this.props.i18nLib.t('wizard.import_report.import_process')}</h4>
      </div>
      <div className="modal-body">
        <table className="table scrollablebody">
            <thead>
                <tr>
                    <th>
                        {this.props.i18nLib.t('wizard.import_report.project_id')}
                    </th>
                    <th>
                        {this.props.i18nLib.t('wizard.import_report.operation')}
                    </th>
                    <th>
                        {this.props.i18nLib.t('wizard.import_report.status')}
                    </th>
                    <th>
                        {this.props.i18nLib.t('wizard.import_report.message')}
                    </th>
                </tr>
            </thead>
            <tbody>
                {rows}
            </tbody>
        </table>
        
      </div>
      <div className="modal-footer">
        <button type="button" className="btn btn-default btn-warning" data-dismiss="modal">{this.props.i18nLib.t('wizard.import_report.close')}</button>
      </div>
    </div>
  </div>
</div>

    );
  }
});

module.exports = ImportReport;
