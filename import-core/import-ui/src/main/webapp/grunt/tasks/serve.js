// `grunt serve`
// Starts up a development server that watches for local file changes
// and automatically reloads them to the browser.

'use strict';

var taskConfig = function(grunt) {
  grunt.registerTask('serve', 'Open a development server within your browser', function(target) {
    // Allow for remote access to app/site via the 0.0.0.0 ip address
    if (grunt.option('allow-remote')) {
      grunt.config.set('connect.options.hostname', '0.0.0.0');
    }

    if (target === 'dist') {
      return grunt.task.run(['build', 'connect:dist:keepalive']);
    }

    grunt.task.run([
      'clean:tmp',
      'injector',
      'wiredep',
      'browserify:server',
      'jsdoc:server',
      'autoprefixer:server'
    ]);

    if (target === 'nowatch') {
      return;
    }

    grunt.task.run([
      'connect:server'
    ]);

    
    if (target === 'docs') {
      return grunt.task.run(['listen:docs']);
    }

    return grunt.task.run(['watch']);
    
  });
};

module.exports = taskConfig;
