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
var constants = require('./../../utils/constants');

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
		this.props.eventHandlers.updateCurrentStep(constants.CHOOSE_FIELDS);
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
		}.bind(this))["catch"](function(err) {
			this.destDataLoaded = true;
			this.hideLoadingIcon();
			this.errorMsg += this.props.i18nLib.t('wizard.map_fields.msg_error_retrieving_destination_fields');
			this.displayError();
		}.bind(this));

		appActions.loadSourceFieldsData.triggerPromise().then(function(data) {
			this.updateSourceFields(data);
			this.sourceDataLoaded = true;
			this.hideLoadingIcon();
		}.bind(this))["catch"](function(err) {
			this.sourceDataLoaded = true;
			this.hideLoadingIcon();
			this.errorMsg += this.props.i18nLib.t('wizard.map_fields.msg_error_retrieving_source_fields');
			this.displayError();
		}.bind(this));

		appActions.loadMappingFieldsData.triggerPromise().then(function(data) {
			this.updateFieldMappingStore(data);
			this.mappingDataLoaded = true;
			this.hideLoadingIcon();
		}.bind(this))["catch"](function(err) {
			this.mappingDataLoaded = true;
			this.hideLoadingIcon();
			this.errorMsg += this.props.i18nLib.t('wizard.map_fields.msg_error_retrieving_mappings');
			this.displayError();
		}.bind(this));

		appActions.loadFieldMappingsTemplateList.triggerPromise().then(function(data) {
			this.updateMappingTemplatesData(data);
		}.bind(this))["catch"](function(err) {
			this.errorMsg += this.props.i18nLib.t('wizard.map_fields.msg_error_retrieving_templates');
			this.displayError();
		}.bind(this));
	},
	selectFieldMapping: function(event){
		this.props.eventHandlers.selectFieldMapping(event);
	},
	getOptions: function(sourceField, language){
		var options = [];
		$.map(this.state.destinationFieldsData, function(item, i) {
			var label = "";
			if(item.multiLangDisplayName && item.multiLangDisplayName[language]) {
				label = item.multiLangDisplayName[language];
			}
			else {
				label = item.displayName || item.uniqueFieldName;
			}
			if(item.mappable && sourceField.type == item.type){
				options.push({value:item.uniqueFieldName, label:label})
			}else if(item.mappable && sourceField.type != item.type && (item.type === "STRING" || item.type === "MULTILANG_STRING") && (sourceField.type === "STRING" || sourceField.type === "MULTILANG_STRING") ){
		      options.push({value:item.uniqueFieldName, label:label})
		    }
		});
		return options
	},
	checkAll: function() {
		var mappableFields = _.where(this.state.sourceFieldsData, {mappable: true});
		return (mappableFields.length == this.state.mappingFieldsData.length);
	},
	handleNext: function() {
		this.props.eventHandlers.chooseFields(this.state.mappingFieldsData, constants.DIRECTION_NEXT);
	},
	handlePrevious: function() {
		this.props.eventHandlers.chooseFields(this.state.mappingFieldsData, constants.DIRECTION_PREVIOUS);
	},
	selectAll: function(event){
		var mappings = this.state.mappingFieldsData;
		if(event.target.checked) {
			_.each(this.state.sourceFieldsData, function(sourceField){
				if(_.find(mappings, function(v){ return v.sourceField.uniqueFieldName == sourceField.uniqueFieldName;}) == null && sourceField.mappable){
					var mappingObject = {
							sourceField: sourceField,
							destinationField: null
					};
					mappings.push(mappingObject);
				}

			});
		}else{
			mappings = [];
		}
		this.setState({
			mappingFieldsData: mappings
		});
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
		}else{
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
		var mappingFieldsData = this.state.mappingFieldsData;
		var self = this;
		var mappings = [];
		appActions.loadFieldMappingsById(id).then(function(data) {
			$.map(this.state.sourceFieldsData, function(item, i) {
				var mappingFromTemplate = _.find(data.fieldMapping, function(v) { return item.uniqueFieldName == v.sourceField.uniqueFieldName});
				if(mappingFromTemplate){
					mappingFromTemplate.destinationField = _.find(self.state.destinationFieldsData, {uniqueFieldName: mappingFromTemplate.destinationField.uniqueFieldName});
					mappingFromTemplate.sourceField  = _.find(self.state.sourceFieldsData, {uniqueFieldName: mappingFromTemplate.sourceField.uniqueFieldName});
					mappings.push(mappingFromTemplate);
				}


			});
			var mappingInfo = {
					name: data.name,
					id: data.id
				};
			this.setState({
				mappingFieldsData: mappings,
				mappingInfo: mappingInfo
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
		}.bind(this))["catch"](function(err) {
			this.errorMsg += this.props.i18nLib.t('wizard.map_fields.msg_error_retrieving_templates');
			this.displayError();
		}.bind(this));
	},
	isMappingComplete: function(){
		var notMapped = _.filter(this.state.mappingFieldsData, function(m) {
			return _.isUndefined(m.destinationField) || _.isNull(m.destinationField)
	    });
		return (this.state.mappingFieldsData.length > 0 && notMapped.length == 0)
	},
    render: function() {
    	var rows = {};
        if (this.state.destinationFieldsData && this.state.sourceFieldsData) {
           var infoMessages = "";
           if(this.state.destinationFieldsData.length > 0) {
                var requiredMessage = [];
                var dependenciesMessage = [];
                _.map(this.state.destinationFieldsData, function(item) {
                        if(item.required) {
                            requiredMessage.push(<div><strong>{item.displayName}</strong>{this.props.i18nLib.t('wizard.map_fields.msg_required_field',{field:item.displayName})}</div>);
                        }
                        if(item.dependencies.length > 0){
                            var dependencies = _.pluck(item.dependencies, 'displayName').join(", ");
                            dependenciesMessage.push(<div ><strong>{item.displayName}</strong>{this.props.i18nLib.t('wizard.map_fields.msg_field_has_dependencies',{field:item.displayName, dependencies:dependencies})}</div>);
                        }
                    }.bind(this));
                if(requiredMessage.length > 0 || dependenciesMessage.length > 0) {
                    infoMessages = <div className="alert alert-info" role="alert">{requiredMessage} {dependenciesMessage}</div>;
                }
           };

           $.map(this.state.sourceFieldsData, function(item, i) {
                var language = this.props.i18nLib.lng() || "en";
                var options = this.getOptions(item, language);
                if(item.mappable) {
                	if(!rows[item.type]) {
                		rows[item.type] = [];
                		rows[item.type].push(<tr className="group-header"><td className = "group-title">{this.props.i18nLib.t('wizard.map_fields.' + item.type.toLowerCase())}</td> <td ></td> <td ></td></tr>);
                	}
                    var selected = _.some(this.state.mappingFieldsData, function(v) { return item.uniqueFieldName == v.sourceField.uniqueFieldName});
                    var mapping = _.find(this.state.mappingFieldsData, function(v) { return item.uniqueFieldName == v.sourceField.uniqueFieldName});

                    var value = "";
                    var cellMessage = "";
                    if(mapping && mapping.destinationField) {
                        value = mapping.destinationField.uniqueFieldName;
                        if(mapping.destinationField.required) {
                            cellMessage = <span className="label label-danger">Required</span>;
                        }
                    }
                    rows[item.type].push(<tr key={item.uniqueFieldName}>
                        <td>
                            <input value={item.uniqueFieldName} aria-label="Field1" type="checkbox" className="source-selector" checked={selected} onChange={this.handleToggle.bind(this, item)}/>
                        </td>
                        <td>
                            <div className="table_cell">
                                {item.displayName}
                            </div>
                        </td>
                        <td >
                        <CustomSelect {...this.props} disabled = {mapping ? "" : "disabled"} initialOption={value} options={options} value="value" label="label" data={item} handleChange={this.handleSelectToggle}/>
                        {cellMessage}
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
                    {infoMessages}
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
                        {rows[constants.FIELD_TYPE.MULTILANG_STRING]}
                        {rows[constants.FIELD_TYPE.STRING]}
                        {rows[constants.FIELD_TYPE.LIST]}
                        {rows[constants.FIELD_TYPE.LOCATION]}
                        {rows[constants.FIELD_TYPE.DATE]}
                        {rows[constants.FIELD_TYPE.ORGANIZATION]}
                        {rows[constants.FIELD_TYPE.TRANSACTION]}
                        </tbody>
                    </table>
                </div>
                <div className="buttons">
                <div className="row">
                <div className="col-md-6">
                   <button ref="previousButton"  className="btn btn-success navbar-btn btn-custom btn-previous" type="button" onClick={this.handlePrevious}>{this.props.i18nLib.t('wizard.map_fields.previous')}</button>
                 </div>
               <div className="col-md-6">
                  <button className="btn btn-warning navbar-btn btn-custom" type="button" data-toggle="modal" data-target="#saveMapFields">{this.props.i18nLib.t('wizard.map_fields.save')}</button>&nbsp;
                  <button disabled = {this.isMappingComplete() ? "" : "disabled"}  className="btn btn-success navbar-btn btn-custom" type="button" onClick={this.handleNext}>{this.props.i18nLib.t('wizard.map_fields.next')}</button>
               </div>
                </div>
                </div>
                 <SaveMappingsDialog {...this.props} loadMappingTemplate = {this.loadMappingTemplate} reloadTemplateData = {this.reloadTemplateData} mappingFieldsData = {this.state.mappingFieldsData} mappingInfo = {this.state.mappingInfo}/>
                </div>
            ); } });

 module.exports = ChooseFields;
