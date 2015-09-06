var React = require('react');
var Router = require('react-router');
var Link = Router.Link;
var constants = require('./../../utils/constants');

var WizardSteps = React.createClass({
  componentDidMount: function () {
      var src = this.props.src; 
  },
  render: function() {	  
	var indexOfCurrent = constants.STEPS_ORDER.indexOf(this.props.currentStep);
	
    return (    	
        <div className=" col-sm-3 col-md-3">
          <ul className="wizard-steps nav-pills nav-stacked" role="tablist">
            <li role="presentation" className = {(constants.STEPS_ORDER.indexOf(constants.UPLOAD_FILE) > indexOfCurrent && this.props.completedSteps.indexOf(constants.UPLOAD_FILE)) == -1  ? 'disabled not-active' : '' } ><Link to="upload" params={this.props.params} aria-controls="file" ><div className="glyphicon glyphicon-file"></div> {this.props.i18nLib.t('wizard.steps.upload_files')}<span className="sr-only">(current)</span></Link></li>
            <li role="presentation" className = {(constants.STEPS_ORDER.indexOf(constants.FILTER_DATA) > indexOfCurrent && this.props.completedSteps.indexOf(constants.FILTER_DATA)) == -1  ? 'disabled not-active' : '' } ><Link to="filter" params={this.props.params} aria-controls="filter"><div className="glyphicon glyphicon-filter"></div> {this.props.i18nLib.t('wizard.steps.filter_data')}</Link></li>
            <li role="presentation" className = {(constants.STEPS_ORDER.indexOf(constants.CHOOSE_PROJECTS) > indexOfCurrent && this.props.completedSteps.indexOf(constants.CHOOSE_PROJECTS)) == -1  ? 'disabled not-active' : '' }><Link to="projects" params={this.props.params} aria-controls="projects" ><div className="glyphicon glyphicon-list-alt"></div>{this.props.i18nLib.t('wizard.steps.choose_projects')}</Link></li>
            <li role="presentation" className = {(constants.STEPS_ORDER.indexOf(constants.CHOOSE_FIELDS) > indexOfCurrent && this.props.completedSteps.indexOf(constants.CHOOSE_FIELDS)) == -1  ? 'disabled not-active' : '' }><Link to="fields" params={this.props.params} aria-controls="fields" ><div className="glyphicon glyphicon-tasks"></div>{this.props.i18nLib.t('wizard.steps.choose_fields')}</Link></li>
            <li role="presentation" className = {(constants.STEPS_ORDER.indexOf(constants.MAP_VALUES) > indexOfCurrent && this.props.completedSteps.indexOf(constants.MAP_VALUES)) == -1  ? 'disabled not-active' : '' } ><Link to="mapvalues" params={this.props.params} aria-controls="mapvalues" ><div className="glyphicon glyphicon-tasks"></div>{this.props.i18nLib.t('wizard.steps.map_values')}</Link></li>
            <li role="presentation" className = {(constants.STEPS_ORDER.indexOf(constants.REVIEW_IMPORT) > indexOfCurrent && this.props.completedSteps.indexOf(constants.REVIEW_IMPORT)) == -1  ? 'disabled not-active' : '' }><Link to="import" params={this.props.params} aria-controls="import" ><div className="glyphicon glyphicon-save"></div> {this.props.i18nLib.t('wizard.steps.review_import')}</Link></li>
          </ul>
        </div>
    );
  }
});

module.exports = WizardSteps;