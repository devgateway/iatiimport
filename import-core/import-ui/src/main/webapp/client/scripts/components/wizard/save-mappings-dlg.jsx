var React = require('react');
var formActions = require('./../../actions/form');
var SaveMappingsDialog = React.createClass({
    getInitialState: function() {
       return {name: ''};
    },
    saveMappings: function(){       
     formActions.saveFieldMappingsTemplate({fieldMapping:this.props.mappingFieldsData, name:this.state.name}).then(function(data) {
        if(data.error){
           this.displayError("Error saving template");
        }else{          
            this.props.refreshParent();
            $('#saveMapFields').modal('hide');
        }            
     }.bind(this)).catch(function(err) {
        this.displayError("Error saving template");
        console.log("Error saving template");
     }.bind(this));
    },
    handleNameChange(e){
       this.setState({name: e.target.value});       
    },
    displayError: function(msg){      
	     $(this.refs.message.getDOMNode()).html(msg);     
	     var box = $(this.refs.messageBox.getDOMNode());
	     box.show();
	     box.fadeOut({duration:10000});     
    },
    render: function () {
    return (
          <div className="modal fade" id="saveMapFields" tabindex="-1" role="dialog" aria-labelledby="myModalLabel2" aria-hidden="true" >
			  <div ref="saveMappingDialog" className="modal-dialog">
			    <div className="modal-content">
			      <div className="modal-header">
			        <button type="button" className="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">�</span></button>
			        <h4 className="modal-title" id="myModalLabel2">Save Mapping</h4>
			      </div>
			      <div className="modal-body">
			      <div className="alert alert-danger message-box" role="alert" ref="messageBox">
                   <span className="glyphicon glyphicon-exclamation-sign error-box" aria-hidden="true"></span>
                    <span className="sr-only">Error:</span>
                      <span ref="message"></span>
                   </div>
			        Mapping Name: <input ref="mappingsName" type="text" onChange = {this.handleNameChange} />
			      </div>
			      <div className="modal-footer">
			        <button type="button" className="btn btn-default" data-dismiss="modal">Close</button>
			        <button type="button" className="btn btn-primary" onClick={this.saveMappings}>Save Mapping</button>
			      </div>
			    </div>
			  </div>
			</div>
        );
    }
});

module.exports = SaveMappingsDialog;