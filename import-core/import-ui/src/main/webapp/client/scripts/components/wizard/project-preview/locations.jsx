var React = require('react');
var common = require('./../../../utils/common');
var constants = require('./../../../utils/constants');
var _ = require('lodash/dist/lodash.underscore');
var Locations = React.createClass({ 
    render: function () { 
       var locations = (this.props.project.stringMultiFields && this.props.project.stringMultiFields.location) ? this.props.project.stringMultiFields.location : [];
       return (<div className="section_group_class" >               
             <div className="section_title_class"><span>{this.props.i18nLib.t('project_preview.locations')}</span><span></span></div>               
             <table className="box_table">
             <tbody>            
             {locations.map(function(location){
                 return (<tr>
                 <td className="box_field_value ">
                  {location}
                  </td>                   
                 </tr>)
               }.bind(this))                   
             }
             </tbody>
            </table>             
        </div>);
    }
});

module.exports = Locations;