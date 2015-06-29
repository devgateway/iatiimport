// `grunt`
// Defaults to running tests and building a production ready version of your site.

'use strict';

var taskConfig = function(grunt) {
  grunt.registerTask('default', 'Defaults to building a production ready version of your site.', [
    'build'
  ]);
};

module.exports = taskConfig;
