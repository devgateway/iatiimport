var React = require('react');
var common = require('./../../../utils/common');
var constants = require('./../../../utils/constants');
var _ = require('lodash/dist/lodash.underscore');
var ParticipatingOrgs = React.createClass({   
    getOrgs: function() {
        var orgs = [];
        if (this.props.project.organizationFields) {
            for (var key in this.props.project.organizationFields) {
                var org = this.props.project.organizationFields[key];                
                var foundOrg = _.find(orgs, function(o) { 
                    return o.name === org.value;
                });
                
                if (foundOrg) {
                    foundOrg.role += ', ' +  org.role;
                } else {
                    orgs.push({name: org.value, role: org.role})    
                }                              
            }            
        }
        
        return orgs;
    },
    render: function () { 
       var orgs = this.getOrgs();
       return (<div className="section_group_class" >               
             <div className="section_title_class"><span>{this.props.i18nLib.t('project_preview.participating_orgs')}</span><span></span></div>               
               <table className="box_table">
               <tbody>
               <tr>
               <td className="box_field_name">{this.props.i18nLib.t('project_preview.org')}</td>
               <td className="box_field_name">{this.props.i18nLib.t('project_preview.role')}</td>
               </tr>
               {orgs.map(function(org){
                   return (<tr>
                   <td className="box_field_value ">
                    {org.name}
                   </td>
                    <td className="box_field_value ">
                    {org.role}
                   </td>
                   </tr>)
                 })                   
               }
               </tbody>
              </table>                
        </div>);
    }
});

module.exports = ParticipatingOrgs;