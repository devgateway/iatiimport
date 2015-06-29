var React = require('react');
var Router = require('react-router');
var Link = Router.Link;
var WizardSteps = React.createClass({
  componentDidMount: function () {
      var src = this.props.src; 
  },
  render: function() {  
    return (
        <div className=" col-sm-3 col-md-3">
          <ul className="wizard-steps nav-pills nav-stacked" role="tablist">
            <li role="presentation"><Link to="upload" params={this.props.params} aria-controls="file" ><div className="glyphicon glyphicon-file"></div> Upload File(s)<span className="sr-only">(current)</span></Link></li>
            <li role="presentation"><Link to="filter" params={this.props.params} aria-controls="filter"><div className="glyphicon glyphicon-filter"></div> Filter Data </Link></li>
            <li role="presentation"><Link to="projects" params={this.props.params} aria-controls="projects" ><div className="glyphicon glyphicon-list-alt"></div> Choose Projects</Link></li>
            <li role="presentation"><Link to="fields" params={this.props.params} aria-controls="fields" ><div className="glyphicon glyphicon-tasks"></div> Choose Fields</Link></li>
            <li role="presentation"><Link to="mapvalues" params={this.props.params} aria-controls="mapvalues" ><div className="glyphicon glyphicon-tasks"></div> Map Values</Link></li>
            <li role="presentation"><Link to="import" params={this.props.params} aria-controls="import" ><div className="glyphicon glyphicon-save"></div> Review and Import</Link></li>
          </ul>
        </div>
    );
  }
});

module.exports = WizardSteps;
