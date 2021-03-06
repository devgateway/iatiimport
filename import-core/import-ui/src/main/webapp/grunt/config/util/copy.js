// Configuration for Copy task(s)
// Copies specified folders/files to specified destination
'use strict';

var taskConfig = function(grunt) {

  grunt.config.set('copy', {
    dist: {
      files: [{
        expand: true,
        cwd: '<%= yeogurt.client %>/',
        dest: '<%= yeogurt.dist %>/',
        src: [
          'fonts/**/*.{woff,woff2,otf,ttf,eot,svg}',
          'images/**/*.{webp,gif,png}',
          'mockup/**/*.json',
          '!*.js',
          '*.{ico,png,txt}',
          '*.html'
        ]
      }]
    }
  });

};

module.exports = taskConfig;
