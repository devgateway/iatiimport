var React = require('react');
var SaveMappingsDialog = React.createClass({
    getInitialState: function() {
       return {name: ''};
    },
    saveMappings: function(){     
      this.props.saveHandler();
    },
    handleNameChange(e){
      this.setState({
            name: e.target.value
        });       
    },
    render: function () {
    return (
          <div className="modal fade" id="saveMapFields" tabindex="-1" role="dialog" aria-labelledby="myModalLabel2" aria-hidden="true" >
			  <div ref="saveMappingDialog" className="modal-dialog">
			    <div className="modal-content">
			      <div className="modal-header">
			        <button type="button" className="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">×</span></button>
			        <h4 className="modal-title" id="myModalLabel2">Save Mapping</h4>
			      </div>
			      <div className="modal-body">
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