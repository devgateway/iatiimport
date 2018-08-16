var React = require('react');
var Router = require('react-router');
var Link = Router.Link;
var constants = require('./../../utils/constants');

var WizardSteps = React.createClass({
  componentDidMount: function () {
      var src = this.props.src; 
  },
  render: function() {	  
   var stepsOrder = this.props.params.src == constants.IMPORT_TYPE_AUTOMATIC ? constants.AUTOMATIC_STEPS_ORDER : constants.MANUAL_STEPS_ORDER;
	var indexOfCurrent = stepsOrder.indexOf(this.props.currentStep);
	return (    	
        <div className=" col-sm-2 col-md-2">
          <ul className="wizard-steps nav-pills nav-stacked" role="tablist">
	        {this.props.params.src == constants.IMPORT_TYPE_AUTOMATIC &&
	            <li role="presentation"  className = {stepsOrder.indexOf(constants.SELECT_DATASOURCE) <= indexOfCurrent ?  '' : 'disabled not-active'}><Link to="selectdatasource" params={this.props.params} aria-controls="file" ><div className="glyphicon glyphicon-file"></div>{this.props.i18nLib.t('wizard.steps.select_data_source')}<span className="sr-only">(current)</span></Link></li>
	        }
	        {this.props.params.src == constants.IMPORT_TYPE_AUTOMATIC &&
	            <li role="presentation"  className = {stepsOrder.indexOf(constants.SELECT_VERSION) <= indexOfCurrent ?  '' : 'disabled not-active'}><Link to="selectversion" params={this.props.params} aria-controls="file" ><div className="glyphicon glyphicon-option-vertical"></div>{this.props.i18nLib.t('wizard.steps.select_version')}<span className="sr-only">(current)</span></Link></li>
	        }
	        {this.props.params.src != constants.IMPORT_TYPE_AUTOMATIC &&
             <li role="presentation"  className = {stepsOrder.indexOf(constants.UPLOAD_FILE) <= indexOfCurrent ?  '' : 'disabled not-active'}><Link to="upload" params={this.props.params} aria-controls="file" ><div className="glyphicon glyphicon-file"></div> {this.props.i18nLib.t('wizard.steps.upload_files')}<span className="sr-only">(current)</span></Link></li>
	        }
            <li role="presentation" className = {stepsOrder.indexOf(constants.FILTER_DATA) <= indexOfCurrent  ?  '' : 'disabled not-active'} ><Link to="filter" params={this.props.params} aria-controls="filter"><div className="glyphicon glyphicon-filter"></div> {this.props.i18nLib.t('wizard.steps.filter_data')}</Link></li>
            <li role="presentation" className = {stepsOrder.indexOf(constants.CHOOSE_PROJECTS) <= indexOfCurrent   ?  '' : 'disabled not-active'}><Link to="projects" params={this.props.params} aria-controls="projects" ><div className="glyphicon glyphicon-list-alt"></div>{this.props.i18nLib.t('wizard.steps.choose_projects')}</Link></li>
            <li role="presentation" className = {stepsOrder.indexOf(constants.CHOOSE_FIELDS) <= indexOfCurrent   ?  '' : 'disabled not-active'}><Link to="fields" params={this.props.params} aria-controls="fields" ><div className="glyphicon glyphicon-tasks"></div>{this.props.i18nLib.t('wizard.steps.choose_fields')}</Link></li>
            <li role="presentation" className = {stepsOrder.indexOf(constants.MAP_VALUES) <= indexOfCurrent   ?  '' : 'disabled not-active'}><Link to="mapvalues" params={this.props.params} aria-controls="mapvalues" ><div className="glyphicon glyphicon-tasks"></div>{this.props.i18nLib.t('wizard.steps.map_values')}</Link></li>
            <li role="presentation" className = {stepsOrder.indexOf(constants.REVIEW_IMPORT) <= indexOfCurrent  ?  '' : 'disabled not-active'}><Link to="import" params={this.props.params} aria-controls="import" ><div className="glyphicon glyphicon-save"></div> {this.props.i18nLib.t('wizard.steps.review_import')}</Link></li>
          </ul>
        </div>
    );
  }
});

module.exports = WizardSteps;