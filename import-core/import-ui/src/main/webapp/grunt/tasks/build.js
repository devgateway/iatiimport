// `grunt build`
// Builds out an optimized site through (but not limited to) minification of CSS and HTML,
// as well as uglification and optimization of Javascript, and compression of images.

'use strict';

var taskConfig = function(grunt) {
  grunt.registerTask('build', 'Open a development server within your browser', function () {

    var target = grunt.option('target');
    var concurrentTask = 'concurrent:compile';

    if (target && target === 'qa') {
      concurrentTask = 'concurrent:qa';
    }

    return grunt.task.run([
      'clean:dist',
      'injector',
      'wiredep',
      'copy:dist',
      concurrentTask,
      'useminPrepare',
      'concat:generated',
      'cssmin',
      'autoprefixer:server',
      'usemin',
      'htmlmin:dist',
      'uglify',
      'clean:tmp'
    ]);
  });
};
module.exports = taskConfig;
