'use strict';

var React = require('react');
var routeActions = require('./actions/routes');
var IndexPage = React.createFactory(require('./components/index.jsx'));

var render = function(Page) {
  React.render(new Page(), document.getElementById('app-wrapper'));
};

var index = function() {
  render(IndexPage);
};

var routes = {
  '/': index
};

module.exports = routes;
