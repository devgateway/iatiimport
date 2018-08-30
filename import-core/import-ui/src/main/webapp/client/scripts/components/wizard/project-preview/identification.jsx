var React = require('react');
var common = require('./../../../utils/common');
var constants = require('./../../../utils/constants');
var Identification = React.createClass({
    render: function () {    
        return (<div class="section_group_class" id="AcIdentification">
                <div className="section_title_class"><span>{this.props.i18nLib.t('project_preview.identification')}</span><span></span></div>
                
                <div className="block">
                <div className="section_field_name block">{common.getFieldDisplayName(this.props.sourceFieldsData, constants.FIELD_NAMES.DESCRIPTION)}</div>
                <div className="section_field_value block">
                <p>{common.getMultilangString(this.props.project.multilangFields, constants.FIELD_NAMES.DESCRIPTION, this.props.i18nLib)}</p>
                </div>
                </div>
                
               </div>);
    }
});

module.exports = Identification;