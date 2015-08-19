var React = require('react');
var fieldMappingTemplateStore = require('./../../stores/FieldMappingTemplateStore');
var Reflux = require('reflux');
var reactAsync = require('react-async');
var appActions = require('./../../actions');
var FieldMappingsDropdown = React.createClass({
    mixins: [Reflux.ListenerMixin],
    getInitialState: function() {
       return {mappingTemplatesData:[]};
    },  
    componentDidMount: function() {     
        this.listenTo(fieldMappingTemplateStore, this.updateMappingTemplatesData);
        this.loadData();
    },
    loadData: function(){        
      appActions.loadFieldMappingsTemplateList.triggerPromise().then(function(data) {                              
        this.updateMappingTemplatesData(data); 
      }.bind(this)).catch(function(err) { 
        console.log(err);      
        console.log('Error loading mapping templates')
      }.bind(this)); 
    },
    updateMappingTemplatesData: function(data) {
        this.setState({
            mappingTemplatesData: data
        });
    }, 
    render: function () {
    var templates = [];    
    if(this.state.mappingTemplatesData.length > 0){
     templates = this.state.mappingTemplatesData.map(function(item, index){
        return <li role="presentation"><a aria-controls="template1" href="#template1">{item.name}</a></li>  
     }.bind(this));
    }
      
    return (
          <ul className="nav nav-pills navbar-right">
                        <li className="dropdown" role="presentation">
                            <a aria-expanded="false" className="dropdown-toggle" data-toggle="dropdown" href="#" role="button">
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