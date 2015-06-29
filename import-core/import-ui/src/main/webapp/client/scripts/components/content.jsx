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
      appConfig.DESTINATION_AUTH_TOKEN = this.state.destinationSessionData[0].authentication_token
  },
  updateSession : function (data) {
      this.setState({
          sessionData: data.sessionData
      });
  },
  render: function() {
    var userInfo = <h3>Session not initialized</h3>;
    var token = <h3>N/A</h3>;
    if (this.state.destinationSessionData) {
       userInfo = <div> User: {this.state.destinationSessionData[0].username} </div>;
       token = <div>Destination System Token: {this.state.destinationSessionData[0].authentication_token} </div>
    };
    return (
      <div className="container">
        <br/>
        <div className="jumbotron">
          Debug session information:
          {userInfo}
          {token}     
        </div>
      </div>
    );
  }
});

module.exports = Content;