var React = require('react');
var _ = require('lodash/dist/lodash.underscore');
var appConfig = require('./../../conf');

var Router = require('react-router');
var RouteHandler = Router.RouteHandler;

var formActions = require('./../../actions/form');

var Reports = React.createClass({  
  getInitialState: function() {
    return {      
    };
  },

  componentWillReceiveProps: function(nextProps) {   
  },

  componentDidMount  : function() {
    
  },  
  render: function() {   
    return (
        <div>
      <div className="container " >     
      <h2></h2>
      <div className="row">          
      <RouteHandler {...this.props}/>
      </div>
      </div>
      </div>      
      );
  }
});
module.exports = Reports;