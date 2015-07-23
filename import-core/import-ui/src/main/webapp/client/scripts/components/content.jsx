var React = require('react');
var reactAsync = require('react-async');
var Reflux = require('reflux');
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
//    this.listenTo(sessionStore, this.updateSession);
  },
  updateDestinationSession : function (data) {
    this.setState({
        destinationSessionData: data.sessionData
    });
    appConfig.DESTINATION_AUTH_TOKEN = this.state.destinationSessionData.token
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
            <div className="jumbotron">
            Session information for the destination system could not be retrieved. Verify if backend services are working correctly.
            
            </div>
        </div>
      );
    }

    if (this.state.destinationSessionData) {
       token = <div>Destination System Token: {this.state.destinationSessionData.token} </div>
       url = <div>Destination System URL: {this.state.destinationSessionData.url} </div>
       username = <div> User: {this.state.destinationSessionData['user-name']} </div>;
       team = <div> User: {this.state.destinationSessionData.team} </div>;
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