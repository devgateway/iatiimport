var React = require('react');
var reactAsync = require('react-async');
var Reflux = require('reflux');
var Cookies = require('js-cookie');
var appActions = require('./../actions');
var appConfig = require('./../conf');
var WorlflowSelector = require('./workflow-selector.jsx')
var Content = React.createClass({ 
  componentDidMount  : function() {	           
  },
  render: function() {  
    return (
      <div className="container">
        <WorlflowSelector/>          
      </div>
    );
  }
});
module.exports = Content;