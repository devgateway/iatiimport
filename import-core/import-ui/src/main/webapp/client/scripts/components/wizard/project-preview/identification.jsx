var React = require('react');
var Tooltip = require('./../tooltip');
var common = require('./../../../utils/common');
var constants = require('./../../../utils/constants');


var FieldView = React.createClass({
  render: function(){
      var data = common.getValueName(this.props.sourceFieldsData, this.props.fieldName, this.props.project.stringFields[this.props.fieldName])
      return(
              <div className="block">
              <div className="section_field_name block">{common.getFieldDisplayName(this.props.sourceFieldsData, this.props.fieldName)}</div>
              <div className="section_field_value block">
              <p>
                 {data ? data : this.props.i18nLib.t('project_preview.no_data')}
               </p>
              </div>
              </div>
            );
     }
});

var Identification = React.createClass({
    render: function () {
        var sourceDocumentDescription = common.getMultilangString(this.props.project.multilangFields, constants.FIELD_NAMES.DESCRIPTION, this.props.i18nLib);
        var sourceTranslatedDocumentDescription = null;
        if (common.shouldTranslate(this.props.project, constants.FIELD_NAMES.DESCRIPTION, this.props.i18nLib.lng())) {
          sourceTranslatedDocumentDescription = common.getTranslation(this.props.project, sourceDocumentDescription, this.props.i18nLib.lng());
        }

        return (<div class="section_group_class" id="AcIdentification">
                <div className="section_title_class"><span>{this.props.i18nLib.t('project_preview.identification')}</span><span></span></div>
                <div className="block">
                <div className="section_field_name block">{common.getFieldDisplayName(this.props.sourceFieldsData, constants.FIELD_NAMES.DESCRIPTION)}</div>
                <div className="section_field_value block">
                <p className="field_description">
                  {sourceDocumentDescription}
                  {sourceTranslatedDocumentDescription && sourceTranslatedDocumentDescription.length > 0
                  && <Tooltip i18nLib={this.props.i18nLib} tooltip={sourceTranslatedDocumentDescription} classes="glyphicon-info-sign-translation" />}
                </p>
                </div>
                </div>

                <FieldView {...this.props} fieldName={constants.FIELD_NAMES.DEFAULT_AID_TYPE}/>
                <FieldView {...this.props} fieldName={constants.FIELD_NAMES.ACTIVITY_SCOPE}/>
                <FieldView {...this.props} fieldName={constants.FIELD_NAMES.DEFAULT_FINANCE_TYPE}/>
                <FieldView {...this.props} fieldName={constants.FIELD_NAMES.DEFAULT_FLOW_TYPE}/>
                <FieldView {...this.props} fieldName={constants.FIELD_NAMES.DEFAULT_TIED_STATUS}/>
               </div>);
    }
});

module.exports = Identification;
