var React = require('react');
var formActions = require('./../../actions/form');
var _ = require('lodash/dist/lodash.underscore');

var SaveMappingsDialog = React.createClass({
    getInitialState: function() {
       return {
            name: '',
            id: null
          };
    },
    saveMappingsCopy: function(event) {
      this.saveMappings(event, true);
    },
    saveMappings: function(event, saveCopy){
     var mappingId = null;
     if(saveCopy &&  typeof saveCopy === "boolean") {
       mappingId = null;
     } else {
       mappingId = this.props.mappingInfo ? this.props.mappingInfo.id : null;
     }

     var mapped = _.filter(this.props.mappingFieldsData, function(m) {
            return m.destinationField;
     });

     formActions.saveFieldMappingsTemplate({fieldMapping: mapped, name:this.state.name, id: mappingId }).then(function(data) {
        if(data.error){
           this.displayError(this.props.i18nLib.t('wizard.save_field_mappings_dlg.'+ data.error));
        }else{
        	this.refs.mappingsName.getDOMNode().value = '';
            this.props.reloadTemplateData();
            this.props.loadMappingTemplate(data.id);

            $('#saveMapFields').modal('hide');
        }
     }.bind(this))["catch"](function(err) {
        this.displayError(this.props.i18nLib.t('wizard.save_field_mappings_dlg.mapping_exists'));
     }.bind(this));
    },
    handleNameChange: function(e){
       this.setState({name: e.target.value});
    },
    componentWillReceiveProps: function(nextProps) {
      var mappingName = nextProps.mappingInfo ? nextProps.mappingInfo.name : "";
      this.setState({name: mappingName});
    },
    displayError: function(msg){
	     $(this.refs.message.getDOMNode()).html(msg);
	     var box = $(this.refs.messageBox.getDOMNode());
	     box.show();
	     box.fadeOut({duration:10000});
    },
    isValidName: function() {
     return this.state.name &&  this.state.name.trim().length > 0;
    },
    render: function () {

      return (
            <div className="modal fade" id="saveMapFields" tabIndex="-1" role="dialog" aria-labelledby="myModalLabel2" aria-hidden="true" >
  			  <div ref="saveMappingDialog" className="modal-dialog">
  			    <div className="modal-content">
  			      <div className="modal-header">
  			        <button type="button" className="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">x</span></button>
  			        <h4 className="modal-titlemappingFieldsData" id="myModalLabel2">{this.props.i18nLib.t('wizard.save_field_mappings_dlg.title')}</h4>
  			      </div>
  			      <div className="modal-body">
  			      <div className="alert alert-danger message-box" role="alert" ref="messageBox">
                     <span className="glyphicon glyphicon-exclamation-sign error-box" aria-hidden="true"></span>
                      <span className="sr-only">Error:</span>
                        <span ref="message"></span>
                     </div>
  			        Mapping Name: <input ref="mappingsName" type="text" onChange = {this.handleNameChange} value={this.state.name}/>
  			      </div>
  			      <div className="modal-footer">
                    <button type="button" className="btn btn-default btn-warning" data-dismiss="modal">{this.props.i18nLib.t('wizard.save_field_mappings_dlg.close')}</button>
                    <button type="button" disabled = {this.isValidName() ? "" : "disabled"}  className="btn btn-primary " onClick={this.saveMappings} >{this.props.i18nLib.t('wizard.save_field_mappings_dlg.save_mapping')}</button>
  			      </div>
  			    </div>
  			  </div>
  			</div>
          );
    }
});

module.exports = SaveMappingsDialog;
