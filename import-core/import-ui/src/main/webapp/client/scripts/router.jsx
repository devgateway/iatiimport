var React = require('react');
var Router = require('react-router');
var Route = Router.Route;
var DefaultRoute = Router.DefaultRoute;

var Layout = require('./components/layout');
var Home = require('./components/home');
var Wizard = require('./components/wizard');

var routes = (
	<Route name="layout" path="/" handler={Layout}>
		<DefaultRoute handler={Home} />
		<Route path="wizard/:id" handler={Wizard}/>
	</Route>
);

exports.start = function() {
  Router.run(routes, function (Handler) {
		React.render(<Handler />, document.getElementById('app-wrapper'));
	});
}
