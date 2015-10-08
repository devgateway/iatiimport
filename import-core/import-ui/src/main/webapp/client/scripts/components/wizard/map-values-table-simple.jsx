var React = require('react');

var CustomSelect = require('./custom-select');
var _ = require('lodash/dist/lodash.underscore');

var MappingTableSimple = React.createClass({
  componentDidMount: function() {

  },
  getInitialStateAsync: function() {    

  },
  render: function() { 
       var rows = [];
       var options = [];
       var sourceField = this.props.mapping.sourceField;
       var destinationField = this.props.mapping.destinationField;
       var valuesIndex = this.props.mapping.valueIndexMapping;

       _.map(destinationField.possibleValues, function(destinationValue, i) { 
           options.push({value:destinationValue.code, label:destinationValue.value});      
       });
       $.each(valuesIndex, function(key, value) {  
           var sourceValue = _.find(sourceField.possibleValues, function(v){ return v.index == key;});
           var destinationValue = _.find(destinationField.possibleValues, function(v){ return v.index == value;});
           var destValue = destinationValue ? destinationValue.code : "";

           rows.push(<tr key = {key}>
                        <td>
                            <div className="table_cell">
                                {sourceValue.value}
                            </div>
                        </td>
                        <td>
                         <CustomSelect initialOption={destValue} options={options} value="value" label="label" data={{sourceFieldName:sourceField.uniqueFieldName, sourceIndexValue:key}} handleChange = {this.props.handleUpdates}/>
                        </td>
                    </tr>);                                         
          }.bind(this));

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

module.exports = MappingTableSimple;
