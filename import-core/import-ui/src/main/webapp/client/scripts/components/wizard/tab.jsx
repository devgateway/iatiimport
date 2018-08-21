var React = require('react');
var Tooltip = require('./tooltip');

var Tab = React.createClass({
    propTypes: {
        isActive: React.PropTypes.bool.isRequired,
        onClick: React.PropTypes.func.isRequired
    }, 
    render: function() {
        var className = this.props.isActive ? 'active' : '';
        return (<li className={className} onClick={this.props.onClick}>
                    <a href="#"><Tooltip i18nLib={this.props.i18nLib} tooltip={this.props.tooltip} classes="glyphicon-info-sign-custom2"/>{this.props.name}</a>
                </li>);
    }
});

module.exports = Tab;