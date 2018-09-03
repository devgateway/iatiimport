var React = require('react');
var common = require('./../../../utils/common');
var constants = require('./../../../utils/constants');
var _ = require('lodash/dist/lodash.underscore');
var RecipientCountries = React.createClass({   
    render: function () { 
       var countries = [];
       if (this.props.project.recepientCountryFields && this.props.project.recepientCountryFields['recipient-country']) {
           countries = this.props.project.recepientCountryFields['recipient-country'];
       }
       return (<div className="section_group_class" >               
             <div className="section_title_class"><span>{this.props.i18nLib.t('project_preview.recipient')}</span><span></span></div>               
               <table className="box_table">
               <tbody>
                  {countries.map(function(country){
                   return (<tr>
                   <td className="box_field_value ">
                    {country.value}
                   </td>                   
                   </tr>)
                 })                   
               }
               </tbody>
              </table>                
        </div>);
    }
});

module.exports = RecipientCountries;