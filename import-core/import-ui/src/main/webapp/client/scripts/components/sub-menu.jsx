var React = require('react');
var TYPE_MENU = "menu-item";
var TYPE_DIVIDER= "divider";
var SubMenu = React.createClass({
  render: function() {
    var subMenus = [];
    if(this.props.items){
	     this.props.items.forEach(function(item){
	        if(item.type === TYPE_MENU){
	          subMenus.push(<li key={item.name}><a href="wizard.html" >{item.label}</a></li>);
	        }else if(item.type === TYPE_DIVIDER){
	          subMenus.push(<li className="divider" key={item.name}></li>);
	        }
		     
	    });
    }    
    return (
      <ul className="dropdown-menu" role="menu">                
             {subMenus}
       </ul>
    );
  }
});

module.exports = SubMenu;