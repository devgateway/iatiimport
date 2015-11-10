var React = require('react');
var reactAsync = require('react-async');
var Reflux = require('reflux');
var Cookies = require('js-cookie');
var appActions = require('./../actions');
var appConfig = require('./../conf');
var destinationSessionStore = require('./../stores/DestinationSessionStore');
//var sessionStore = require('./../stores/SessionStore');

var Content = React.createClass({
  mixins: [
      reactAsync.Mixin, Reflux.ListenerMixin
  ],
  getInitialStateAsync: function() {
    appActions.initDestinationSession();
    appActions.initSession();
  },
  componentDidMount  : function() {
    // from the path '/wizard/:id'
    var id = this.props.params.id;
    this.listenTo(destinationSessionStore, this.updateDestinationSession);    
  },
  updateDestinationSession : function (data) {
    this.setState({
        destinationSessionData: data.sessionData
    });
    appConfig.DESTINATION_AUTH_TOKEN = this.state.destinationSessionData.token;
    appConfig.DESTINATION_USERNAME = this.state.destinationSessionData['user-name'];    
    Cookies.set("DESTINATION_AUTH_TOKEN", this.state.destinationSessionData.token);
    Cookies.set("DESTINATION_USERNAME", this.state.destinationSessionData['user-name']);
    // Added true always for now, the API returns wrong value
    Cookies.set("CAN_ADD_ACTIVITY", true || this.state.destinationSessionData['add-activity']);
    Cookies.set("WORKSPACE", this.state.destinationSessionData['team']);
    
  },
  updateSession : function (data) {
      this.setState({
          sessionData: data.sessionData
      });
  },
  render: function() {
    var token = <h3>N/A</h3>;
    var url = <h3>N/A</h3>;
    var username = <h3>N/A</h3>;
    var team = <h3>N/A</h3>;
    if(this.state.destinationSessionData && !this.state.destinationSessionData.token) {
      return (
        <div className="container">
          <br/>
          <div className="alert alert-danger server-status-message" role="alert" ><span className="glyphicon glyphicon-exclamation-sign error-box" aria-hidden="true"></span><span className="sr-only">Error:</span><span > Session information for the destination system could not be retrieved. Verify if backend services are working correctly.</span> </div>           
        </div>
      );
    }
    
    if(this.state.destinationSessionData && !(true ||this.state.destinationSessionData['add-activity'])){
    	return (<div className="container"><br/><div className="alert alert-danger server-status-message" role="alert" ><span className="glyphicon glyphicon-exclamation-sign error-box" aria-hidden="true"></span><span className="sr-only">Error:</span><span > Access Denied. {Cookies.set("DESTINATION_USERNAME")} does not have permission to import activities into {Cookies.set("WORKSPACE")} workspace.</span> </div></div>);
    }

    if (this.state.destinationSessionData) {
       token = <div>Destination System Token: {this.state.destinationSessionData.token} </div>
       url = <div>Destination System URL: {this.state.destinationSessionData.url} </div>
       username = <div> User: {this.state.destinationSessionData['user-name']} </div>;
       team = <div> Team: {this.state.destinationSessionData.team} </div>;
    };
    var debugInfo = <div className="jumbotron"> </div>;
    if(appConfig.DEBUG) {
      var debugInfo = (
          <div className="jumbotron">
            Debug session information:
            {token}     
            {url}     
            {username}
            {team}
          </div>
      );
    }
    return (
      <div className="container">
        <br/>
          {debugInfo}
      </div>
    );
  }
});

module.exports = Content;