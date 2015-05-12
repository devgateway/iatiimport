var React = require('react');
var UploadFile = React.createClass({  
  render: function() {  
    return (
    
        <div className="panel panel-default">
                <div className="panel-heading"><strong>Map Fields Values</strong></div>
                
                <div className="panel-body">
                                <div className="row">
                                    <div className="col-sm-12 col-md-12">
                                        <ul className="nav nav-pills navbar-right">
                                            <li role="presentation" className="dropdown">
                                                <a className="dropdown-toggle" data-toggle="dropdown" href="#" role="button" aria-expanded="false">
                                                    Load Existing Mapping
                                                    <span className="caret"></span>
                                                </a>
                                                <ul className="dropdown-menu" role="menu">
                                                    <li role="presentation"><a href="#template1" aria-controls="template1">Value Mapping 1</a></li>
                                                    <li role="presentation"><a href="#template2" aria-controls="template2">Value Mapping 2</a></li>
                                                </ul>
                                            </li>
                                        </ul>
                                    </div>
                                </div>
                                <div className="row">
                                    <div className="col-sm-3 col-md-3">
                                        <ul className="nav nav-pills nav-stacked" role="tablist">
                                            <li role="presentation" className="active"><a href="#field_one" aria-controls="field_one" role="tab" data-toggle="tab">Activity Status</a></li>
                                            <li role="presentation"><a href="#field_two" aria-controls="field_two" role="tab" data-toggle="tab">Financing Instrument</a></li>
                                        </ul>
                                    </div>
                                    <div className="col-sm-9 col-md-9 main">
                                        <div className="tab-content">
                                            <div role="tabpanel" className="tab-pane active" id="field_one">
                                                <div className="panel panel-default">
                                                    <div className="panel-body">
                                                        <table className="table">
                                                            <thead>
                                                                <tr>
                                                                    <th>
                                                                        Source Values
                                                                    </th>
                                                                    <th>
                                                                        Destination Value
                                                                    </th>
                                                                </tr>
                                                            </thead>
                                                            <tbody>
                                                                <tr>
                                                                    <td>
                                                                        <div className="table_cell">
                                                                            Completed
                                                                        </div>
                                                                    </td>
                                                                    <td>
                                                                        <ul className="nav nav-pills navbar-left">
                                                                            <li role="presentation" className="dropdown">
                                                                                <a className="dropdown-toggle" data-toggle="dropdown" href="#" role="button" aria-expanded="false">
                                                                                    Completed
                                                                                    <span className="caret"></span>
                                                                                </a>
                                                                                <ul className="dropdown-menu" role="menu">
                                                                                    <li role="presentation"><a href="#template1" aria-controls="template1">Completed</a></li>
                                                                                    <li role="presentation"><a href="#template1" aria-controls="template1">Ongoing</a></li>
                                                                                    <li role="presentation"><a href="#template2" aria-controls="template2">Pipeline</a></li>
                                                                                </ul>
                                                                            </li>
                                                                        </ul>
                                                                    </td>
                                                                </tr>
                                                                <tr>
                                                                    <td>
                                                                        <div className="table_cell">
                                                                            Ongoing
                                                                        </div>
                                                                    </td>
                                                                    <td>
                                                                        <ul className="nav nav-pills navbar-left">
                                                                            <li role="presentation" className="dropdown">
                                                                                <a className="dropdown-toggle" data-toggle="dropdown" href="#" role="button" aria-expanded="false">
                                                                                    Ongoing
                                                                                    <span className="caret"></span>
                                                                                </a>
                                                                                <ul className="dropdown-menu" role="menu">
                                                                                    <li role="presentation"><a href="#template1" aria-controls="template1">Completed</a></li>
                                                                                    <li role="presentation"><a href="#template1" aria-controls="template1">Ongoing</a></li>
                                                                                    <li role="presentation"><a href="#template2" aria-controls="template2">Pipeline</a></li>
                                                                                </ul>
                                                                            </li>
                                                                        </ul>
                                                                    </td>
                                                                </tr>
                                                                <tr>
                                                                    <td>
                                                                        <div className="table_cell">
                                                                            Pipeline
                                                                        </div>
                                                                    </td>
                                                                    <td>
                                                                        <ul className="nav nav-pills navbar-left">
                                                                            <li role="presentation" className="dropdown">
                                                                                <a className="dropdown-toggle" data-toggle="dropdown" href="#" role="button" aria-expanded="false">
                                                                                    Pipeline
                                                                                    <span className="caret"></span>
                                                                                </a>
                                                                                <ul className="dropdown-menu" role="menu">
                                                                                    <li role="presentation"><a href="#template1" aria-controls="template1">Completed</a></li>
                                                                                    <li role="presentation"><a href="#template1" aria-controls="template1">Ongoing</a></li>
                                                                                    <li role="presentation"><a href="#template2" aria-controls="template2">Pipeline</a></li>
                                                                                </ul>
                                                                            </li>
                                                                        </ul>
                                                                    </td>
                                                                </tr>
                                                            </tbody>
                                                        </table>
                                                    </div>
                                                </div>
                                            </div>
                                            <div role="tabpanel" className="tab-pane" id="field_two">
                                                <div className="panel panel-default">
                                                    <div className="panel-body">
                                                        <table className="table">
                                                            <thead>
                                                                <tr>
                                                                    <th>
                                                                        Source Values
                                                                    </th>
                                                                    <th>
                                                                        Destination Value
                                                                    </th>
                                                                </tr>
                                                            </thead>
                                                            <tbody>
                                                                <tr>
                                                                    <td>
                                                                        <div className="table_cell">
                                                                            Grant
                                                                        </div>
                                                                    </td>
                                                                    <td>
                                                                        <ul className="nav nav-pills navbar-left">
                                                                            <li role="presentation" className="dropdown">
                                                                                <a className="dropdown-toggle" data-toggle="dropdown" href="#" role="button" aria-expanded="false">
                                                                                    Grant
                                                                                    <span className="caret"></span>
                                                                                </a>
                                                                                <ul className="dropdown-menu" role="menu">
                                                                                    <li role="presentation"><a href="#template1" aria-controls="template1">Grant</a></li>
                                                                                    <li role="presentation"><a href="#template1" aria-controls="template1">Loan</a></li>
                                                                                </ul>
                                                                            </li>
                                                                        </ul>
                                                                    </td>
                                                                </tr>
                                                                <tr>
                                                                    <td>
                                                                        <div className="table_cell">
                                                                            Loan
                                                                        </div>
                                                                    </td>
                                                                    <td>
                                                                        <ul className="nav nav-pills navbar-left">
                                                                            <li role="presentation" className="dropdown">
                                                                                <a className="dropdown-toggle" data-toggle="dropdown" href="#" role="button" aria-expanded="false">
                                                                                    Loan
                                                                                    <span className="caret"></span>
                                                                                </a>
                                                                                <ul className="dropdown-menu" role="menu">
                                                                                    <li role="presentation"><a href="#template1" aria-controls="template1">Grant</a></li>
                                                                                    <li role="presentation"><a href="#template1" aria-controls="template1">Loan</a></li>
                                                                                </ul>
                                                                            </li>
                                                                        </ul>
                                                                    </td>
                                                                </tr>
                                                            </tbody>
                                                        </table>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div className="buttons">
                                    <button type="button" className="btn btn-warning navbar-btn btn-custom" >Save</button>&nbsp;
                                    <button type="button" className="btn btn-success navbar-btn btn-custom" >Next >></button>
                                </div>

                            </div>
        </div>
        
    );
  }
});

module.exports = UploadFile;
