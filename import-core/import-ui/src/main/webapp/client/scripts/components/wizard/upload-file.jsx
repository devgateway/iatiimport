var React = require('react');
var _ = require('lodash/dist/lodash.underscore');
var fileStore = require('./../../stores/FileStore');
var moment = require('moment');
var reactAsync = require('react-async');
var Reflux = require('reflux');
var appActions = require('./../../actions');
var appConfig = require('./../../conf');

var UploadFile = React.createClass({
    mixins: [
        reactAsync.Mixin, Reflux.ListenerMixin
    ],
    componentDidMount: function() {     
        this.listenTo(fileStore, this.updateFileData);
        var $el = $(this.refs.iatiFileInput.getDOMNode());
        var self = this;    
        $el.fileinput(
            {
                allowedFileExtensions : ['xml'],
                uploadUrl: appConfig.TOOL_HOST + appConfig.TOOL_REST_PATH + "/upload",
                dropZoneEnabled: false,
                maxFileCount: 1
            });
        $el.on('filepreupload', function(event, data, previewId, index, jqXHR) {
                var alreadyExists = _.find(self.state.fileData, function(v){
                    if(data.files[0].name === v.fileName)
                    {
                        return true;
                    }
                });
                if(alreadyExists) {
                    return {
                        "message": " File with same name already exists "
                    }
                }
         });
        $el.on("fileuploaded",function(event, data, previewId, index) {
           appActions.loadFileData();
           $el.fileinput('clear');
        })
    },
    getInitialStateAsync: function() {
        appActions.loadFileData();
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
            var createdDate = moment(item.createdDate).fromNow();
            files.push(<tr key={item.id}>
                    <td>
                        {item.fileName}
                    </td>
                    <td>
                        {createdDate}
                    </td>
                </tr>);
            });
        }
        return (
            <div className="panel panel-default">
                <div className="panel-heading"><strong>{this.props.i18nLib.t('wizard.steps.upload_files')}</strong></div>
                <div className="panel-body">
                    {this.props.i18nLib.t('wizard.upload_file.select_file')}
                    <input className="file" ref="iatiFileInput" type="file"/>
                </div>
                <table className="table file-list">
                    <thead>
                        <tr>
                            <th>
                                 {this.props.i18nLib.t('wizard.upload_file.filename')}
                            </th>
                            <th>
                                {this.props.i18nLib.t('wizard.upload_file.upload_date')}
                            </th>
                        </tr>
                    </thead>
                    <tbody>
                        {files}
                    </tbody>
                </table>
                <br /><br /><br />
                <div className="buttons">
                    <button className="btn btn-success navbar-btn btn-custom" type="button" onClick={this.props.eventHandlers.uploadFile}>{this.props.i18nLib.t('wizard.upload_file.next')}</button>
                </div>
            </div>
            );
    } 
}); 
module.exports = UploadFile;
