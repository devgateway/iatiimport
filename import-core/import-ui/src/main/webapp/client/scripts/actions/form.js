'use strict';

var Reflux = require('reflux');
var formActions = Reflux.createActions([
    'updateFilters',
    'updateSelectedProjects',    
]);

formActions.updateSelectedFields = Reflux.createAction({ asyncResult: true });
formActions.updateSelectedValues = Reflux.createAction({ asyncResult: true });
formActions.saveFieldMappingsTemplate = Reflux.createAction({ asyncResult: true });
formActions.saveValueMappingsTemplate = Reflux.createAction({ asyncResult: true });
module.exports = formActions;


