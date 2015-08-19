'use strict';

var Reflux = require('reflux');
var formActions = Reflux.createActions([
    'updateFilters',
    'updateSelectedProjects',
    'updateSelectedValues'
]);

formActions.updateSelectedFields = Reflux.createAction({ asyncResult: true });
formActions.saveFieldMappingsTemplate = Reflux.createAction({ asyncResult: true });
module.exports = formActions;


