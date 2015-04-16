// Configuration for JSLHint task(s)
// Runs JSHint on specified files
'use strict';

var taskConfig = function(grunt) {

  grunt.config.set('jshint', {
    options: {
      jshintrc: '.jshintrc',
      reporter: require('jshint-stylish')
    },
    test: [
      'Gruntfile.js',
      '<%= yeogurt.client %>/scripts/**/*.js',
      '!<%= yeogurt.client %>/scripts/vendor/**/*.*'
    ]
  });

};

module.exports = taskConfig;
