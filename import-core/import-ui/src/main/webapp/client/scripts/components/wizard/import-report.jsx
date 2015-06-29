var React = require('react');
var Router = require('react-router');
var Link = Router.Link;
var ImportReport = React.createClass({
  componentDidMount: function () {
  },
  
  render: function() {
    var results = [];

    return (
<div className="modal fade" id="myModal" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
  <div className="modal-dialog">
    <div className="modal-content">
      <div className="modal-header">
        <button type="button" className="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 className="modal-title" id="myModalLabel">Import Process</h4>
      </div>
      <div className="modal-body">
        <table className="table scrollablebody">
            <thead>
                <tr>
                    <th>
                        Project Id
                    </th>
                    <th>
                        Result
                    </th>
                    <th>
                        Message
                    </th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td>
                        Project Name 1
                    </td>
                    <td>
                        <span className="label label-success">Imported</span>
                    </td>
                    <td>
                        OK
                    </td>
                </tr>
                <tr>
                    <td>
                        Project Name 2
                    </td>
                    <td>
                        <span className="label label-success">Imported</span>
                    </td>
                    <td>
                        OK
                    </td>
                </tr>
                <tr>
                    <td>
                        Project Name 3
                    </td>
                    <td>
                        <span className="label label-danger">Rejected</span>
                    </td>
                    <td>
                        Rejected with message: "No permissions"
                    </td>
                </tr>
            </tbody>
        </table>
        
      </div>
      <div className="modal-footer">
        <button type="button" className="btn btn-default" data-dismiss="modal">Close</button>
        <button type="button" className="btn btn-primary">Save Log</button>
      </div>
    </div>
  </div>
</div>

    );
  }
});

module.exports = ImportReport;
