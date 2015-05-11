var React = require('react');
var fileStore = require('./../../stores/FileStore');
var reactAsync = require('react-async');
var Reflux = require('reflux');
var appActions = require('./../../actions');

var UploadFile = React.createClass({
  mixins: [reactAsync.Mixin, Reflux.ListenerMixin],
  componentDidMount: function() {
    $("#iati-file-input").fileinput();
    this.listenTo(fileStore, this.updateFileData);
  }, 
   getInitialStateAsync: function() {
    appActions.loadFileData();
    fileStore.listen(function(data) {
    try {     
        return cb(null, {          
          fileData: data.fileData
        });
      } catch(err) { 
      }
    });
   }, 
   updateFileData: function(data) {  
    this.setState({
      fileData: data.fileData     
    });
  },
  render: function() {
  var files = [];
  if(this.state.fileData && this.state.fileData.length > 0){
         this.state.fileData.forEach(function(item){ 
         files.push( <tr key={item.fileName}> <td>
                                        {item.fileName}
                                    </td>
                                    <td>
                                        {item.uploadDate}
                                    </td>
                                    <td>
                                        <span className="label label-success">View</span>
                                    </td>
                                </tr>)
         });
    }  
    return (    
        <div className="panel panel-default">
                <div className="panel-heading"><strong>Upload file(s)</strong></div>
                       <div className="panel-body">
                            Select files to upload
                            <input id="iati-file-input" type="file" className="file"/>
                        </div>
                        <div className="progress upload-progress-bar" >
                          <div className="progress-bar progress-bar-success" role="progressbar" aria-valuenow="40" aria-valuemin="0" aria-valuemax="100" >
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
                            <button type="button" className="btn btn-warning navbar-btn btn-custom"> Process</button>&nbsp;
                            <button type="button" className="btn btn-disabled navbar-btn btn-custom" >Next >></button>
                        </div>
        </div>
        
    );
  }
});

module.exports = UploadFile;
