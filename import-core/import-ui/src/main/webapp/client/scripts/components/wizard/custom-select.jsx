var React = require('react');
var CustomSelect = React.createClass({
     getInitialState: function() {
         return {
             value: null
         }
     },
     componentDidMount: function() {    	 
     },
     change: function(event){
         this.setState({value: event.target.value});             
         this.props.handleChange(this.props.data, event.target.value); 
     },
     render: function(){
        var optionNodes = this.props.options.map(function(option){
           return <option key={option[this.props.value]} value={option[this.props.value]}>{option[this.props.label]}</option>;
        }.bind(this));
        return(           
               <select className="form-control" onChange={this.change} value={this.props.initialOption}>
                 <option value="" >Select</option>
                 {optionNodes}
               </select>          
        );
     }
});

module.exports = CustomSelect;