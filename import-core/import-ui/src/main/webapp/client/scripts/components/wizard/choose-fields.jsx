var React = require('react');
var reactAsync = require('react-async');
var Reflux = require('reflux');
var appActions = require('./../../actions');
var Router = require('react-router');
var Link = Router.Link;
var SaveMappingsDialog = require('./save-field-mappings-dlg.jsx');
var FieldMappingsDropdown = require('./mappings-dropdown.jsx');
var CustomSelect = require('./custom-select');
var destinationFieldsStore = require('./../../stores/DestinationFieldsStore');
var sourceFieldsStore = require('./../../stores/SourceFieldsStore');
var fieldMappingStore = require('./../../stores/FieldMappingStore');
var fieldMappingTemplateStore = require('./../../stores/FieldMappingTemplateStore');
var _ = require('lodash/dist/lodash.underscore');
var formActions = require('./../../actions/form');

var appActions = require('./../../actions');

var ChooseFields = React.createClass({
	mixins: [Reflux.ListenerMixin],
	getInitialState: function() {
		return {sourceFieldsData:[], destinationFieldsData:[], mappingFieldsData:[],mappingTemplatesData:[]};
	},
	destDataLoaded:false,
	sourceDataLoaded:false,
	mappingDataLoaded:false,
	errorMsg: "",
	componentDidMount: function() {
		this.listenTo(destinationFieldsStore, this.updateDestinationFields);
		this.listenTo(sourceFieldsStore, this.updateSourceFields);
		this.listenTo(fieldMappingStore, this.updateFieldMappingStore);
		this.listenTo(fieldMappingTemplateStore, this.updateMappingTemplatesData);
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
	updateMappingTemplatesData: function(data) {
		this.setState({
			mappingTemplatesData: data
		});
	},  
	clearFlags: function(){
		this.destDataLoaded = false;
		this.sourceDataLoaded = false;
		this.mappingDataLoaded = false;
	}, 
	displayError: function(){
		if(this.destDataLoaded && this.sourceDataLoaded && this.mappingDataLoaded){
			this.props.eventHandlers.displayError(this.errorMsg); 
		}
	},
	hideLoadingIcon: function(){
		if(this.destDataLoaded && this.sourceDataLoaded && this.mappingDataLoaded){
			this.props.eventHandlers.hideLoadingIcon();
		}
	},   
	loadData: function(){
		this.clearFlags(); 
		this.errorMsg = "";
		this.props.eventHandlers.showLoadingIcon();
		appActions.loadDestinationFieldsData.triggerPromise().then(function(data) {                             
			this.updateDestinationFields(data);
			this.destDataLoaded = true; 
			this.hideLoadingIcon();
		}.bind(this)).catch(function(err) {
			this.destDataLoaded = true; 
			this.hideLoadingIcon();        
			this.errorMsg += " Error retrieving destination fields.";
			this.displayError(); 
		}.bind(this)); 

		appActions.loadSourceFieldsData.triggerPromise().then(function(data) {                              
			this.updateSourceFields(data);
			this.sourceDataLoaded = true;
			this.hideLoadingIcon();
		}.bind(this)).catch(function(err) {
			this.sourceDataLoaded = true;
			this.hideLoadingIcon();        
			this.errorMsg += " Error retrieving source fields.";  
			this.displayError();      
		}.bind(this));

		appActions.loadMappingFieldsData.triggerPromise().then(function(data) {                              
			this.updateFieldMappingStore(data); 
			this.mappingDataLoaded = true;
			this.hideLoadingIcon();
		}.bind(this)).catch(function(err) {
			this.mappingDataLoaded = true;
			this.hideLoadingIcon();       
			this.errorMsg += " Error retrieving field mappings.";  
			this.displayError();       
		}.bind(this));

		appActions.loadFieldMappingsTemplateList.triggerPromise().then(function(data) {                              
			this.updateMappingTemplatesData(data); 
		}.bind(this)).catch(function(err) { 
			console.log(err);      
			console.log('Error loading mapping templates')
		}.bind(this));
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
	loadMappingTemplate: function(id){     
		appActions.loadFieldMappingsById(id).then(function(data) {           
			this.setState({
				mappingFieldsData: data.fieldMapping
			});            
			this.forceUpdate();             
		}.bind(this));
	}, 
	deleteMappingTemplate: function(id){     
		appActions.deleteMappingTemplate(id).then(function(data) {    	   
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
	reloadTemplateData: function(){
		appActions.loadFieldMappingsTemplateList.triggerPromise().then(function(data) {                              
			this.updateMappingTemplatesData(data); 
		}.bind(this)).catch(function(err) { 
			console.log(err);      
			console.log('Error loading mapping templates')
		}.bind(this));
	},  
    render: function() {
        var rows = [];
        if (this.state.destinationFieldsData && this.state.sourceFieldsData) {             
           $.map(this.state.sourceFieldsData, function(item, i) {
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
                    <FieldMappingsDropdown {...this.props} mappingTemplatesData = {this.state.mappingTemplatesData} deleteMappingTemplate = {this.deleteMappingTemplate} loadMappingTemplate = {this.loadMappingTemplate} />
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
                    <button className="btn btn-warning navbar-btn btn-custom" type="button" data-toggle="modal" data-target="#saveMapFields">{this.props.i18nLib.t('wizard.map_fields.save')}</button>&nbsp;
                    <button className="btn btn-success navbar-btn btn-custom" type="button" onClick={this.handleNext}>{this.props.i18nLib.t('wizard.map_fields.next')}</button>
                </div>
                 <SaveMappingsDialog {...this.props} reloadTemplateData = {this.reloadTemplateData} mappingFieldsData = {this.state.mappingFieldsData} />		 
                </div>
            ); } }); 
            
 module.exports = ChooseFields;