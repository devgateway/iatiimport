var React = require('react');
var ChooseFields = React.createClass({  
  render: function() {  
    return (
    
        <div className="panel panel-default">
                <div className="panel-heading"><strong>Choose and Map Fields</strong></div>
                <div className="panel-body">
                                <ul className="nav nav-pills navbar-right">
                                    <li role="presentation" className="dropdown">
                                        <a className="dropdown-toggle" data-toggle="dropdown" href="#" role="button" aria-expanded="false">
                                            Load Existing Template
                                            <span className="caret"></span>
                                        </a>
                                        <ul className="dropdown-menu" role="menu">
                                            <li role="presentation"><a href="#template1" aria-controls="template1">Template 1 IATI 1.05</a></li>
                                            <li role="presentation"><a href="#template2" aria-controls="template2">Template 2 IATI 2.01</a></li>
                                        </ul>
                                    </li>
                                </ul>
                                <table className="table">
                                    <thead>
                                        <tr>
                                            <th>
                                                Import/Update
                                            </th>
                                            <th>
                                                Source Field
                                            </th>
                                            <th>
                                                Destination Field
                                            </th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <tr>
                                            <td>
                                                <input type="checkbox" aria-label="Field1"/>
                                            </td>
                                            <td>
                                                <div className="table_cell">
                                                Activity Status
                                                </div>
                                            </td>
                                            <td>
                                                <ul className="nav nav-pills navbar-left">
                                                    <li role="presentation" className="dropdown">
                                                        <a className="dropdown-toggle" data-toggle="dropdown" href="#" role="button" aria-expanded="false">
                                                            Project Status
                                                            <span className="caret"></span>
                                                        </a>
                                                        <ul className="dropdown-menu" role="menu">
                                                            <li role="presentation"><a href="#template1" aria-controls="template1">Project Status</a></li>
                                                            <li role="presentation"><a href="#template2" aria-controls="template2">Project Title</a></li>
                                                            <li role="presentation"><a href="#template2" aria-controls="template2">Description</a></li>
                                                            <li role="presentation"><a href="#template2" aria-controls="template2">Sectors</a></li>
                                                            <li role="presentation"><a href="#template2" aria-controls="template2">Contacts</a></li>
                                                            <li role="presentation"><a href="#template2" aria-controls="template2">Location</a></li>
                                                            <li role="presentation"><a href="#template2" aria-controls="template2">Disbursements</a></li>
                                                            <li role="presentation"><a href="#template2" aria-controls="template2">Commitments</a></li>
                                                        </ul>
                                                    </li>
                                                </ul>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td>
                                                <input type="checkbox" aria-label="Field1"/>
                                            </td>
                                            <td>
                                                <div className="table_cell">
                                                Project Name
                                                </div>
                                            </td>
                                            <td>
                                                <ul className="nav nav-pills navbar-left">
                                                    <li role="presentation" className="dropdown">
                                                        <a className="dropdown-toggle" data-toggle="dropdown" href="#" role="button" aria-expanded="false">
                                                            Project Title
                                                            <span className="caret"></span>
                                                        </a>
                                                        <ul className="dropdown-menu" role="menu">
                                                            <li role="presentation"><a href="#template1" aria-controls="template1">Project Status</a></li>
                                                            <li role="presentation"><a href="#template2" aria-controls="template2">Project Title</a></li>
                                                            <li role="presentation"><a href="#template2" aria-controls="template2">Description</a></li>
                                                            <li role="presentation"><a href="#template2" aria-controls="template2">Sectors</a></li>
                                                            <li role="presentation"><a href="#template2" aria-controls="template2">Contacts</a></li>
                                                            <li role="presentation"><a href="#template2" aria-controls="template2">Location</a></li>
                                                            <li role="presentation"><a href="#template2" aria-controls="template2">Disbursements</a></li>
                                                            <li role="presentation"><a href="#template2" aria-controls="template2">Commitments</a></li>
                                                        </ul>
                                                    </li>
                                                </ul>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td>
                                                <input type="checkbox" aria-label="Field1"/>
                                            </td>
                                            <td>
                                                <div className="table_cell">
                                                Description
                                                </div>
                                            </td>
                                            <td>
                                                <ul className="nav nav-pills navbar-left">
                                                    <li role="presentation" className="dropdown">
                                                        <a className="dropdown-toggle" data-toggle="dropdown" href="#" role="button" aria-expanded="false">
                                                            Description
                                                            <span className="caret"></span>
                                                        </a>
                                                        <ul className="dropdown-menu" role="menu">
                                                            <li role="presentation"><a href="#template1" aria-controls="template1">Project Status</a></li>
                                                            <li role="presentation"><a href="#template2" aria-controls="template2">Project Title</a></li>
                                                            <li role="presentation"><a href="#template2" aria-controls="template2">Description</a></li>
                                                            <li role="presentation"><a href="#template2" aria-controls="template2">Sectors</a></li>
                                                            <li role="presentation"><a href="#template2" aria-controls="template2">Contacts</a></li>
                                                            <li role="presentation"><a href="#template2" aria-controls="template2">Location</a></li>
                                                            <li role="presentation"><a href="#template2" aria-controls="template2">Disbursements</a></li>
                                                            <li role="presentation"><a href="#template2" aria-controls="template2">Commitments</a></li>
                                                        </ul>
                                                    </li>
                                                </ul>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td>
                                                <input type="checkbox" aria-label="Field1"/>
                                            </td>
                                            <td>
                                                <div className="table_cell">
                                                Contacts
                                                </div>
                                            </td>
                                            <td>
                                                <ul className="nav nav-pills navbar-left">
                                                    <li role="presentation" className="dropdown">
                                                        <a className="dropdown-toggle" data-toggle="dropdown" href="#" role="button" aria-expanded="false">
                                                            Contacts
                                                            <span className="caret"></span>
                                                        </a>
                                                        <ul className="dropdown-menu" role="menu">
                                                            <li role="presentation"><a href="#template1" aria-controls="template1">Project Status</a></li>
                                                            <li role="presentation"><a href="#template2" aria-controls="template2">Project Title</a></li>
                                                            <li role="presentation"><a href="#template2" aria-controls="template2">Description</a></li>
                                                            <li role="presentation"><a href="#template2" aria-controls="template2">Sectors</a></li>
                                                            <li role="presentation"><a href="#template2" aria-controls="template2">Contacts</a></li>
                                                            <li role="presentation"><a href="#template2" aria-controls="template2">Location</a></li>
                                                            <li role="presentation"><a href="#template2" aria-controls="template2">Disbursements</a></li>
                                                            <li role="presentation"><a href="#template2" aria-controls="template2">Commitments</a></li>
                                                        </ul>
                                                    </li>
                                                </ul>
                                            </td>
                                        </tr>
                                    </tbody>
                                </table>
                            </div>
                            <div className="buttons">
                                <button type="button" className="btn btn-warning navbar-btn btn-custom" >Save</button>&nbsp;
                                <button type="button" className="btn btn-success navbar-btn btn-custom" >Next >></button>
                            </div>
        </div>
        
    );
  }
});

module.exports = ChooseFields;
