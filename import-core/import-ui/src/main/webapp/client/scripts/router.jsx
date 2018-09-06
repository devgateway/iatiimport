var React = require('react');
var Router = require('react-router');
var Route = Router.Route;
var DefaultRoute = Router.DefaultRoute;

var Layout = require('./components/layout');
var Home = require('./components/home');
var ManualProcess = require('./components/content');
var LandingPage = require('./components/landing-page');
var Wizard = require('./components/wizard');

var UploadFile = require('./components/wizard/upload-file');
var SelectDataSource = require('./components/wizard/select-data-source');
var SelectVersion = require('./components/wizard/select-version');
var FilterData = require('./components/wizard/filter-data');
var ChooseProjects = require('./components/wizard/choose-projects');
var ChooseFields = require('./components/wizard/choose-fields');
var MapValues = require('./components/wizard/map-values');
var MapValuesTab = require('./components/wizard/map-values-table');
var Import = require('./components/wizard/review-import');
var Reports = require('./components/reports/index');
var Admin = require('./components/admin/index');
var ImportList = require('./components/reports/import-list');
var ImportLog = require('./components/reports/import-log');
var WorkflowList = require('./components/reports/workflow-list');
var DataSource = require('./components/admin/data-source');
var ErrorPage = require('./components/error-page')
var appActions = require('./actions');
var Cookies = require('js-cookie');

var routes = (
	<Route name="layout" path="/" handler={Home}>
		<DefaultRoute handler={LandingPage} />	
        <Route name="error" path="error" handler={ErrorPage} />
		<Route name="manual" path="manual" handler={ManualProcess} />
		<Route path="wizard/:src/:dst" handler={Wizard}>	
			<Route name="upload" handler={UploadFile}/>        
            <Route name="selectdatasource" path="selectdatasource" handler={SelectDataSource}/>
            <Route name="selectversion" path="selectversion" handler={SelectVersion}/>        
			<Route name="filter" path="filter" handler={FilterData}/>
			<Route name="projects" path="projects" handler={ChooseProjects}/>
			<Route name="fields" path="fields" handler={ChooseFields}/>
			<Route name="mapvalues" path="values" handler={MapValues}>			  
			</Route>			
			<Route name="import" path="import" handler={Import}/>		  
		</Route>
		<Route path="reports" handler = {Reports}>
		   <Route name="previousimports" path="previousimports" handler={ImportList}/>" +
		   <Route name="workflowlist" path="workflowlist" handler={WorkflowList}/>
		   <Route name="importlog" path="importlog/:id" handler={ImportLog}/>
        </Route>
        <Route path="admin" handler = {Admin}>        
          <Route name="datasource" path="datasource" handler={DataSource}/>
        </Route>			
	</Route>
);


exports.start = function() {
   appActions.initDestinationSession.triggerPromise().then(function(data) {       
        Cookies.set("IS_ADMIN", data['is-admin']); 
        Router.run(routes, function (Handler) {
            React.render(<Handler />, document.getElementById('app-wrapper'));
        });       
      }.bind(this))["catch"](function(err) {
          Cookies.set("IS_ADMIN", null); 
          Router.run(routes, function (Handler) {
              React.render(<Handler />, document.getElementById('app-wrapper'));
          });
          
      }.bind(this));

  
}
