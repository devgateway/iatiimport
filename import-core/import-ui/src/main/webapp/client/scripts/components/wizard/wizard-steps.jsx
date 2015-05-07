var React = require('react');
var Router = require('react-router');
var Link = Router.Link;
var WizardSteps = React.createClass({
  componentDidMount: function () {
      var id = this.props.id; 
  },
  render: function() {  
    return (
        <div className=" col-sm-3 col-md-3">
          <ul className="wizard-steps nav-pills nav-stacked" role="tablist">
            <li role="presentation" ><Link to="upload" params={{id:this.props.params.id}} aria-controls="file" ><div className="glyphicon glyphicon-file"></div> Upload File(s)<span className="sr-only">(current)</span><span className="badge">Done</span></Link></li>
            <li role="presentation"><Link to="filter" params={{id:this.props.params.id}} aria-controls="filter"><div className="glyphicon glyphicon-filter"></div> Filter Data <span className="badge">Done</span></Link></li>
            <li role="presentation"><Link to="projects" params={{id:this.props.params.id}} aria-controls="projects" ><div className="glyphicon glyphicon-list-alt"></div> Choose Projects</Link></li>
            <li role="presentation"><Link to="fields" params={{id:this.props.params.id}} aria-controls="fields" ><div className="glyphicon glyphicon-tasks"></div> Choose Fields</Link></li>
            <li role="presentation"><Link to="mapvalues" params={{id:this.props.params.id}} aria-controls="mapvalues" ><div className="glyphicon glyphicon-tasks"></div> Map Values</Link></li>
            <li role="presentation"><Link to="import" params={{id:this.props.params.id}} aria-controls="import" ><div className="glyphicon glyphicon-save"></div> Review and Import</Link></li>
          </ul>
        </div>
    );
  }
});

module.exports = WizardSteps;
