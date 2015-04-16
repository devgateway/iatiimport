'use strict';

var Router = require('director').Router;
var routes = require('./routes');
var Dispatcher = require('./dispatchers/default');
var pageConstants = require('./constants/page');
var routesConstants = require('./constants/routes');

// Setup router
var router = new Router(routes);

// Start listening to route changes
router.init();

// Handle urls by ensuring the use of hash routing
window.location.replace('/#' + window.location.pathname);

// Handle route and page changes
Dispatcher.register(function(payload) {

  var action = payload.action;

  if (action.actionType === routesConstants.SET_CURRENT_ROUTE) {
    router.setRoute(action.route);
  }

  else if (action.actionType === pageConstants.SET_CURRENT_PAGE) {
    // Set current page title
    document.title = action.page.title;
  }

  return true; // No errors.  Needed by promise in Dispatcher.
});

console.log('Welcome to Yeogurt');
