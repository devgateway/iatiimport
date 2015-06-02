var React = require('react');
var Router = require('react-router');
var Route = Router.Route;
var DefaultRoute = Router.DefaultRoute;

var Layout = require('./components/layout');
var Home = require('./components/home');
var Content = require('./components/content');
var Wizard = require('./components/wizard');

var UploadFile = require('./components/wizard/upload-file');
var FilterData = require('./components/wizard/filter-data');
var ChooseProjects = require('./components/wizard/choose-projects');
var ChooseFields = require('./components/wizard/choose-fields');
var MapValues = require('./components/wizard/map-values');
var MapValuesTab = require('./components/wizard/map-values-table');
var Import = require('./components/wizard/review-import');

var routes = (
	<Route name="layout" path="/" handler={Home}>
		<DefaultRoute handler={Content} />		
		<Route path="wizard/:id" handler={Wizard}>	
			<DefaultRoute name="upload" handler={UploadFile}/>
			<Route name="filter" path="filter" handler={FilterData}/>
			<Route name="projects" path="projects" handler={ChooseProjects}/>
			<Route name="fields" path="fields" handler={ChooseFields}/>
			<Route name="mapvalues" path="values" handler={MapValues}>			  
			</Route>			
			<Route name="import" path="import" handler={Import}/>		  
		</Route>
		
	</Route>
);

exports.start = function() {
  Router.run(routes, function (Handler) {
		React.render(<Handler />, document.getElementById('app-wrapper'));
	});
}
