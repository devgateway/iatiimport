var React = require('react');
var _ = require('lodash/dist/lodash.underscore');
var SimilarProjectsDialog = React.createClass({
    onProjectSelectionChange: function(event) {
       var selectedProject = _.find(this.props.projectMapping.projectsWithSimilarTitles, function(item) { return item.stringFields.internalId === event.target.dataset.id;});
       this.props.updateSimilarProjectSelected(selectedProject);
    },
    mapSelectedProject: function() {
       this.props.mapSelectedProject();
       $('#similarProjects').modal('hide');
    },
    onClose: function() {
      $('#similarProjects').modal('hide');
    },
    isMapSelectedProjectButtonDisabled: function() {
      if (this.props.similarProjectSelected || (this.props.projectMapping && this.props.projectMapping.destinationDocument)) {
        return false;
      }
      return true;
    },
    render: function () {
       var rows = [];
       if (this.props.projectMapping && this.props.projectMapping.projectsWithSimilarTitles){
           var project = this.props.similarProjectSelected ? this.props.similarProjectSelected : this.props.projectMapping.destinationDocument;
           var selectedProjectInternalId = project ? project.stringFields.internalId : "";
           $.map(this.props.projectMapping.projectsWithSimilarTitles, function (item, i) {
             var internalId = item.stringFields.internalId;
            rows.push(<tr>
                       <td>
                        <input type="checkbox" checked={selectedProjectInternalId === internalId} data-id={internalId} onChange={this.onProjectSelectionChange} />
                          </td>
                          <td>
                           {item.identifier}
                          </td>
                           <td>
                           {this.props.getTitle(item, this.props.i18nLib.lng())}
                          </td>
                         </tr>)
              }.bind(this));
       }

       var mapSelectedDisabled = this.isMapSelectedProjectButtonDisabled();

       return (
          <div className="modal fade" id="similarProjects" tabIndex="-1" role="dialog" aria-labelledby="myModalLabel2" aria-hidden="true" >
			   <div ref="similarProjectsDialog" className="modal-dialog">
			    <div className="modal-content">
			      <div className="modal-header">
			        <button type="button" className="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">x</span></button>
			        <h4 className="modal-title" id="myModalLabel2">{this.props.i18nLib.t('wizard.similar_projects_dlg.title')}</h4>
			      </div>
			      <div className="modal-body">

			      <table className="table">
			      <thead>
                       <tr>
                          <th>
                          </th>
                          <th>
                          {this.props.i18nLib.t('wizard.similar_projects_dlg.iati_id')}
                          </th>
                          <th>
                          {this.props.i18nLib.t('wizard.similar_projects_dlg.project_title')}
                          </th>
                       </tr>
                   </thead>
                   <tbody>
                    {rows}
                   </tbody>
			      </table>
			      <div className="modal-footer">
			        <button type="button" className="btn btn-default btn-warning" data-dismiss="modal" onClick={this.onClose}>{this.props.i18nLib.t('wizard.similar_projects_dlg.close')}</button>
			        <button type="button" disabled={mapSelectedDisabled} className="btn btn-primary" onClick={this.mapSelectedProject}>{this.props.i18nLib.t('wizard.similar_projects_dlg.select_project')}</button>
			      </div>
			    </div>
			  </div>
			</div>
			</div>
        );
    }
});



module.exports = SimilarProjectsDialog;
