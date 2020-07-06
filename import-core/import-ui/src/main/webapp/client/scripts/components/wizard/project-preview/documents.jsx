var React = require('react');
var common = require('./../../../utils/common');
var constants = require('./../../../utils/constants');
var _ = require('lodash/dist/lodash.underscore');
var Documents = React.createClass({
    render: function () {
       var documents = this.props.project.documentLinkFields ? Object.values(this.props.project.documentLinkFields) : [];
       debugger;
       return (<div className="section_group_class" >
             <div className="section_title_class"><span>{this.props.i18nLib.t('project_preview.documents')}</span><span></span></div>
             <table className="box_table">
             <tbody>
             <tr>
               <td className="box_field_name">{this.props.i18nLib.t('project_preview.documents_title')}</td>
               <td className="box_field_name">{this.props.i18nLib.t('project_preview.documents_category')}</td>
               <td className="box_field_name">{this.props.i18nLib.t('project_preview.documents_year')}</td>
             </tr>
             {documents.map(function (document, index) {
               return (<tr key={index}>
                 <td className="box_field_value ">
                   <a href={document.url} target='_blank'>{document.title}</a>
                 </td>
                 <td className="box_field_value ">
                   {document.category + " - " + common.getValueName(this.props.sourceFieldsData,constants.FIELD_NAMES.CATEGORY, document.category)}
                 </td>
                 <td className="box_field_value ">
                   {document.year}
                 </td>
               </tr>)
             }.bind(this))
             }
             </tbody>
            </table>
        </div>);
    }
});

module.exports = Documents;
