'use strict';

var Reflux = require('reflux');
var formActions = Reflux.createActions([    
    'updateSelectedProjects',    
]);


formActions.updateLanguages = Reflux.createAction({ asyncResult: true });
formActions.updateFilters = Reflux.createAction({ asyncResult: true });
formActions.updateSelectedFields = Reflux.createAction({ asyncResult: true });
formActions.updateSelectedValues = Reflux.createAction({ asyncResult: true });
formActions.saveFieldMappingsTemplate = Reflux.createAction({ asyncResult: true });
formActions.saveValueMappingsTemplate = Reflux.createAction({ asyncResult: true });

formActions.updateDataSource = Reflux.createAction({ asyncResult: true });
formActions.loadDataSource = Reflux.createAction({ asyncResult: true });
formActions.loadReportingOrganizations = Reflux.createAction({ asyncResult: true });
formActions.loadDataSource = Reflux.createAction({ asyncResult: true });
formActions.loadReportingOrgsWithUpdates = Reflux.createAction({ asyncResult: true });

module.exports = formActions;


