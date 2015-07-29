var React = require('react');
var reactAsync = require('react-async');
var Reflux = require('reflux');
var Router = require('react-router');
var appActions = require('./../../actions');
var Link = Router.Link;
var RouteHandler = Router.RouteHandler;
var MappingTableSimple = require('./map-values-table-simple');
var TabbedArea = require('./tabbed-area');

var valueMappingStore = require('./../../stores/ValueMappingStore');
var _ = require('lodash/dist/lodash.underscore');


var MapValues = React.createClass({
    mixins: [Reflux.ListenerMixin],
  getInitialState: function() {
    return {
      activeTab: 0,
      mappings : []
    }
  },
  componentDidMount: function() {
      this.listenTo(valueMappingStore, this.updateValueMappingStore);
      this.loadData();
  },
  updateValueMappingStore: function(data) {
      this.setState({
          mappings: data
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
    mapping.valueIndexMapping[sourceFieldData.sourceIndexValue] = selectedDestination.index;
    //yeah, no mutation here. TODO: Fix it!
    this.forceUpdate();
  },

  render: function() {
    var sourceFields = [];
    $.map(this.state.mappings, function(mapping, i) {
      if (mapping.sourceField.fieldName && mapping.destinationField.fieldName && mapping.sourceField.type == "LIST") {
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
          <TabbedArea activeTab={this.state.activeTab} paneModels={sourceFields} switchTab={this.switchTab}/>
        </div>
        <div className="buttons">
          <button className="btn btn-warning navbar-btn btn-custom" type="button">{this.props.i18nLib.t('wizard.map_values.save')}</button>&nbsp;
          <button className="btn btn-success navbar-btn btn-custom" type="button" onClick={this.handleNext}>{this.props.i18nLib.t('wizard.map_values.next')}</button>
        </div>
        </div>
      ); } }); 
      
      module.exports = MapValues;