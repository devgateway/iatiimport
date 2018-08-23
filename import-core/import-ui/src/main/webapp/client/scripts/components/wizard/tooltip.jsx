var React = require('react');
var DEFAULT_LANGUAGE_CODE = 'en';
var Tooltip = React.createClass({   
    getInitialState: function() {
       return {};
    },
    componentDidMount: function() {   
        $(this.refs.tooltip.getDOMNode()).tooltip();
    }, 
    getClasses: function(){
        var classes = "glyphicon glyphicon-info-sign ";
        if (this.props.classes) {
            classes += this.props.classes;
        } else {
            classes += "glyphicon-info-sign-custom1";
        }
        
        return classes;
    },
    getTooltip: function() {              
        var tooltip = this.props.tooltip ? this.props.tooltip : "";        
        if (typeof tooltip === 'string' || tooltip instanceof String) {
             return tooltip;
        } else {
             var currentLanguage = this.props.i18nLib.lng();
             return tooltip[currentLanguage] || tooltip[DEFAULT_LANGUAGE_CODE] || "";  
        }       
    },
    render: function() {           
       return (<span ref="tooltip" className={this.getClasses()} data-toggle="tooltip" data-placement="bottom" title={this.getTooltip()}> </span>);
    } 
}); 
module.exports = Tooltip;
