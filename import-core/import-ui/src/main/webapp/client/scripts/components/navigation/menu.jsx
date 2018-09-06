var React = require('react');
var menuStore = require('./../../stores/MenuStore');
var reactAsync = require('react-async');
var Reflux = require('reflux');
var NAVBAR_LEFT = 'navbar-left';
var appActions = require('./../../actions');
var SubMenu = require('./sub-menu');
var ImportProcessMenu = require('./workflow-menu');
var Router = require('react-router');
var Link = Router.Link;
var Cookies = require('js-cookie');
var appActions = require('./../../actions');
var common = require('./../../utils/common');
var Menu = React.createClass({
    mixins              : [
        Reflux.ListenerMixin
    ],
    getInitialState: function() {
        return {};
    },
    componentDidMount   : function () {
        this.listenTo(menuStore, this.updateMenu);
        this.loadData();
        
    },
    loadData: function () {
        appActions.loadMenuData();       
    },
    updateMenu: function (data) {
        this.setState({
            menuData: data.menuData
        });
    },
  
    showMenu: function(item) {        
        if (item.name === 'close') {
            return true;
        }
        
        var isAdmin = common.isAdmin();
        var showMenuItem = true;
        if (item.name === 'admin'){
            showMenuItem = isAdmin;
        } else {            
            showMenuItem = isAdmin ? false : true;         
        }    
        
        return showMenuItem;
    },
    render: function () {      
        var menusLeft = [];
        var menusRight = [];
        var self = this;
        if (this.state.menuData) {
            this.state.menuData.forEach(function (item) {                
                if (item && item.enabled && this.showMenu(item)) {
                    var iconClass = 'glyphicon ';
                    if (item.iconClass) {
                        iconClass += item.iconClass;
                    }
                    var subMenu = (item.children && item.children.length > 0) ? <SubMenu items={item.children} {...self.props} /> : '';
                    var caret = (item.children && item.children.length > 0) ? 'caret' : '';
                    if (item.position == NAVBAR_LEFT) {
                    	if(item.children){
                    		menusLeft.push(<li className="dropdown" key={item.label}>
                    		<Link aria-expanded="true" className="dropdown-toggle" data-toggle="dropdown" role="button" to={item.url}>
                    		<div className={iconClass}></div>{self.props.i18nLib.t(item["i18n-key"])}
                    		<span className={caret}></span>                               
                    		</Link>
                    		{subMenu}
                    		</li>);
                    	}else{
                    		menusLeft.push(<li className="dropdown" key={item.label}>
                    		<Link aria-expanded="true" className="dropdown-toggle"  role="button" to={item.url}>
                    		<div className={iconClass}></div>{self.props.i18nLib.t(item["i18n-key"])}
                    		<span className={caret}></span>                               
                    		</Link>
                    		{subMenu}
                    		</li>);
                    	} 
                    	
                    } else {
                        menusRight.push(<li className="dropdown" key={item.label}>
                            <a aria-expanded="true" className="dropdown-toggle" data-toggle="dropdown" href="#" role="button" onClick={self.props.closeImportTool}>
                                <div className={iconClass}></div>{self.props.i18nLib.t(item["i18n-key"])}
                                <span className={caret}></span>
                            </a>
                                {subMenu}
                        </li>);
                    }
                }
            }.bind(this));
        }
        if (!common.isAdmin()) {
            menusLeft.splice(1, 0, <ImportProcessMenu {...self.props} />);
        }
        
        return (
            <div className="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
                <ul className='nav navbar-nav'>
                    {menusLeft}
                </ul>
                <ul className='nav navbar-nav navbar-right'>
                    {menusRight}
                </ul>
            </div>
        );
    }
});

module.exports = Menu;