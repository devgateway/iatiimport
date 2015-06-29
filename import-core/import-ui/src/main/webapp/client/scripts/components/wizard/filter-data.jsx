var React = require('react');
var reactAsync = require('react-async');
var Reflux = require('reflux');
var appActions = require('./../../actions');
var Router = require('react-router');
var Link = Router.Link;

var languageStore = require('./../../stores/LanguageStore');
var filterStore = require('./../../stores/FilterStore');

var FilterData = React.createClass({
    mixins: [
        reactAsync.Mixin, Reflux.ListenerMixin
    ],
    componentDidMount: function() {
        this.listenTo(languageStore, this.updateLanguages);
        this.listenTo(filterStore, this.updateFilters);
    },
    getInitialStateAsync: function() {
        appActions.loadLanguageData();
        languageStore.listen(function(data) {
            try {                
                return cb(null, {
                    languageData: data.languageData
                });
            } catch (err) {}
        });

        appActions.loadFilterData();
        filterStore.listen(function(data) {
            try {
                return cb(null, {
                    filterData: data.filterData
                });
            } catch (err) {}
        });
    },
    updateFilters: function(data) {
        this.setState({
            filterData: data.filterData
        });
    },
    updateLanguages: function(data) {
        this.setState({
            languageData: data.languageData
        });
    },
    render: function() {
        var filters = [];

        if (this.state.filterData) {
            $.map(this.state.filterData, function(filter, i) {
                var filterValues = [];
                $.map(filter.possibleValues, function(values, i) {
                    filterValues.push(
                            <div className="input-group">
                            <span className="input-group-addon">
                                <input aria-label={values.value} type="checkbox" value={values.code}/>
                            </span>
                            <input aria-label="Field1" className="form-control" readOnly type="text" value={values.value}/>
                            </div>
                        )
                });
//                debugger;
                filters.push(
                    <div className="panel panel-warning">
                        <div className="panel-heading">{filter.displayName}</div>
                        <div className="panel-body">
                            {filterValues}
                        </div>
                    </div>
                );
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
                <div className="panel-heading"><strong>Filter Information</strong></div>
                <div className="panel-body">
                    Select for each field, which values you would like to include as part of the import process
                    <br /><br />
                    {filters}
                    <div className="panel panel-warning">
                        <div className="panel-heading">Language</div>
                        <div className="panel-body">
                            {languages}
                        </div>
                    </div>
                </div>
                <div className="buttons">
                    <button className="btn btn-success navbar-btn btn-custom" type="button" onClick={this.props.eventHandlers.filterData}>Next >></button>
                </div>
                </div>
            ); } 
});

module.exports = FilterData;