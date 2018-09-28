var React = require('react');
var common = require('./../../../utils/common');
var constants = require('./../../../utils/constants');
var Header = React.createClass({
    render: function () {    
        return (<div className="preview_header"> 
                <span className="preview_title">               
                      {common.getMultilangString(this.props.project.multilangFields, constants.FIELD_NAMES.TITLE, this.props.i18nLib)}
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