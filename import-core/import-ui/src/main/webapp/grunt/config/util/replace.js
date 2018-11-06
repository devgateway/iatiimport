// Configuration for String replace task(s)
// uses on scope of IATIIMPORT-206 to replace the footer with the build information
// can be used to repalce any kind of string
'use strict';

var taskConfig = function (grunt) {
  var buildsource = grunt.option('buildsource');
  if(!buildsource) {
    buildsource = '';
  }
  grunt.config.set('string-replace', {
    dist: {
      files: {
        '<%= yeogurt.dist %>/scripts/main.js': ['<%= yeogurt.dist %>/scripts/main.js']
      },
      options: {
        replacements: [{
          pattern: '@@buildSource@@',
          replacement: buildsource
        }]
      }
      }
  });
};

module.exports = taskConfig;
