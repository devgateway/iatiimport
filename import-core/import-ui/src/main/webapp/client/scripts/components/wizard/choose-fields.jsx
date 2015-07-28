var React = require('react');
var reactAsync = require('react-async');
var Reflux = require('reflux');
var appActions = require('./../../actions');
var Router = require('react-router');
var Link = Router.Link;
var CustomSelect = require('./custom-select');
var destinationFieldsStore = require('./../../stores/DestinationFieldsStore');
var sourceFieldsStore = require('./../../stores/SourceFieldsStore');
var fieldMappingStore = require('./../../stores/FieldMappingStore');
var _ = require('lodash/dist/lodash.underscore');

var ChooseFields = React.createClass({
    mixins: [Reflux.ListenerMixin],
    getInitialState: function() {
       return {sourceFieldsData:[], destinationFieldsData:[], mappingFieldsData:[]};
    },
    componentDidMount: function() {
        this.listenTo(destinationFieldsStore, this.updateDestinationFields);
        this.listenTo(sourceFieldsStore, this.updateSourceFields);
        this.listenTo(fieldMappingStore, this.updateFieldMappingStore);
        this.loadData();
    },    
    updateSourceFields: function(data) {
        this.setState({
            sourceFieldsData: data
        });
    },
    updateDestinationFields: function(data) {
        this.setState({
            destinationFieldsData: data
        });
    }, 
    updateFieldMappingStore: function(data) {
        this.setState({
            mappingFieldsData: data
        });
    }, 
     loadData: function(){      
      appActions.loadDestinationFieldsData.triggerPromise().then(function(data) {      
        this.props.eventHandlers.hideLoadingIcon();                       
        this.updateDestinationFields(data); 
      }.bind(this)).catch(function(err) {
        console.log("Error retrieving destination fields");
      }); 
      
      appActions.loadSourceFieldsData.triggerPromise().then(function(data) {      
        this.props.eventHandlers.hideLoadingIcon();                       
        this.updateSourceFields(data); 
      }.bind(this)).catch(function(err) {
        console.log("Error retrieving source fields");        
      });
      
      appActions.loadMappingFieldsData.triggerPromise().then(function(data) {      
        this.props.eventHandlers.hideLoadingIcon();                       
        this.updateFieldMappingStore(data); 
      }.bind(this)).catch(function(err) {
        console.log("Error retrieving field mappings");        
      });
    },  
    selectFieldMapping: function(event){
      this.props.eventHandlers.selectFieldMapping(event);
    },
    getOptions: function(sourceField){    
    var options = [];
    $.map(this.state.destinationFieldsData, function(item, i) {
            if(item.mappable && sourceField.type == item.type){              
               options.push({value:item.uniqueFieldName, label:item.displayName || item.uniqueFieldName})
            }              
         });
     return options
    }, 
    checkAll: function() {

    },
    handleNext: function() {
        this.props.eventHandlers.chooseFields(this.state.mappingFieldsData);
    },
    selectAll: function(event){
        this.forceUpdate();
    },
    handleToggle: function(item, event) {
        var mapping = [];
        if(event.target.checked) {
            var mappingObject = {
                sourceField: item,
                destinationField: null
            }
            mapping = this.state.mappingFieldsData.concat(mappingObject);
        }
        else
        {
            mapping =  _.reject(this.state.mappingFieldsData, function(v){ return v.sourceField.uniqueFieldName == item.uniqueFieldName;});
        }

        this.setState({
                mappingFieldsData: mapping
            });
        this.forceUpdate();
    },
    handleSelectToggle: function(sourceField, destinationFieldName) {
        var destinationField = _.find(this.state.destinationFieldsData, {uniqueFieldName: destinationFieldName});
        var mapping = _.find(this.state.mappingFieldsData, function(v){ return sourceField.uniqueFieldName == v.sourceField.uniqueFieldName});
        if(mapping) {
            mapping.destinationField = destinationField;
            this.forceUpdate();
        }
    },
    render: function() {
        var rows = [];
        if (this.state.destinationFieldsData && this.state.sourceFieldsData) {                    
           $.map(this.state.sourceFieldsData, function(item, i) {
                var options = this.getOptions(item);                
                rows.push(<tr key={item.fieldName}>
                    <td>
                        <input value={item.fieldName} aria-label="Field1" type="checkbox" onChange = {this.selectFieldMapping} className="source-selector"/>
                    </td>
                    <td>
                        <div className="table_cell">
                            {item.displayName}
                        </div>
                    </td>
                    <td>                   
                    <CustomSelect options={options} value="value" label="label" data={{sourceField:item.fieldName}} handleChange = {this.props.eventHandlers.updateFieldMappings}/>
                    </td>
                </tr>);
                var options = this.getOptions(item);
                if(item.mappable) {
                    var selected = _.some(this.state.mappingFieldsData, function(v) { return item.uniqueFieldName == v.sourceField.uniqueFieldName});
                    var mapping = _.find(this.state.mappingFieldsData, function(v) { return item.uniqueFieldName == v.sourceField.uniqueFieldName});
                    var value = "";
                    if(mapping && mapping.destinationField) {
                        value = mapping.destinationField.uniqueFieldName;
                    }
                    rows.push(<tr key={item.uniqueFieldName}>
                        <td>
                            <input value={item.uniqueFieldName} aria-label="Field1" type="checkbox" checked={selected} onChange={this.handleToggle.bind(this, item)}/>
                        </td>
                        <td>
                            <div className="table_cell">
                                {item.displayName}
                            </div>
                        </td>
                        <td>                   
                        <CustomSelect initialOption={value} options={options} value="value" label="label" data={item} handleChange={this.handleSelectToggle}/>
                        </td>
                    </tr>);
                }                
            }.bind(this));
        }

        return (
            <div className="panel panel-default">
                <div className="panel-heading"><strong>{this.props.i18nLib.t('wizard.map_fields.choose_map_fields')}</strong></div>
                <div className="panel-body">
                    <ul className="nav nav-pills navbar-right">
                        <li className="dropdown" role="presentation">
                            <a aria-expanded="false" className="dropdown-toggle" data-toggle="dropdown" href="#" role="button">
                                {this.props.i18nLib.t('wizard.map_fields.load_existing_template')}
                                <span className="caret"></span>
                            </a>
                            <ul className="dropdown-menu" role="menu">
                                <li role="presentation"><a aria-controls="template1" href="#template1">{this.props.i18nLib.t('wizard.map_fields.usual_field_mapping')}</a></li>
                                <li role="presentation"><a aria-controls="template2" href="#template2">{this.props.i18nLib.t('wizard.map_fields.other_field_mapping')}</a></li>
                            </ul>
                        </li>
                    </ul>
                    <table className="table">
                        <thead>
                            <tr>
                                <th>
                                    <input type="checkbox" checked={this.checkAll()} onChange={this.selectAll} />
                                     {this.props.i18nLib.t('wizard.map_fields.import_update')}
                                </th>
                                <th>
                                     {this.props.i18nLib.t('wizard.map_fields.source_field')}
                                </th>
                                <th>
                                     {this.props.i18nLib.t('wizard.map_fields.destination_field')}
                                </th>
                            </tr>
                        </thead>
                        <tbody>
                            {rows}
                        </tbody>
                    </table>
                </div>
                <div className="buttons">
                    <button className="btn btn-warning navbar-btn btn-custom" type="button">{this.props.i18nLib.t('wizard.map_fields.save')}</button>&nbsp;
                    <button className="btn btn-success navbar-btn btn-custom" type="button" onClick={this.handleNext}>{this.props.i18nLib.t('wizard.map_fields.next')}</button>
                </div>
                </div>
            ); } }); 
            
 module.exports = ChooseFields;