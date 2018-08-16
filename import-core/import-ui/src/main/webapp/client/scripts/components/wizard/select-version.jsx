var React = require('react');
var _ = require('lodash/dist/lodash.underscore');
var fileStore = require('./../../stores/FileStore');
var moment = require('moment');
var reactAsync = require('react-async');
var Reflux = require('reflux');
var appActions = require('./../../actions');
var appConfig = require('./../../conf');
var DataSourceStore = require('./../../stores/DataSourceStore');
var constants = require('./../../utils/constants');
var formActions = require('./../../actions/form');
var SelectVersion = React.createClass({
    mixins: [Reflux.ListenerMixin
    ],
    getInitialState: function() {
       return {};
    },
    componentDidMount: function() {  
        this.props.eventHandlers.updateCurrentStep(constants.SELECT_VERSION);
    }, 
    handleNext: function() {
        this.props.eventHandlers.initAutomaticImport();
    },
    render: function() {      
        return (
            <div className="panel panel-default">
                <div className="panel-heading"><strong>{this.props.i18nLib.t('select_version.title')}</strong></div>
                <div className="panel-body">
                {this.props.versions &&
                    <div>
                     <label>{this.props.i18nLib.t('select_version.currently_importing')} {this.props.currentVersion}</label> <br/>  
                      {this.props.versions.filter(function(v){ return v != this.props.currentVersion}.bind(this)).length > 0 &&
                          <div>
                          <label>{this.props.i18nLib.t('select_version.other_versions_available')}</label> <br/>                    
                           <ul className="list-unstyled workflow-selector">
                               {this.props.versions.filter(function(v){ return v != this.props.currentVersion}.bind(this)).map(function(version){
                                  return (<li className="workflow-link">{version}</li>);  
                               })}
                           </ul>      
                            <br/>
                           {this.props.i18nLib.t('select_version.import_will_repeat')}                            
                          </div>
                      }                     
                    </div>
                }
                </div>
                <div className="buttons">
                    <button className="btn btn-success navbar-btn btn-custom" type="button" onClick={this.handleNext}>{this.props.i18nLib.t('data_source.next')}</button>
                </div>
            </div>
            );
    } 
}); 
module.exports = SelectVersion;
