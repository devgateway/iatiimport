var React = require('react');

var Reflux = require('reflux');
var reactAsync = require('react-async');
var appActions = require('./../../actions');
var MappingsDropdown = React.createClass({
	mixins: [Reflux.ListenerMixin],
	getInitialState: function() {
		return {};
	},  
	componentDidMount: function() {        
	},    
	loadTemplate: function(e){
		this.props.loadMappingTemplate(e.target.getAttribute('data-id')); 
	}, 
	deleteTemplate: function(e){
		if(confirm(this.props.i18nLib.t('wizard.mappings_dropdown.confirm_delete', { templateName: e.target.getAttribute('data-name')}))){
			this.props.deleteMappingTemplate(e.target.getAttribute('data-id'));   		
		}        
	},   
	render: function () {
		var templates = [];    
		if(this.props.mappingTemplatesData.length > 0){
			templates = this.props.mappingTemplatesData.map(function(item, index){
				return (<li role="presentation" className="template-dropdown-item" ><div className="row">
		          <div className="wordwrap col-md-8 template-dropdown-item-link" title = {item.name} data-id = {item.id}  onClick= {this.loadTemplate}>{item.name}</div>
		          <div className="wordwrap col-md-4 template-dropdown-item-delete glyphicon glyphicon-remove" data-id = {item.id} data-name = {item.name} onClick= {this.deleteTemplate}></div>
		          </div>
				</li> );
			}.bind(this));
		}
		if(this.props.mappingTemplatesData.length > 0){
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
		return (<span></span>);
	}
});

module.exports = MappingsDropdown;