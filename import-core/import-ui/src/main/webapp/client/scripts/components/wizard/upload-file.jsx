var React = require('react');
var _ = require('lodash/dist/lodash.underscore');
var fileStore = require('./../../stores/FileStore');
var moment = require('moment');
var reactAsync = require('react-async');
var Reflux = require('reflux');
var appActions = require('./../../actions');
var appConfig = require('./../../conf');
var constants = require('./../../utils/constants');

var UploadFile = React.createClass({
    mixins: [Reflux.ListenerMixin
    ],
    getInitialState: function() {
       return {fileData: [], valid : true};
    },
    componentDidMount: function() {        
    	this.props.eventHandlers.updateCurrentStep(constants.UPLOAD_FILE);
        this.listenTo(fileStore, this.updateFileData);             
        var $el = $(this.refs.iatiFileInput.getDOMNode());
        var self = this;    
        $el.fileinput(
            {
                allowedFileExtensions : ['xml'],
                uploadUrl: appConfig.TOOL_HOST + appConfig.TOOL_REST_PATH + "/upload",
                dropZoneEnabled: false,
                maxFileCount: 1,
                allowedPreviewTypes:'none'
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
                        "message": self.props.i18nLib.t('wizard.upload_file.msg_file_exists')
                    }
                }
         });
        $el.on("fileuploaded",function(event, data, previewId, index) {
           self.loadData();
           $el.fileinput('clear');
        });
        
        $el.on("fileuploaderror",function(event, data, previewId, index) {
        	//$el.fileinput('clear');
            //self.props.eventHandlers.displayError(self.props.i18nLib.t('wizard.upload_file.msg_error_upload_failed'));
         });    
         this.loadData();    
        
    },    
    loadData: function(){  
      this.props.eventHandlers.showLoadingIcon();  
      appActions.loadFileData.triggerPromise().then(function(data) {      
        this.props.eventHandlers.hideLoadingIcon();                       
        this.updateFileData(data); 
      }.bind(this))["catch"](function(err) {       
        this.props.eventHandlers.hideLoadingIcon(); 
        this.props.eventHandlers.displayError(this.props.i18nLib.t('wizard.upload_file.msg_error_retrieving_files'));
      }.bind(this)); 
    },
    updateFileData: function(data) {
    	var $el = $(this.refs.iatiFileInput.getDOMNode());
        this.setState({fileData: data}, function(){
        	if(this.state.fileData.length > 0){
        		$el.fileinput('disable');
        	}
        });
    },
    handleDelete: function(e){   
    	var $el = $(this.refs.iatiFileInput.getDOMNode());
    	if(confirm("Are you sure you want to delete " + e.target.getAttribute('data-name') + "?")){
    		var id = e.target.getAttribute('data-id');
    		appActions.deleteImport(id).then(function(data) {    	   
    			this.updateFileData([]);    			
    			this.forceUpdate();
    			$el.fileinput('enable');
    		}.bind(this)); 		
    	}    	
    },
    handlePrevious: function(){
        this.props.eventHandlers.goHome();
     },
    render: function() {
        var files = [];
        if (this.state.fileData && this.state.fileData.length > 0) {   
        	
        var validFiles = _.filter(this.state.fileData, function(file) {  
        	return file.valid == true
	    });	
        	
        $.map(this.state.fileData, function (item, i) {    
            var createdDate = moment(item.createdDate).fromNow();
            var isValidStyle = item.valid ? 'label label-success': 'label label-danger';
            var isValidText = this.props.i18nLib.t('wizard.upload_file.' + (item.valid ? 'valid':'invalid'));
            if(!item.valid) {            	
                this.props.eventHandlers.displayError(this.props.i18nLib.t('wizard.upload_file.msg_invalid_file'));
            }
            files.push(<tr key={item.id}>
                    <td>
                        {item.fileName}
                    </td>
                    <td>
                        {createdDate}
                    </td>
                    <td>
                        <span className={isValidStyle}>{isValidText}</span>
                    </td>
                    <td><span data-id={item.id} data-name={item.fileName} className="glyphicon glyphicon-remove" onClick={this.handleDelete}></span></td>
                </tr>);
            }.bind(this));
        }
        return (
            <div className="panel panel-default">
                <div className="panel-heading"><strong>{this.props.i18nLib.t('wizard.steps.upload_files')}</strong></div>
                <div className="panel-body">
                    {this.props.i18nLib.t('wizard.upload_file.select_file')}
                    <input className="file" ref="iatiFileInput" type="file"   />
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
                            <th>
                                {this.props.i18nLib.t('wizard.upload_file.valid')}
                            </th>
                            <th>{this.props.i18nLib.t('wizard.upload_file.action')}</th>
                        </tr>
                    </thead>
                    <tbody>
                        {files}
                    </tbody>
                </table>
                <br /><br /><br />
                <div className="buttons">
                        <div className="col-md-6"><button className="btn btn-success navbar-btn btn-custom btn-previous" type="button" onClick={this.handlePrevious}>{this.props.i18nLib.t('wizard.previous')}</button></div>
                    <button disabled = {validFiles && validFiles.length > 0 ? "" : "disabled"} className="btn btn-success navbar-btn btn-custom" type="button" onClick={this.props.eventHandlers.uploadFile}>{this.props.i18nLib.t('wizard.upload_file.next')}</button>
                </div>
            </div>
            );
    } 
}); 
module.exports = UploadFile;
