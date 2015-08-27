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
var FieldMappingsDropdown = require('./field-mappings-dropdown.jsx');


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
      }.bind(this)).catch(function(err) {        
        this.props.eventHandlers.displayError("Error retrieving value mappings");
      }.bind(this));
      this.loadTemplateData();
  },  
  switchTab: function(idx) {
    this.setState({
      activeTab: idx
    });
  },
  handleNext: function() {
    this.props.eventHandlers.mapValues(this.state.mappings);
  },
  updateValueMappings: function(sourceFieldData, selectedDestinationValue) {
    var mapping = _.find(this.state.mappings, function(v) { return v.sourceField.uniqueFieldName == sourceFieldData.sourceFieldName });
    var selectedDestination = _.find(mapping.destinationField.possibleValues, function(v) { return v.code == selectedDestinationValue});
    mapping.valueIndexMapping[sourceFieldData.sourceIndexValue] = selectedDestination ? selectedDestination.index : null;      
    //yeah, no mutation here. TODO: Fix it!
    this.forceUpdate();
  },
  
  loadMappingTemplate: function(id){	    
		appActions.loadValueMappingsById(id).then(function(data) {
			var mappings = this.state.mappings;			 
			 $.map(mappings, function(mapping, i) {
				 var templateMapping = _.find(data.fieldValueMapping, function(v) { return v.sourceField.uniqueFieldName == mapping.sourceField.uniqueFieldName});
				  if(templateMapping){
					 mapping.valueIndexMapping = templateMapping.valueIndexMapping; 
				 }
				 
			 });
			
			this.setState({mappings: mappings});        
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
		}.bind(this)).catch(function(err) { 
			console.log(err);      
			console.log('Error loading mapping templates')
		}.bind(this));
  },
  render: function() {	
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
      if (mapping.sourceField && mapping.destinationField && mapping.sourceField.fieldName && mapping.destinationField.fieldName && (mapping.sourceField.type == "LIST" || mapping.sourceField.type == "ORGANIZATION")) {
        sourceFields.push({
          tabName : mapping.sourceField.displayName,
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
          <TabbedArea activeTab={this.state.activeTab} paneModels={sourceFields} switchTab={this.switchTab}/>
        </div>
        <div className="buttons">
          <button className="btn btn-warning navbar-btn btn-custom" type="button" data-toggle="modal" data-target="#saveMapValues">{this.props.i18nLib.t('wizard.map_values.save')}</button>&nbsp;
          <button className="btn btn-success navbar-btn btn-custom" type="button" onClick={this.handleNext}>{this.props.i18nLib.t('wizard.map_values.next')}</button>
        </div>
        <SaveMappingsDialog {...this.props} reloadTemplateData = {this.loadTemplateData} mappings = {this.state.mappings} />
        </div>
      ); } }); 
      
      module.exports = MapValues;