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
            <li role="presentation"  className = {this.props.currentStep === constants.UPLOAD_FILE  ?  '' : 'disabled not-active'}><Link to="upload" params={this.props.params} aria-controls="file" ><div className="glyphicon glyphicon-file"></div> {this.props.i18nLib.t('wizard.steps.upload_files')}<span className="sr-only">(current)</span></Link></li>
            <li role="presentation" className = {this.props.currentStep === constants.FILTER_DATA  ?  '' : 'disabled not-active'} ><Link to="filter" params={this.props.params} aria-controls="filter"><div className="glyphicon glyphicon-filter"></div> {this.props.i18nLib.t('wizard.steps.filter_data')}</Link></li>
            <li role="presentation" className = {this.props.currentStep === constants.CHOOSE_PROJECTS  ?  '' : 'disabled not-active'}><Link to="projects" params={this.props.params} aria-controls="projects" ><div className="glyphicon glyphicon-list-alt"></div>{this.props.i18nLib.t('wizard.steps.choose_projects')}</Link></li>
            <li role="presentation" className = {this.props.currentStep === constants.CHOOSE_FIELDS  ?  '' : 'disabled not-active'}><Link to="fields" params={this.props.params} aria-controls="fields" ><div className="glyphicon glyphicon-tasks"></div>{this.props.i18nLib.t('wizard.steps.choose_fields')}</Link></li>
            <li role="presentation" className = {this.props.currentStep === constants.MAP_VALUES  ?  '' : 'disabled not-active'}><Link to="mapvalues" params={this.props.params} aria-controls="mapvalues" ><div className="glyphicon glyphicon-tasks"></div>{this.props.i18nLib.t('wizard.steps.map_values')}</Link></li>
            <li role="presentation" className = {this.props.currentStep === constants.REVIEW_IMPORT  ?  '' : 'disabled not-active'}><Link to="import" params={this.props.params} aria-controls="import" ><div className="glyphicon glyphicon-save"></div> {this.props.i18nLib.t('wizard.steps.review_import')}</Link></li>
          </ul>
        </div>
    );
  }
});

module.exports = WizardSteps;