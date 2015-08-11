var React = require('react');
var Reflux = require('reflux');
var Router = require('react-router');
var Link = Router.Link;
var _ = require('lodash/dist/lodash.underscore');
var moment = require('moment');
var Header = require('./../layout/header');
var Footer = require('./../layout/footer');
var appConfig = require('./../../conf');
var Router = require('react-router');
var RouteHandler = Router.RouteHandler;
var appActions = require('./../../actions');
var importListStore = require('./../../stores/ImportListStore');
var ImportList = React.createClass({ 
  mixins: [
        Reflux.ListenerMixin
    ],
  getInitialState: function() {
    return {     
      importListData: []
    };
  },  
   componentWillMount: function () {
     this.listenTo(importListStore, this.updateImportList);             
     this.loadData();
    },
    updateImportList: function (data) {        
        this.setState({
            importListData: data
        });
    }, 
    loadData: function(){	 
     appActions.loadImportListData.triggerPromise().then(function(data) {                                       
        this.updateImportList(data);                
      }.bind(this)).catch(function(err) {
        console.log(err);
        console.log('Error loading importList');
     }.bind(this));
  },
  render: function() {  
        var importList = [];
        if (this.state.importListData && this.state.importListData.length > 0) {        
        $.map(this.state.importListData, function (item, i) {    
            var createdDate = moment(item.createdDate).fromNow();
            importList.push(<tr key={item.id}>
                   <td>
                        {item.id}
                    </td>
                    <td>
                        {item.fileName}
                    </td>
                    <td>
                        {createdDate}
                    </td>
                    <td>
                       <Link to="importlog" params={{id:item.id}}  >View Import	</Link>
                    </td>
                </tr>);
            });
        }
      
    return (
     <div className="container " >
      <h2>Previous Imports</h2>      
         <table className="table file-list">
                    <thead>
                        <tr>
                           <th>
                                 ID
                            </th>
                            <th>
                                 File Name
                            </th>
                            <th>
                                Upload Date
                            </th>
                            <th>
                                Action
                            </th>
                        </tr>
                    </thead>
                    <tbody>
                        {importList}
                    </tbody>
                </table>
      </div>
      );
  }
});
module.exports = ImportList;