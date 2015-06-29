var React = require('react');
var ReviewImport = React.createClass({
    render: function () {
        return (
            <div className="panel panel-default">
                <div className="panel-heading"><strong>Review and Import</strong></div>
                <div className="panel-body">
                    <div className="row">
                        <div className="col-sm-3 col-md-3"></div>
                        <div className="col-sm-6 col-md-6">
                            <div className="form-group has-success has-feedback">
                                <input aria-describedby="inputSuccess2Status" className="form-control" id="inputSuccess2" type="text" value="Files Uploaded" readOnly="readonly"/>
                                <span aria-hidden="true" className="glyphicon glyphicon-ok form-control-feedback"></span>
                            </div>
                            <div className="form-group has-success has-feedback">
                                <input aria-describedby="inputSuccess2Status" className="form-control" id="inputSuccess2" type="text" value="Data Filtered" readOnly="readonly"/>
                                <span aria-hidden="true" className="glyphicon glyphicon-ok form-control-feedback"></span>
                            </div>
                            <div className="form-group has-success has-feedback">
                                <input aria-describedby="inputSuccess2Status" className="form-control" id="inputSuccess2" type="text" value="Projects Selected" readOnly="readonly"/>
                                <span aria-hidden="true" className="glyphicon glyphicon-ok form-control-feedback"></span>
                            </div>
                            <div className="form-group has-success has-feedback">
                                <input aria-describedby="inputSuccess2Status" className="form-control" id="inputSuccess2" type="text" value="Fields Selected" readOnly="readonly"/>
                                <span aria-hidden="true" className="glyphicon glyphicon-ok form-control-feedback"></span>
                            </div>
                            <div className="form-group has-success has-feedback">
                                <input aria-describedby="inputSuccess2Status" className="form-control" id="inputSuccess2" type="text" value="Values Mapped" readOnly="readonly"/>
                                <span aria-hidden="true" className="glyphicon glyphicon-ok form-control-feedback"></span>
                            </div>
                        </div>
                        <div className="col-sm-3 col-md-3"></div>
                    </div>
                    <div className="buttons">
                        <button className="btn btn-warning navbar-btn btn-custom" type="button">Close</button>&nbsp;
                        <button className="btn btn-success navbar-btn btn-custom" type="button" onClick={this.props.eventHandlers.reviewImport}>Proceed with Import</button>
                    </div>
                </div>
            </div>

        );
    }
});

module.exports = ReviewImport;