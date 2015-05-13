var React = require('react');
var TYPE_MENU = "menu-item";
var TYPE_DIVIDER= "divider";
var SubMenu = React.createClass({
  render: function() {
    var subMenus = [];
    if(this.props.items){
    $.map(this.props.items, function (item, i) {	    
	        if(item.type === TYPE_MENU){
	          subMenus.push(<li key={item.name}><a href={item.url} >{item.label}</a></li>);
	        }else if(item.type === TYPE_DIVIDER){
	          subMenus.push(<li className="divider" key={item.name}></li>);
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