var React = require('react');
var menuStore = require('./../../stores/MenuStore');
var reactAsync = require('react-async');
var Reflux = require('reflux');
var NAVBAR_LEFT = 'navbar-left';
var appActions = require('./../../actions');
var SubMenu = require('./sub-menu');
var Router = require('react-router');
var Link = Router.Link;
var Menu = React.createClass({
    mixins              : [
        reactAsync.Mixin, Reflux.ListenerMixin
    ],
    componentDidMount   : function () {
        this.listenTo(menuStore, this.updateMenu);
    },
    getInitialStateAsync: function () {
        appActions.loadMenuData();
    },
    updateMenu          : function (data) {
        this.setState({
            menuData: data.menuData
        });
    },
    render              : function () {
        var menusLeft = [];
        var menusRight = [];
        if (this.state.menuData) {
            this.state.menuData.forEach(function (item) {
                if (item && item.enabled) {
                    var iconClass = 'glyphicon ';
                    if (item.iconClass) {
                        iconClass += item.iconClass;
                    }
                    var subMenu = (item.children && item.children.length > 0) ? <SubMenu items={item.children}/> : '';
                    var caret = (item.children && item.children.length > 0) ? 'caret' : '';
                    if (item.position == NAVBAR_LEFT) {
                        menusLeft.push(<li className="dropdown" key={item.label}>
                            <Link aria-expanded="true" className="dropdown-toggle" data-toggle="dropdown" role="button" to={item.url}>
                                <div className={iconClass}></div>{item.label}
                                <span className={caret}></span>
                            </Link>
                                {subMenu}
                        </li>);
                    } else {
                        menusRight.push(<li className="dropdown" key={item.label}>
                            <a aria-expanded="true" className="dropdown-toggle" data-toggle="dropdown" href="#" role="button">
                                <div className={iconClass}></div>{item.label}
                                <span className={caret}></span>
                            </a>
                                {subMenu}
                        </li>);
                    }
                }
            });
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