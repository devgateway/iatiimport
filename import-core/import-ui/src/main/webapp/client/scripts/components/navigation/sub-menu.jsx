var React = require('react');
var Cookies = require('js-cookie');
var TYPE_MENU = "menu-item";
var TYPE_DIVIDER = "divider";
var TYPE_MENU_LANGUAGE = "language-selector-item";
var TYPE_MENU_WORKFLOW = "workflow-selector-item";
var SubMenu = React.createClass({
  switchLanguage: function(language){
     this.props.switchLanguage(language);
  },
  selectWorkflow: function(e){
    e.preventDefault();
    $.get('/importer/import/wipeall', function(){});  
    var hash =  e.target.getAttribute('data-hash');    
    window.location.href = window.location.pathname + window.location.search + hash;
    window.location.reload(false);    
  },
  render: function() {
    var subMenus = [];
    if(this.props.items){
    $.map(this.props.items, function (item, i) {	    
	        if(item.type === TYPE_MENU){
	          subMenus.push(<li key={item.name}><a href={item.url} >{this.props.i18nLib.t(item["i18n-key"]) || item.label}</a></li>);
	        }else if(item.type === TYPE_DIVIDER){
	          subMenus.push(<li className="divider" key={item.name}></li>);
	        }else if(item.type === TYPE_MENU_LANGUAGE ){
	          subMenus.push(<li key={item.name} ><a href="#" onClick={this.switchLanguage.bind(null, item.name)}  >{this.props.i18nLib.t(item["i18n-key"])}<span className={Cookies.get('i18next') === item.name ? 'glyphicon glyphicon-ok' : ''}></span></a></li>);
	        }else if(item.type === TYPE_MENU_WORKFLOW){
	           subMenus.push(<li key={item.name}><a href="#" data-hash={item.url} onClick={this.selectWorkflow}  >{this.props.i18nLib.t(item["i18n-key"]) || item.label}</a></li>);
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