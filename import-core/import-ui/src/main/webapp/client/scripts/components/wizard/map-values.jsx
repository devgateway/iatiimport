var React = require('react');
var reactAsync = require('react-async');
var Reflux = require('reflux');
var Router = require('react-router');
var appActions = require('./../../actions');
var Link = Router.Link;
var RouteHandler = Router.RouteHandler;
var MappingTableSimple = require('./map-values-table-simple');
var TabbedArea = require('./tabbed-area');
var SaveMappingsDialog = require('./save-value-mappings-dlg.jsx');
var valueMappingStore = require('./../../stores/ValueMappingStore');
var valueMappingTemplateStore = require('./../../stores/ValueMappingTemplateStore');
var _ = require('lodash/dist/lodash.underscore');
var FieldMappingsDropdown = require('./mappings-dropdown.jsx');
var constants = require('./../../utils/constants');
var common = require('./../../utils/common');

var MapValues = React.createClass({
    mixins: [Reflux.ListenerMixin],
  getInitialState: function() {
    return {
      activeTab: 0,
      mappings : [],
      mappingTemplatesData:[]
    }
  },
  componentDidMount: function() {
	  this.props.eventHandlers.updateCurrentStep(constants.MAP_VALUES);
      this.listenTo(valueMappingStore, this.updateValueMappingStore);
      this.listenTo(valueMappingTemplateStore, this.updateMappingTemplatesData);
      this.loadData();
  },
  updateValueMappingStore: function(data) {
      this.setState({
          mappings: data
      });
  },
  updateMappingTemplatesData: function(data) {
		this.setState({
			mappingTemplatesData: data
		});
	},
  loadData: function(){
      this.props.eventHandlers.showLoadingIcon();
      appActions.loadValueMappingData.triggerPromise().then(function(data) {
        this.props.eventHandlers.hideLoadingIcon();
        this.updateValueMappingStore(data);
      }.bind(this))["catch"](function(err) {
         console.log(err);
        this.props.eventHandlers.displayError(this.props.i18nLib.t('wizard.map_values.msg_error_retrieving_value_mappings'));
      }.bind(this));
      this.loadTemplateData();
  },
  switchTab: function(idx) {
    this.setState({
      activeTab: idx
    });
  },
  handleNext: function() {
    this.props.eventHandlers.mapValues(this.state.mappings, constants.DIRECTION_NEXT);
  },
  handlePrevious: function() {
	    this.props.eventHandlers.mapValues(this.state.mappings, constants.DIRECTION_PREVIOUS);
  },
  updateValueMappings: function(sourceFieldData, selectedDestinationValue) {
    var mapping = _.find(this.state.mappings, function(v) { return v.sourceField.uniqueFieldName == sourceFieldData.sourceFieldName });
    var selectedDestination = _.find(mapping.destinationField.possibleValues, function(v) { return v.code == selectedDestinationValue});
    mapping.valueIndexMapping[sourceFieldData.sourceIndexValue] = selectedDestination ? selectedDestination.index : null;
    console.log(mapping.valueIndexMapping);
    //yeah, no mutation here. TODO: Fix it!
    this.forceUpdate();
  },

  loadMappingTemplate: function(id){
		appActions.loadValueMappingsById(id).then(function(data) {
			//TODO: refactor
			var mappings = this.state.mappings;
			 $.map(mappings, function(mapping, i) {
				 var templateMapping = _.find(data.fieldValueMapping, function(v) { return v.sourceField.uniqueFieldName == mapping.sourceField.uniqueFieldName});
				  if(templateMapping){
					 for(var vmapping in templateMapping.valueIndexMapping ){
						var templateDestination =  _.find(templateMapping.destinationField.possibleValues, function(p){ return p.index == templateMapping.valueIndexMapping[vmapping]});
						var selectedDestination = _.find(mapping.destinationField.possibleValues, function(v) { return v && templateDestination && v.value == templateDestination.value});
						if(selectedDestination){
							mapping.valueIndexMapping[vmapping] = selectedDestination ? selectedDestination.index : null;

						}
					 }
				 }

			 });

       var mappingInfo = {
           name: data.name,
           id: data.id
         };

			this.setState({
          mappings: mappings,
          mappingInfo: mappingInfo
        });
			this.forceUpdate();
		}.bind(this));
	},
	deleteMappingTemplate: function(id){
		appActions.deleteValueMappingsTemplate(id).then(function(data) {
			var templateData = this.state.mappingTemplatesData;
			var dataAfterDelete = templateData.filter(function (item) {
				return item.id != id;
			});
			this.setState({
				mappingTemplatesData: dataAfterDelete
			});
			this.forceUpdate();
		}.bind(this));
	},
	loadTemplateData: function(){
		appActions.loadValueMappingsTemplateList.triggerPromise().then(function(data) {
			this.updateMappingTemplatesData(data);
		}.bind(this))["catch"](function(err) {
			this.props.eventHandlers.displayError(this.props.i18nLib.t('wizard.map_values.msg_error_loading_templates'));
		}.bind(this));
  },
  isMappingComplete: function(){
	var notMapped = _.filter(this.state.mappings, function(m) {
	     var hasNull = false;
       if(m.sourceField.fieldName === "participating-org" && m.sourceField.subType === "Funding") {
         return false;
       }
	     for (var member in m.valueIndexMapping) {
	        if (m.valueIndexMapping[member] == null)
	            hasNull = true;
	    }
	    return !_.isEmpty(m.valueIndexMapping) && hasNull;
	});
	return notMapped.length == 0
  },
  render: function() {
    var language = this.props.i18nLib.lng() || "en";
    var sourceFields = [];
    var message = "";
    if(!_.some(this.state.mappings, function(v){ return v.sourceField.type == 'LIST' || v.sourceField.type == 'ORGANIZATION' })) {
      message = <div className="panel panel-default">
                  <div className="panel-body">
                    {this.props.i18nLib.t('wizard.map_values.empty_list')}
                  </div>
                </div>;
    }
    $.map(this.state.mappings, function(mapping, i) {
      if (mapping.sourceField && mapping.destinationField && mapping.sourceField.fieldName && mapping.destinationField.fieldName && (mapping.sourceField.type == "LIST" || mapping.sourceField.type == "ORGANIZATION" || mapping.sourceField.type == "LOCATION")) {
        sourceFields.push({
          tabName : common.getDisplayValue(mapping.sourceField, language),
          tooltip: mapping.sourceField.description,
          children: [<MappingTableSimple key={i} mapping={mapping} handleUpdates={this.updateValueMappings} {...this.props}/>],
          classes : {}
        });
      }
    }.bind(this));

    return (
      <div className="panel panel-default">
        <div className="panel-heading"><strong>{this.props.i18nLib.t('wizard.map_values.map_field_values')}</strong></div>
        <div className="panel-body">
         <FieldMappingsDropdown {...this.props} mappingTemplatesData = {this.state.mappingTemplatesData} deleteMappingTemplate = {this.deleteMappingTemplate} loadMappingTemplate = {this.loadMappingTemplate} />
          {message}
          <TabbedArea activeTab={this.state.activeTab} paneModels={sourceFields} switchTab={this.switchTab} i18nLib={this.props.i18nLib} />
        </div>
        <div className="buttons">

        <div className="row">
        <div className="col-md-6">
           <button ref="previousButton"   className="btn btn-success navbar-btn btn-custom btn-previous" type="button" onClick={this.handlePrevious}>{this.props.i18nLib.t('wizard.map_values.previous')}</button>
         </div>
       <div className="col-md-6">
       <button className="btn btn-warning navbar-btn btn-custom" type="button" data-toggle="modal" data-target="#saveMapValues">{this.props.i18nLib.t('wizard.map_values.save')}</button>&nbsp;
       <button disabled = {this.isMappingComplete() ? "" : "disabled"} className="btn btn-success navbar-btn btn-custom" type="button" onClick={this.handleNext}>{this.props.i18nLib.t('wizard.map_values.next')}</button>
       </div>
      </div>
        </div>
        <SaveMappingsDialog {...this.props} loadMappingTemplate = {this.loadMappingTemplate} reloadTemplateData = {this.loadTemplateData} mappings = {this.state.mappings}  mappingInfo = {this.state.mappingInfo}/>
        </div>
      ); } });

      module.exports = MapValues;
