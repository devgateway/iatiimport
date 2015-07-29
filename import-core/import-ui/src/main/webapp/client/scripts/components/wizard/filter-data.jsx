var React = require('react');
var reactAsync = require('react-async');
var Reflux = require('reflux');
var appActions = require('./../../actions');
var Router = require('react-router');
var Link = Router.Link;
var _ = require('lodash/dist/lodash.underscore');

var languageStore = require('./../../stores/LanguageStore');
var filterStore = require('./../../stores/FilterStore');

var FilterData = React.createClass({
    mixins: [Reflux.ListenerMixin],
     getInitialState: function() {
       return {filterData: [], languageData:[]};
    },
    languageDataLoaded: false,
    filterDataLoaded: false,    
    componentDidMount: function() {
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
      this.props.eventHandlers.showLoadingIcon();
      appActions.loadLanguageData.triggerPromise().then(function(data) {                            
         this.updateLanguages(data); 
         this.languageDataLoaded = true;
         this.hideLoadingIcon();
      }.bind(this)).catch(function(err) {       
        this.languageDataLoaded = true;
        this.hideLoadingIcon(); 
        this.props.eventHandlers.displayError("Error retrieving languages");
      }.bind(this));
      
      appActions.loadFilterData.triggerPromise().then(function(data) {                              
        this.updateFilters(data);
        this.filterDataLoaded = true; 
        this.hideLoadingIcon(); 
      }.bind(this)).catch(function(err) {
         this.filterDataLoaded = true; 
         this.hideLoadingIcon(); 
         this.props.eventHandlers.displayError("Error retrieving filters");                 
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
    handleToggle: function(field, value, event) {        
        var currentField = _.find(this.state.filterData, { 'fieldName': field.fieldName });
        var filterExists = _.some(currentField.filters, function(a) { return a == value.code});
        if(!filterExists && event.target.checked) {
            currentField.filters.push(value.code);
        }
        else
        {
            currentField.filters = _.without(currentField.filters, value.code);
        }
        var currentFilterData = this.state.filterData;

        this.setState( { filterData: this.state.filterData });
    },
    handleNext: function() {
        this.props.eventHandlers.filterData(this.state.filterData);
    },
    selectAll: function(field, event) {
        if(event.target.checked) {
            field.filters = _.pluck(field.possibleValues, 'code');
        } 
        else
        {
            field.filters = [];
        }
        this.setState({
            filterData: this.state.filterData
        });
    },
    render: function() {
        var filters = [];
        if (this.state.filterData) {
            $.map(this.state.filterData, function(filter, i) {
                var filterValues = [];
                $.map(filter.possibleValues, function(values, i) {
                    var checkedValue = _.some(filter.filters, function(v){ return v == values.code});
                    filterValues.push(
                            <div className="input-group">
                            <span className="input-group-addon">
                                <input aria-label={values.value} className="value-select" type="checkbox" checked={checkedValue} onChange={this.handleToggle.bind(this, filter, values)} />
                            </span>
                            <input aria-label="Field1" className="form-control" readOnly type="text" value={values.value}/>
                            </div>
                        )
                }.bind(this));

                if(filterValues.length > 0 ) {
                    filters.push(
                        <div className="panel panel-warning filter-group">
                            <div className="panel-heading filter-group-header"><span className="filter-group-title">{filter.displayName}</span> <input type="checkbox" className="group-select" onChange={this.selectAll.bind(this, filter)} /></div>
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
                        <input aria-label="language" type="checkbox" value={language.code}/>
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
                    <div className="panel panel-warning">
                        <div className="panel-heading">{this.props.i18nLib.t('wizard.filter_data.language')}</div>
                        <div className="panel-body">
                            {languages}
                        </div>
                    </div>
                </div>
                <div className="buttons">
                    <button className="btn btn-success navbar-btn btn-custom btn-next" type="button" onClick={this.handleNext}>{this.props.i18nLib.t('wizard.filter_data.next')}</button>
                </div>
                </div>
            ); 
    } 
});

module.exports = FilterData;