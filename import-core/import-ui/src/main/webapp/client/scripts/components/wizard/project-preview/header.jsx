var React = require('react');
var Tooltip = require('./../tooltip');
var common = require('./../../../utils/common');
var constants = require('./../../../utils/constants');

var Header = React.createClass({
    render: function () {
        let documentTitle = common.getTitle(this.props.project, this.props.i18nLib.lng());
        let translatedDocumentTitle = null;
        if (common.shouldTranslate(this.props.project, constants.FIELD_NAMES.DESCRIPTION, this.props.i18nLib.lng())) {
          translatedDocumentTitle = common.getTranslation(this.props.project, documentTitle, this.props.i18nLib.lng());
        }
        debugger;

        return (<div className="preview_header">
                <span className="preview_title">
                      {documentTitle}
                      {translatedDocumentTitle && translatedDocumentTitle.length > 0 && <Tooltip i18nLib={this.props.i18nLib} tooltip={translatedDocumentTitle} classes="glyphicon-info-sign-translation"/>}
                </span>
                    <div className="preview_status_container">
                       <div className="inline">
                        <div className="preview_status_title inline">{common.getFieldDisplayName(this.props.sourceFieldsData, constants.FIELD_NAMES.IATI_ID)}</div>
                        <div className="preview_status_detail inline">{this.props.project.identifier}</div>

                        <div className="preview_status_title inline">{common.getFieldDisplayName(this.props.sourceFieldsData, constants.FIELD_NAMES.ACTIVITY_STATUS)}</div>
                        <div className="preview_status_detail inline">{common.getValueName(this.props.sourceFieldsData, constants.FIELD_NAMES.ACTIVITY_STATUS, this.props.project.stringFields[constants.FIELD_NAMES.ACTIVITY_STATUS])}</div>
                       </div>
                     </div>
                    </div>);
    }
});

module.exports = Header;
