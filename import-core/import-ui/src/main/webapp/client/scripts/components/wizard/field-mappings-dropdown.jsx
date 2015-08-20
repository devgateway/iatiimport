var React = require('react');

var Reflux = require('reflux');
var reactAsync = require('react-async');
var appActions = require('./../../actions');
var FieldMappingsDropdown = React.createClass({
    mixins: [Reflux.ListenerMixin],
    getInitialState: function() {
       return {};
    },  
    componentDidMount: function() {        
    },    
    loadTemplate: function(e){
        this.props.loadMappingTemplate(e.target.getAttribute('data-id')); 
    },    
    render: function () {
    var templates = [];    
    if(this.props.mappingTemplatesData.length > 0){
     templates = this.props.mappingTemplatesData.map(function(item, index){
        return <li role="presentation" className="template-dropdown-item" data-id = {item.id} onClick= {this.loadTemplate}>{item.name}</li>  
     }.bind(this));
    }
      
    return (
          <ul className="nav nav-pills navbar-right">
                        <li className="dropdown" role="presentation">
                            <a aria-expanded="false" className="dropdown-toggle" data-toggle="dropdown" href="#" role="button" >
                                {this.props.i18nLib.t('wizard.map_fields.load_existing_template')}
                                <span className="caret"></span>
                            </a>
                            <ul className="dropdown-menu" role="menu">
                                {templates}
                            </ul>
                        </li>
                    </ul>
        );
    }
});

module.exports = FieldMappingsDropdown;