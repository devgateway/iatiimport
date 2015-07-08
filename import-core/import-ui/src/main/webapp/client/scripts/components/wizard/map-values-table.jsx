var React = require('react');
var reactAsync = require('react-async');
var Reflux = require('reflux');
var appActions = require('./../../actions');
var destinationValuesStore = require('./../../stores/DestinationValuesStore');
var sourceValuesStore = require('./../../stores/SourceValuesStore');
var CustomSelect = require('./custom-select');
var MappingTable = React.createClass({
  mixins: [
        reactAsync.Mixin, Reflux.ListenerMixin
    ],
  componentDidMount: function() {
        this.listenTo(destinationValuesStore, this.updateDestinationValues);
        this.listenTo(sourceValuesStore, this.updateSourceValues);
    },
    getInitialStateAsync: function() {         
        appActions.loadDestinationValuesData(this.props.destinationFieldName);
        destinationValuesStore.listen(function(data) {
            try {
                return cb(null, {
                    destinationValuesData: data.destinationValuesData
                });
            } catch (err) {}
        });        
        appActions.loadSourceValuesData(this.props.sourceFieldName);
        sourceValuesStore.listen(function(data) {
            try {
                return cb(null, {
                    sourceValuesData: data.sourceValuesData
                });
            } catch (err) {}
        });
    },
    
    updateSourceValues: function(data) {    
        this.setState({
            sourceValuesData: data.sourceValuesData
        });
    },
    updateDestinationValues: function(data) {
        this.setState({
            destinationValuesData: data.destinationValuesData
        });
  },
  render: function() { 
       var rows = []; 
       var options = [];       
       if(this.state.destinationValuesData){
	       $.map(this.state.destinationValuesData, function(destinationValue, i) { 
	           options.push({value:destinationValue.value, label:destinationValue.value});      
	       });
       }       
       if(this.state.sourceValuesData){
	        $.map(this.state.sourceValuesData, function(sourceValue, i) {  
	             rows.push(<tr key = {i}>
				                    <td>
				                        <div className="table_cell">
				                            {sourceValue.value}
				                        </div>
				                    </td>
				                    <td>
				                     <CustomSelect options={options} value="value" label="label" data={{sourceValue:sourceValue.value, sourceFieldName: this.props.sourceFieldName, destinationFieldName:this.props.destinationFieldName}} handleChange = {this.props.updateValueMappings}/>
				                    </td>
				                </tr>);                                         
	            }.bind(this));
          }        
        return (                                              
			<div className="panel panel-default">			
			    <div className="panel-body">
			        <table className="table">
			            <thead>
			                <tr>
			                    <th>
			                        {this.props.i18nLib.t('wizard.map_values.source_value')}
			                    </th>
			                    <th>
			                        {this.props.i18nLib.t('wizard.map_values.destination_value')}			                        
			                    </th>
			                </tr>
			            </thead>
			            <tbody>
			              {rows}
			            </tbody>
			        </table>
			    </div>
			</div>              
  
    );
  }
});

module.exports = MappingTable;
