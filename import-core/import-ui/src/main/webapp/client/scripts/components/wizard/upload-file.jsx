var React = require('react');
var fileStore = require('./../../stores/FileStore');
var reactAsync = require('react-async');
var Reflux = require('reflux');
var appActions = require('./../../actions');

var UploadFile = React.createClass({
    mixins: [
        reactAsync.Mixin, Reflux.ListenerMixin
    ],
    componentDidMount: function() {     
        this.listenTo(fileStore, this.updateFileData);
        var $el = $(this.refs.iatiFileInput.getDOMNode());    
        $el.fileinput({uploadUrl: "http://localhost:9010/upload"});
    },
    getInitialStateAsync: function() {
        appActions.loadFileData();
        fileStore.listen(function(data) {
            try {
                return cb(null, {
                    fileData: data.fileData
                });
            } catch (err) {}
        });
    },
    updateFileData: function(data) {
        this.setState({
            fileData: data.fileData
        });
    },
    render: function() {
        var files = [];
        if (this.state.fileData && this.state.fileData.length > 0) {        
        $.map(this.state.fileData, function (item, i) {            
                files.push(<tr key={item.file_name}>
                    <td>
                        {item.file_name}
                    </td>
                    <td>
                        {item.upload_date}
                    </td>
                    <td>
                        <span className="label label-success">View</span>
                    </td>
                </tr>);
            });
        }
        return (
            <div className="panel panel-default">
                <div className="panel-heading"><strong>Upload file(s)</strong></div>
                <div className="panel-body">
                    Select files to upload
                    <input className="file" ref="iatiFileInput" type="file"/>
                </div>
                <div className="progress upload-progress-bar">
                    <div aria-valuemax="100" aria-valuemin="0" aria-valuenow="40" className="progress-bar progress-bar-success" role="progressbar">
                        <span className="sr-only">40% Complete (success)</span>
                    </div>
                </div>
                <table className="table">
                    <thead>
                        <tr>
                            <th>
                                Filename
                            </th>
                            <th>
                                Date and Time
                            </th>
                            <th>
                                Action
                            </th>
                        </tr>
                    </thead>
                    <tbody>
                        {files}
                    </tbody>
                </table>
                <br /><br /><br />
                <div className="buttons">
                    <button className="btn btn-warning navbar-btn btn-custom" type="button">
                        Process</button>&nbsp;
                    <button className="btn btn-disabled navbar-btn btn-custom" type="button">Next >></button>
                </div>
                </div>
                ); } }); 
            module.exports = UploadFile;