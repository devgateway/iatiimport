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
var Pagination = require('react-bootstrap/lib/Pagination');
var ImportList = React.createClass({ 
  mixins: [
        Reflux.ListenerMixin
    ],
  getInitialState: function() {
    return {     
      importListData: [],
      pageSize:10, 
      activePage:1
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
     var params = {size:10, page:this.state.activePage - 1}
     appActions.loadImportListData(params).then(function(data) {                                       
        this.updateImportList(data);                
      }.bind(this)).catch(function(err) {       
        console.log('Error loading importList');
     }.bind(this));
  },
  handlePageSelect:function(event, selectedEvent){    
    this.setState({
      activePage: selectedEvent.eventKey
    });
    this.loadData();
  },
  render: function() {  
        var importList = [];
        var itemsSize = this.state.importListData.totalElements > this.state.pageSize?(this.state.importListData.totalElements / this.state.pageSize).toFixed(0) : 1;       
        if (this.state.importListData.content && this.state.importListData.content.length > 0) {        
        $.map(this.state.importListData.content, function (item, i) {    
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
                <Pagination bsSize='small' items={itemsSize} activePage={this.state.activePage} onSelect={this.handlePageSelect} />
      </div>
      );
  }
});
module.exports = ImportList;