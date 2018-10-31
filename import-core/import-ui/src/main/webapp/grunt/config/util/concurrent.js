// Configuration for Concurrent task(s)
// Runs tasks in parallel to speed up the build process
'use strict';

var taskConfig = function(grunt) {

  grunt.config.set('concurrent', {
    images: [
      'imagemin:dist',
    ],
    compile: [
      'browserify:dist'
    ],
    qa: [
      'browserify:qa'
    ],

    docs: [
      'jsdoc:dist',
    ]
  });
};

module.exports = taskConfig;
