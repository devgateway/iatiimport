var React = require('react');
var common = require('./../../../utils/common');
var constants = require('./../../../utils/constants');
var _ = require('lodash/dist/lodash.underscore');
var PolicyMarkers = React.createClass({      
    render: function () {         
       var policyMarkerCodes = (this.props.project.stringMultiFields && this.props.project.stringMultiFields['policy-marker']) ? this.props.project.stringMultiFields['policy-marker'] : [];
       
       return (<div className="section_group_class" >               
             <div className="section_title_class"><span>{this.props.i18nLib.t('project_preview.policy_markers')}</span><span></span></div>               
               <table className="box_table">
               <tbody>
              
               {policyMarkerCodes.sort().map(function(code, index){
                   return (<tr key={index}>
                   <td className="box_field_value " >
                    {code + " - " + common.getValueName(this.props.sourceFieldsData,constants.FIELD_NAMES.POLICY_MARKER, code)}
                    </td>                   
                   </tr>)
                 }.bind(this))                   
               }
               </tbody>
              </table>                
        </div>);
    }
});

module.exports = PolicyMarkers;