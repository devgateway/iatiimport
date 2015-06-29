var React = require('react');
var Router = require('react-router');
var Link = Router.Link;
var RouteHandler = Router.RouteHandler;
var MappingTable = require('./map-values-table');
var TabbedArea = require('./tabbed-area');
var MapValues = React.createClass({
  getInitialState: function() {
    return {
      activeTab: 0
    }
  },
  switchTab: function(idx) {
    this.setState({
      activeTab: idx
    });
  },
  render: function() {
    var sourceFields = [];
    var tabs = [];
    $.map(this.props.wizardData.fieldMappings, function(mapping, i) {
      if (mapping.sourceFieldName && mapping.destinationFieldName && mapping.selected) {
        sourceFields.push({
          tabName : mapping.sourceFieldName,
          children: [< MappingTable key = {i} sourceFieldName = {mapping.sourceFieldName} destinationFieldName = {mapping.destinationFieldName} updateValueMappings = {this.props.eventHandlers.updateValueMappings} />],
          classes : {}
        });
      }
    }.bind(this));

    return (
      <div className="panel panel-default">
        <div className="panel-heading"><strong>Map Fields Values</strong></div>
        <div className="panel-body">
          <TabbedArea activeTab={this.state.activeTab} paneModels={sourceFields} switchTab={this.switchTab}/>
        </div>
        <div className="buttons">
          <button className="btn btn-warning navbar-btn btn-custom" type="button">Save</button>&nbsp;
          <button className="btn btn-success navbar-btn btn-custom" type="button" onClick={this.props.eventHandlers.mapValues}>Next >></button>
        </div>
        </div>
      ); } }); 
      
      module.exports = MapValues;