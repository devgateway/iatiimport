var React = require('react');
var reactAsync = require('react-async');
var Reflux = require('reflux');
var appActions = require('./../../actions');
var Router = require('react-router');
var Link = Router.Link;

var languageStore = require('./../../stores/LanguageStore');
var recipientStore = require('./../../stores/RecipientStore');

var FilterData = React.createClass({
    mixins: [
        reactAsync.Mixin, Reflux.ListenerMixin
    ],
    componentDidMount: function() {
        this.listenTo(languageStore, this.updateLanguages);
        this.listenTo(recipientStore, this.updateRecipients);
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
        appActions.loadRecipientData();
        recipientStore.listen(function(data) {
            try {
                return cb(null, {
                    recipientData: data.recipientData
                });
            } catch (err) {}
        });
    },
    updateRecipients: function(data) {
        this.setState({
            recipientData: data.recipientData
        });
    },
    updateLanguages: function(data) {
        this.setState({
            languageData: data.languageData
        });
    },
    render: function() {
        var recipients = [];

        if (this.state.recipientData) {
            $.map(this.state.recipientData, function(recipient, i) {
                recipients.push(<div className="input-group">
                    <span className="input-group-addon">
                        <input aria-label="Recipient" type="checkbox" value={recipient.id}/>
                    </span>
                    <input aria-label="Field1" className="form-control" readOnly type="text" value={recipient.name}/>
                </div>);
            }.bind(this));
        }

        var languages = [];
        if (this.state.languageData) {
            $.map(this.state.languageData, function(language, i) {
                languages.push(<div className="input-group">
                    <span className="input-group-addon">
                        <input aria-label="language" type="checkbox" value={language.name}/>
                    </span>
                    <input aria-label="Field1" className="form-control" readOnly type="text" value={language.name}/>
                </div>);
            }.bind(this));
        }

        return (
            <div className="panel panel-default">
                <div className="panel-heading"><strong>Filter Information</strong></div>
                <div className="panel-body">
                    Select for each field, which values you would like to include as part of the import process
                    <br /><br />
                    <div className="panel panel-warning">
                        <div className="panel-heading">Recipient Country</div>
                        <div className="panel-body">
                            {recipients}
                        </div>
                    </div>
                    <div className="panel panel-warning">
                        <div className="panel-heading">Language</div>
                        <div className="panel-body">
                            {languages}
                        </div>
                    </div>
                </div>
                <div className="buttons">
                    <button className="btn btn-success navbar-btn btn-custom" type="button">Next >></button>
                </div>
                </div>
            ); } }); module.exports = FilterData;