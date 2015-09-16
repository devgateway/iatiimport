var React = require('react');
var importProcessStore = require('./../../stores/importProcessStore');
var Reflux = require('reflux');
var NAVBAR_LEFT = 'navbar-left';
var appActions = require('./../../actions');
var SubMenu = require('./sub-menu');
var Router = require('react-router');
var Link = Router.Link;
var ImportProcessMenu = React.createClass({
    mixins: [Reflux.ListenerMixin
    ],
    getInitialState: function() {
		return {importProcessData: []};
	},
    componentDidMount   : function () {  
    	this.listenTo(importProcessStore, this.updateProcessData);
    	this.loadData();
    },
    updateProcessData: function(data){
       this.setState({importProcessData:data});
    },
    loadData: function(){
    	appActions.loadImportProcessData.triggerPromise().then(function(data) {
    		this.updateProcessData(data);
    	}.bind(this));
     },
    render: function () {  
    	var items = [];    	
    	if (this.state.importProcessData) {    		
            $.map(this.state.importProcessData, function(importProcess, i) {            	
            	items.push({name: importProcess.label, url: "#/wizard/" + importProcess.sourceProcessor + "/" + importProcess.destinationProcessor, label: importProcess.label, type: "menu-item"});
            }.bind(this));
            
    	}
    	
        return (
        		<li className="dropdown" role="button" key={this.props.i18nLib.t("header.nav.menu.import_process")}>
        		<Link aria-expanded="true" className="dropdown-toggle"  data-toggle="dropdown"  role="button" to="/">
        		<div className="glyphicon glyphicon-import"></div>{this.props.i18nLib.t("header.nav.menu.import_process")}
        		<span className="caret"></span>                               
        		</Link>
        		  <SubMenu items={items} {...this.props} />
        		</li>
        );
    }
});

module.exports = ImportProcessMenu;