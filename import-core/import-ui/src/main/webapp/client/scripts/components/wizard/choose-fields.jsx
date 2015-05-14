var React = require('react');
var reactAsync = require('react-async');
var Reflux = require('reflux');
var appActions = require('./../../actions');
var Router = require('react-router');
var Link = Router.Link;

var destinationFieldsStore = require('./../../stores/DestinationFieldsStore');
var sourceFieldsStore = require('./../../stores/SourceFieldsStore');

var ChooseFields = React.createClass({
    mixins: [
        reactAsync.Mixin, Reflux.ListenerMixin
    ],
    componentDidMount: function() {
        this.listenTo(destinationFieldsStore, this.updateDestinationFields);
        this.listenTo(sourceFieldsStore, this.updateSourceFields);
    },
    getInitialStateAsync: function() {
        appActions.loadDestinationFieldsData();
        destinationFieldsStore.listen(function(data) {
            try {
                return cb(null, {
                    destinationFieldsData: data.destinationFieldsData
                });
            } catch (err) {}
        });
        appActions.loadSourceFieldsData();
        sourceFieldsStore.listen(function(data) {
            try {
                return cb(null, {
                    sourceFieldsData: data.sourceFieldsData
                });
            } catch (err) {}
        });
    },
    updateSourceFields: function(data) {
        this.setState({
            sourceFieldsData: data.sourceFieldsData
        });
    },
    updateDestinationFields: function(data) {
        this.setState({
            destinationFieldsData: data.destinationFieldsData
        });
    },   
    createFieldsDropdown: function(sourceField){
    var destinationFields = $.map(this.state.destinationFieldsData, function(item, i) {
            if(sourceField.field_type == item.field_type){
               return <option key={item.field_name} value={item.field_name} >{item.field_name}</option>
            }              
           });
     return destinationFields
    }, 
    render: function() {
        var rows = [];
        if (this.state.destinationFieldsData && this.state.sourceFieldsData) {                    
           $.map(this.state.sourceFieldsData, function(item, i) {
                rows.push(<tr key={item.field_name}>
                    <td>
                        <input aria-label="Field1" type="checkbox"/>
                    </td>
                    <td>
                        <div className="table_cell">
                            {item.field_name}
                        </div>
                    </td>
                    <td>
                    <select className="form-control">
                      {this.createFieldsDropdown(item)}
                    </select>
                    </td>
                </tr>);
            }.bind(this));
        }

        return (
            <div className="panel panel-default">
                <div className="panel-heading"><strong>Choose and Map Fields</strong></div>
                <div className="panel-body">
                    <ul className="nav nav-pills navbar-right">
                        <li className="dropdown" role="presentation">
                            <a aria-expanded="false" className="dropdown-toggle" data-toggle="dropdown" href="#" role="button">
                                Load Existing Template
                                <span className="caret"></span>
                            </a>
                            <ul className="dropdown-menu" role="menu">
                                <li role="presentation"><a aria-controls="template1" href="#template1">Template 1 IATI 1.05</a></li>
                                <li role="presentation"><a aria-controls="template2" href="#template2">Template 2 IATI 2.01</a></li>
                            </ul>
                        </li>
                    </ul>
                    <table className="table">
                        <thead>
                            <tr>
                                <th>
                                    Import/Update
                                </th>
                                <th>
                                    Source Field
                                </th>
                                <th>
                                    Destination Field
                                </th>
                            </tr>
                        </thead>
                        <tbody>
                            {rows}
                        </tbody>
                    </table>
                </div>
                <div className="buttons">
                    <button className="btn btn-warning navbar-btn btn-custom" type="button">Save</button>&nbsp;
                    <button className="btn btn-success navbar-btn btn-custom" type="button">Next >></button>
                </div>
                </div>
            ); } }); 
            
 module.exports = ChooseFields;