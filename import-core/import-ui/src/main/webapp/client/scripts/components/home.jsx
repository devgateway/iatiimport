var React = require('react');

var Home = React.createClass({

  render: function() {

    return (
      <div className="container">
        <div className="jumbotron">
          <h1>Basic DG React+Flux app</h1>
          <div className="alert alert-success">
          Generated using <a href="https://github.com/larsonjj/generator-yeogurt">yeogurt</a> and changing the dependencies and structure to match reflux.
          </div>
          <ul>
              <li>ReactJS Reflux Boilerplate</li>
              <li>Bootstrap</li>
              <li>Modernizr</li>
              <li>LiveReload</li>
          </ul>
          <pre>Use "grunt serve" to start the js app</pre>
        </div>
      </div>
    );
  }
});

module.exports = Home;
