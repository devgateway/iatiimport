// Configuration for browserify task(s)
// Compiles JavaScript into single bundle file
'use strict';

var taskConfig = function(grunt) {

  grunt.config.set('browserify', {
    server: {
      options: {
        transform:  [ require('grunt-react').browserify ],
        browserifyOptions: {
          debug: true,
          extensions: '.jsx'
        },
        watch: true
      },
      files: {
        '<%= yeogurt.tmp %>/scripts/main.js': ['<%= yeogurt.client %>/scripts/main.js']
      }
    },
    dist: {
      options: {
        transform:  [ require('grunt-react').browserify ],
        browserifyOptions: {
          debug: true,
          extensions: '.jsx'
        },
        preBundleCB: function(b) {
          // Minify code
          return b.plugin('minifyify', {
            map: 'main.js.map',
            output: 'dist/scripts/main.js.map'
          });
        }
      },
      files: {
        '<%= yeogurt.dist %>/scripts/main.js': ['<%= yeogurt.client %>/scripts/main.js']
      }
    },
    qa: {
      options: {
        transform:  [ require('grunt-react').browserify ],
        browserifyOptions: {
          debug: true,
          extensions: '.jsx'
        }
      },
      files: {
        '<%= yeogurt.dist %>/scripts/main.js': ['<%= yeogurt.client %>/scripts/main.js']
      }
    },
    test: {
      options: {
        transform:  [ require('grunt-react').browserify ],
        browserifyOptions: {
          debug: true,
          extensions: '.jsx'
        },
        watch: true
      },
      files: {
        'test/scripts/bundle.js': ['test/spec/**/*.spec.js']
      }
    }
  });

};

module.exports = taskConfig;
