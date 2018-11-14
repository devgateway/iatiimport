var React = require('react');
var reactAsync = require('react-async');
var Reflux = require('reflux');
var appActions = require('./../../actions');
var Router = require('react-router');
var Link = Router.Link;
var _ = require('lodash/dist/lodash.underscore');
var languageStore = require('./../../stores/LanguageStore');
var filterStore = require('./../../stores/FilterStore');
var constants = require('./../../utils/constants');
var Tooltip = require('./tooltip');

var FilterData = React.createClass({
	mixins: [Reflux.ListenerMixin],
	getInitialState: function() {
		return {filterData: [], languageData:[]};
	},
	languageDataLoaded: false,
	filterDataLoaded: false,
	errorMsg:"",
	componentDidMount: function() {
		this.props.eventHandlers.updateCurrentStep(constants.FILTER_DATA);
		this.listenTo(languageStore, this.updateLanguages);
		this.listenTo(filterStore, this.updateFilters);
		this.loadData();
	},    
	updateFilters: function(data) {
		this.setState({
			filterData: data
		});
	},
	updateLanguages: function(data) {
		this.setState({
			languageData: data
		});
	},
	loadData: function(){      
		this.clearFlags();
		this.errorMsg = "";
		this.props.eventHandlers.showLoadingIcon();
		appActions.loadLanguageData.triggerPromise().then(function(data) {                            
			this.updateLanguages(data); 
			this.languageDataLoaded = true;
			this.hideLoadingIcon();
		}.bind(this))["catch"](function(err) {       
			this.languageDataLoaded = true;
			this.hideLoadingIcon(); 
			this.errorMsg += this.props.i18nLib.t('wizard.filter_data.msg_error_retrieving_languages');
			this.displayError();        
		}.bind(this));

		appActions.loadFilterData.triggerPromise().then(function(data) {                              
			this.updateFilters(data);
			this.filterDataLoaded = true; 
			this.hideLoadingIcon(); 
		}.bind(this))["catch"](function(err) {
			this.filterDataLoaded = true; 
			this.hideLoadingIcon(); 
			this.errorMsg += this.props.i18nLib.t('wizard.filter_data.msg_error_retrieving_filters');
			this.displayError();                          
		}.bind(this));
	},
	clearFlags: function(){     
		this.languageDataLoaded = false;
		this.filterDataLoaded = false;        
	}, 
	hideLoadingIcon: function(){
		if(this.languageDataLoaded && this.filterDataLoaded){
			this.props.eventHandlers.hideLoadingIcon();
		}
	}, 
	displayError: function(){
		if(this.languageDataLoaded && this.filterDataLoaded){
			this.props.eventHandlers.displayError(this.errorMsg); 
		}
	},
	handleToggle: function(field, value, event) {        
		var currentField = _.find(this.state.filterData, { 'fieldName': field.fieldName });
		var filterValue = value.code ? value.code : value.value;
		var filterExists = _.some(currentField.filters, function(a) { return a == filterValue});
		if (!filterExists && event.target.checked) {
			currentField.filters.push(filterValue);
		} else	{
			currentField.filters = _.without(currentField.filters, filterValue);
		}
		var currentFilterData = this.state.filterData;

		this.setState( { filterData: this.state.filterData });
	},
	handleToggleRadio: function(field, value, event) {        
		var currentField = _.find(this.state.filterData, { 'fieldName': field.fieldName });		
		if(event.target.checked) {
			currentField.filters = [];
			currentField.filters.push(value.code);
		}
		else
		{
			currentField.filters = [];
		}
		this.setState( { filterData: this.state.filterData });
	},
	handleLanguageToggle: function(language, event){
		var languages = this.state.languageData;
		var currentLanguage = _.find(languages, { 'code': language.code });
		currentLanguage.selected = event.target.checked;
		this.setState({languageData:languages});		
	},
	handleNext: function() {
		this.props.eventHandlers.filterData(this.state.languageData,this.state.filterData,constants.DIRECTION_NEXT);
	},
	handlePrevious: function(){
		this.props.eventHandlers.filterData(this.state.languageData,this.state.filterData,constants.DIRECTION_PREVIOUS);
	},
	selectAll: function(field, event) {
		if(event.target.checked) {			
			field.filters = [];
			_.each(field.possibleValues, function(item) {
			    var filterValue = item.code ? item.code : item.value;
			    field.filters.push(filterValue);
			 });
		} else {
			field.filters = [];
		}
		this.setState({
			filterData: this.state.filterData
		});
	},
	isValid: function(){
	  var requiredFiltersState = [];
	  if (this.state.filterData) {
           var requiredFiltersState = $.map(this.state.filterData, function(filter, i) {
            	if(filter.filterRequired && filter.possibleValues.length > 0){
            		return filter.filters.length > 0
            	}
            }.bind(this));
      }
	  return (_.indexOf(requiredFiltersState, false) == -1)
	},
    render: function() {
        var filters = [];
        if (this.state.filterData) {
            $.map(this.state.filterData, function(filter, i) {
                var filterValues = [];                
                $.map(filter.possibleValues, function(values, i) {
                    var checkedValue = _.some(filter.filters, function(v){ return v === values.code || v === values.value});
                    if(filter.exclusive){
                       filterValues.push(
                            <div className="input-group">
                            <span className="input-group-addon">
                                <input aria-label={values.value} name = {filter.fieldName} className="value-select" type="radio" checked={checkedValue} onChange={this.handleToggleRadio.bind(this, filter, values)} />
                            </span>
                            <input aria-label="Field1" className="form-control" readOnly type="text" value={values.value}/>
                            </div>
                        )
                    }else{                      
                       filterValues.push(
                            <div className="input-group">
                            <span className="input-group-addon">
                                <input aria-label={values.value} className="value-select" type="checkbox" checked={checkedValue} onChange={this.handleToggle.bind(this, filter, values)} />
                            </span>
                            <input aria-label="Field1" className="form-control" readOnly type="text" value={(values.code ? values.code + ' : ' : '' ) +  values.value}/>
                            </div>
                        )
                    }                    
                }.bind(this));

                if(filterValues.length > 0 ) {
                    var groupSelector = "";
                    if(!filter.exclusive){
                       groupSelector = <input type="checkbox" className="group-select" onChange={this.selectAll.bind(this, filter)} />;
                    }
                    filters.push(
                        <div className="panel panel-warning filter-group">
                            <div className="panel-heading filter-group-header"><Tooltip i18nLib={this.props.i18nLib} tooltip={filter.description}/><span className="filter-group-title">{filter.displayName} {filter.filterRequired ? "*" : ""}</span>{groupSelector}</div>
                            <div className="panel-body">
                                {filterValues}
                            </div>
                        </div>
                    );
                }
            }.bind(this));
        }

        var languages = [];
        if (this.state.languageData) {
            $.map(this.state.languageData, function(language, i) {
                languages.push(<div className="input-group">
                    <span className="input-group-addon">
                        <input aria-label="language" type="checkbox" value={language.code} checked={language.selected} onChange={this.handleLanguageToggle.bind(this, language)}/>
                    </span>
                    <input aria-label="Field1" className="form-control" readOnly type="text" value={language.description}/>
                </div>);
            }.bind(this));
        }

        return (
            <div className="panel panel-default">
                <div className="panel-heading"><strong>{this.props.i18nLib.t('wizard.filter_data.filter_information')}</strong></div>
                <div className="panel-body">                   
                    {this.props.i18nLib.t('wizard.filter_data.select_filters')}
                    <br /><br />
                    {filters}
                    {languages.length > 0 &&
                        <div className="panel panel-warning">
                        <div className="panel-heading">{this.props.i18nLib.t('wizard.filter_data.language')}</div>
                        <div className="panel-body">
                            {languages}
                        </div>
                      </div>  
                    }
                   
                </div>
                <div className="buttons">
                    <div className="row">                          
                          <div className="col-md-6"><button className="btn btn-success navbar-btn btn-custom btn-previous" type="button" onClick={this.handlePrevious}>{this.props.i18nLib.t('wizard.filter_data.previous')}</button></div>
                          <div className="col-md-6"><button disabled = { this.isValid() ? "" : "disabled"} className="btn btn-success navbar-btn btn-custom btn-next" type="button" onClick={this.handleNext}>{this.props.i18nLib.t('wizard.filter_data.next')}</button></div>
                     </div>                   
                </div>
                </div>
            ); 
    } 
});

module.exports = FilterData;