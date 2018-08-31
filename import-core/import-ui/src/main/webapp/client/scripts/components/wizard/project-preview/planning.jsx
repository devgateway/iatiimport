var React = require('react');
var common = require('./../../../utils/common');
var constants = require('./../../../utils/constants');
var _ = require('lodash/dist/lodash.underscore');

var Planning = React.createClass({   
     render: function () {   
       var actualEndDate = this.props.project.dateFields[constants.FIELD_NAMES.ACTUAL_END_DATE];
       var plannedEndDate = this.props.project.dateFields[constants.FIELD_NAMES.PLANNED_END_DATE];
       var actualStartDate = this.props.project.dateFields[constants.FIELD_NAMES.ACTUAL_START_DATE];
       var plannedStartDate = this.props.project.dateFields[constants.FIELD_NAMES.PLANNED_START_DATE];
         
       return (<div className="section_group_class" >               
                 <div className="section_title_class"><span>{this.props.i18nLib.t('project_preview.planning')}</span><span></span></div>               
                 <table className="box_table">
                  <tbody>
                  <tr>
                   <td className="box_field_value ">
                     <div className="box_field_name block">{common.getFieldDisplayName(this.props.sourceFieldsData, constants.FIELD_NAMES.PLANNED_START_DATE)}</div>
                     <div className="box_field_value block">{plannedStartDate ? common.formatDate(plannedStartDate) : this.props.i18nLib.t('project_preview.no_data')}</div>
                    </td>
                    <td className="box_field_value ">
                        <div className="box_field_name block">{common.getFieldDisplayName(this.props.sourceFieldsData, constants.FIELD_NAMES.ACTUAL_START_DATE)}</div>
                        <div className="box_field_value block">{actualStartDate ? common.formatDate(actualStartDate) : this.props.i18nLib.t('project_preview.no_data')}</div>
                    </td>                     
                   </tr>
                    <tr>
                        <td className="box_field_value ">
                          <div className="box_field_name block">{common.getFieldDisplayName(this.props.sourceFieldsData, constants.FIELD_NAMES.PLANNED_END_DATE)}</div>
                          <div className="box_field_value block">{plannedEndDate ? common.formatDate(plannedEndDate) : this.props.i18nLib.t('project_preview.no_data')}</div>
                         </td>
                         <td className="box_field_value ">
                             <div className="box_field_name block">{common.getFieldDisplayName(this.props.sourceFieldsData, constants.FIELD_NAMES.ACTUAL_END_DATE)}</div>
                             <div className="box_field_value block">{actualEndDate ? common.formatDate(actualEndDate) : this.props.i18nLib.t('project_preview.no_data')}</div>
                         </td>                     
                      </tr>
                  </tbody>
                 </table>                
           </div>);
    }
});

module.exports = Planning;