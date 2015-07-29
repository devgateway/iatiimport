var React = require('react');
var Cookies = require('js-cookie');
var TYPE_MENU = "menu-item";
var TYPE_DIVIDER = "divider";
var TYPE_MENU_LANGUAGE = "language-selector-item";
var SubMenu = React.createClass({
  switchLanguage: function(language){
     this.props.switchLanguage(language);
  },
  render: function() {
    var subMenus = [];
    if(this.props.items){
    $.map(this.props.items, function (item, i) {	    
	        if(item.type === TYPE_MENU){
	          subMenus.push(<li key={item.name}><a href={item.url} >{this.props.i18nLib.t(item["i18n-key"])}</a></li>);
	        }else if(item.type === TYPE_DIVIDER){
	          subMenus.push(<li className="divider" key={item.name}></li>);
	        }else if(item.type === TYPE_MENU_LANGUAGE ){
	          subMenus.push(<li key={item.name} ><a href="#" onClick={this.switchLanguage.bind(null, item.name)}  >{this.props.i18nLib.t(item["i18n-key"])}<span className={Cookies.get('i18next') === item.name ? 'glyphicon glyphicon-ok' : ''}></span></a></li>);
	        }		     
	    }.bind(this));
    }    
    return (
      <ul className="dropdown-menu" role="menu">                
             {subMenus}
       </ul>
    );
  }
});

module.exports = SubMenu;