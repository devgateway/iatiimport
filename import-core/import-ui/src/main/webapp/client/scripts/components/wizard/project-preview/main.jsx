var React = require('react');
var _ = require('lodash/dist/lodash.underscore');
var Header = require('./header');
var Identification = require('./identification');
var FundingSummary = require('./funding-summary');
var ParticipatingOrgs = require('./participating-orgs');
var Transactions = require('./transactions');
var Planning = require('./planning');
var Sectors = require('./sectors');
var Locations = require('./locations');
var RecipientCountries = require('./recipient-countries');
var PolicyMarkers = require('./policy-markers');

var ProjectPreview = React.createClass({
    getInitialState: function() {
       return {selectedProject: null};
    },
    onClose: function() {
      this.props.closeProjectPreview();
    },
    render: function () {
        return (
          <div className="modal fade in" id="projectPreview" tabIndex="-1" role="dialog" aria-hidden="true" style={{display: 'block'}} >
			   <div ref="projectPreviewDialog" className="modal-dialog projectPreview">
			    <div className="modal-content">
			      <div className="modal-header">
			        <button type="button" className="close" data-dismiss="modal" aria-label="Close" onClick={this.onClose}><span aria-hidden="true">x</span></button>
			        <h4 className="modal-title" >{this.props.i18nLib.t('project_preview.title')}</h4>
			      </div>
			      <div className="modal-body">
			        <div className="preview_container">
			        <Header {...this.props}/>
			        <div className="preview_content">
			        <div className="container-fluid">
                     <div className="row">
			           <div className="col-md-9 left-column">
			              <div className="main_group_container">
			               <Identification {...this.props}/>
			               <Planning {...this.props}/>
			               <ParticipatingOrgs {...this.props}/>
			               <RecipientCountries {...this.props}/>
			               <Sectors {...this.props}/>
			               <PolicyMarkers {...this.props}/>
			               <Locations {...this.props}/>
			               <Transactions {...this.props}/>
			              </div>
			           </div>
			            <div className="preview_summary col-md-offset-9">
			              <div className="summary_container">
			                <FundingSummary {...this.props}/>
			              </div>
                        </div>

                    </div>
			        </div>
			        </div>


			        </div>

			    </div>
			        <div className="modal-footer">
                    <button type="button" className="btn btn-default btn-warning" data-dismiss="modal" onClick={this.onClose}>{this.props.i18nLib.t('project_preview.close')}</button>
                  </div>
			  </div>
			</div>
			</div>
        );
    }
});

module.exports = ProjectPreview;
