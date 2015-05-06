var React = require('react');
var WizardSteps = React.createClass({
  componentDidMount: function () {
      // from the path '/wizard/:id'
      var id = this.props.params.id;    
  },
  render: function() {  
    return (
        <div className="col-sm-3 col-md-3">
          <ul className="nav nav-pills nav-stacked" role="tablist">
            <li role="presentation" className="active"><a href="#file" aria-controls="file" role="tab" data-toggle="tab"><div className="glyphicon glyphicon-file"></div> Upload File(s) <span className="sr-only">(current)</span><span className="badge">Done</span></a></li>
            <li role="presentation"><a href="#filter" aria-controls="filter" role="tab" data-toggle="tab"><div className="glyphicon glyphicon-filter"></div> Filter Data <span className="badge">Done</span></a></li>
            <li role="presentation"><a href="#projects" aria-controls="projects" role="tab" data-toggle="tab"><div className="glyphicon glyphicon-list-alt"></div> Choose Projects</a></li>
            <li role="presentation"><a href="#fields" aria-controls="fields" role="tab" data-toggle="tab"><div className="glyphicon glyphicon-tasks"></div> Choose Fields</a></li>
            <li role="presentation"><a href="#mapvalues" aria-controls="mapvalues" role="tab" data-toggle="tab"><div className="glyphicon glyphicon-tasks"></div> Map Values</a></li>
            <li role="presentation"><a href="#review" aria-controls="review" role="tab" data-toggle="tab"><div className="glyphicon glyphicon-save"></div> Review and Import</a></li>
          </ul>
        </div>
    );
  }
});

module.exports = WizardSteps;
