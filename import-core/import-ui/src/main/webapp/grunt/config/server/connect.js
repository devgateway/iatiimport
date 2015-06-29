// Configuration for Connect task(s)
// Boots up a server that loads up all static files
// Enables livereload
'use strict';

var taskConfig = function(grunt) {

  grunt.config.set('connect', {
    options: {
      port: 9010,
      livereload: 35729,
      hostname: 'localhost'
    },
    server: {
      options: {
        open: 'http://localhost:9010/',
        base: '<%= yeogurt.client %>/.serve',
        middleware: function(connect) {
          return [
            require('grunt-connect-prism/middleware'),
            connect.static('.tmp'),
            connect().use('/bower_components', connect.static('./client/bower_components')),
            connect.static('client')
          ];
        }
      }
    },
    dist: {
      options: {
        open: 'http://127.0.0.1:9010/',
        base: '<%= yeogurt.dist %>',
        livereload: false
      }
    }
  });

};

module.exports = taskConfig;
