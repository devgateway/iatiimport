var React = require('react');
var common = require('./../../../utils/common');
var constants = require('./../../../utils/constants');
var _ = require('lodash/dist/lodash.underscore');
var Sectors = React.createClass({      
    render: function () {         
       var sectorCodes = (this.props.project.stringMultiFields && this.props.project.stringMultiFields.sector) ? this.props.project.stringMultiFields.sector : [];       
       return (<div className="section_group_class" >               
             <div className="section_title_class"><span>{this.props.i18nLib.t('project_preview.sectors')}</span><span></span></div>               
               <table className="box_table">
               <tbody>
              
               {sectorCodes.map(function(code){
                   return (<tr>
                   <td className="box_field_value ">
                    {code + " - " + common.getValueName(this.props.sourceFieldsData,constants.FIELD_NAMES.SECTOR, code)}
                    </td>                   
                   </tr>)
                 }.bind(this))                   
               }
               </tbody>
              </table>                
        </div>);
    }
});

module.exports = Sectors;