var React = require('react');
var DEFAULT_LANGUAGE_CODE = 'en';
var Tooltip = React.createClass({
  getInitialState: function () {
    return {};
  },
  componentDidMount: function () {
    $(this.refs.tooltip.getDOMNode()).tooltip();
  },
  getClasses: function () {
    var classes=''

    if (!this.props.image) {
      classes = 'glyphicon glyphicon-info-sign ';
    }

    if (this.props.classes) {
      classes += this.props.classes;
    } else {
      classes += "glyphicon-info-sign-custom1";
    }
    return classes;
  },
  getDataPlacement: function () {
    var dataPlacement = "bottom";
    if (this.props.dataPlacement) {
      dataPlacement = this.props.classes;
    }

    return dataPlacement;
  },
  getTooltip: function () {
    var tooltip = this.props.tooltip ? this.props.tooltip : "";
    if (typeof tooltip === 'string' || tooltip instanceof String) {
      return tooltip;
    } else {
      var currentLanguage = this.props.i18nLib.lng();
      return tooltip[currentLanguage] || tooltip[DEFAULT_LANGUAGE_CODE] || "";
    }
  },
  render: function () {
    return (
      <span ref="tooltip" className={this.props.image?'':this.getClasses()} data-toggle="tooltip" data-placement={this.getDataPlacement()}
            title={this.getTooltip()}>
       {this.props.image && <img src={'../../../images/' + this.props.image} className={!this.props.image?'':this.getClasses()}/>}
       </span>);
  }
});
module.exports = Tooltip;
