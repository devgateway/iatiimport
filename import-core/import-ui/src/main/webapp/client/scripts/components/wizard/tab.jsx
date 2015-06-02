var React = require('react');
var Tab = React.createClass({
    propTypes: {
        isActive: React.PropTypes.bool.isRequired,
        onClick: React.PropTypes.func.isRequired
    }, 
    render: function() {
        var className = this.props.isActive ? 'active' : '';
        return (<li className={className} onClick={this.props.onClick}>
                    <a href="#">{this.props.name}</a>
                </li>);
    }
});

module.exports = Tab;