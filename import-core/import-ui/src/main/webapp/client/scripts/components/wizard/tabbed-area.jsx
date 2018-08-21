var React = require('react');
var Tab = require('./tab');
var TabbedArea = React.createClass({
    propTypes: {        
        paneModels: React.PropTypes.array.isRequired,        
        activeTab: React.PropTypes.number.isRequired,        
        switchTab: React.PropTypes.func.isRequired
    },
 
    handleClick: function(idx, e) {
        e.preventDefault();
        this.props.switchTab(idx);
    },
 
    render: function() {        
          return (  <div>
                 <div className="col-sm-3 col-md-3">
                <ul className="nav nav-pills nav-stacked">
                    {this.renderTabs()}
                </ul>
                </div>
                <div className="col-sm-9 col-md-9 main">
                <div className="tab-content">
                    {this.renderPanes()}
                </div>
                </div>
            </div>);
        
    },
 
    renderTabs: function() {
        return this.props.paneModels.map(function(panePojo, idx) {
            return (
                <Tab key={idx} name={panePojo.tabName}   tooltip={panePojo.tooltip} i18nLib={this.props.i18nLib}              
                    onClick={this.handleClick.bind(this, idx)}
                    isActive={idx === this.props.activeTab}
                />
            );
        }.bind(this));
    },
 
    renderPanes: function() {
        return this.props.paneModels.map(function(paneModel, idx) {
            paneModel.classes['tab-pane'] = true;
            paneModel.classes.active = idx === this.props.activeTab;
            var classes = 'tab-pane';
            classes += paneModel.classes.active ? ' active' :'';            
            return (<div key={idx} className={classes}>                      
                        {paneModel.children}
                    </div>);
        }.bind(this));
    },
});

module.exports = TabbedArea;