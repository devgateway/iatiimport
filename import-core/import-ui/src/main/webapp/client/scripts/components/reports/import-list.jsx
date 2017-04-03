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
      activePage:1,
      sort: {field:'createdDate', direction: 'desc'}
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
     var params = {size:10, page:this.state.activePage - 1, sort: this.state.sort}
     appActions.loadImportListData(params).then(function(data) {                                       
        this.updateImportList(data);                
      }.bind(this))["catch"](function(err) {       
        console.log('Error loading importList');
     }.bind(this));
  },
  onSort: function(e){	  
	 var direction = (this.state.sort.direction == 'asc') ? 'desc' : 'asc';	  
	 this.setState({ sort: {field:e.target.getAttribute('data-field'), direction: direction}},function() {
		this.loadData();
	 });	 
  },
  handlePageSelect:function(event, selectedEvent){    
    this.setState({
      activePage: selectedEvent.eventKey}, function(){
    	  this.loadData();
      });
    
  },
  handleDelete:function(e){	  
	  if(confirm("Are you sure you want to delete " + e.target.getAttribute('data-name') + "?")){
		  var id = e.target.getAttribute('data-id');
		  appActions.deleteImport(id).then(function(data) {    	   
				var importList = this.state.importListData;   	
				importList.content  = importList.content.filter(function (item) {	return item.id != id;	});				    	     
				this.setState({
					importListData: importList
				});            
				this.forceUpdate();         
			}.bind(this)); 		
	} 
	  
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
                       <Link to="importlog" params={{id:item.id}}  >{window.i18nLib.t('previous_imports.view_import')}	</Link><span> | </span> 
                       <span  className = "fake-link" data-id={item.id}data-name={item.fileName} onClick={this.handleDelete}>{window.i18nLib.t('previous_imports.delete')}</span>
                    </td>
                </tr>);
            }.bind(this));
        }
      
    return (
     <div className="container " >
      <h2>{window.i18nLib.t('previous_imports.title')}</h2>      
         <table className="table file-list">
                    <thead>
                        <tr>
                           <th>
                                 ID
                                 <i className="fa fa-fw fa-sort" data-field="id" onClick={this.onSort}></i>
                            </th>
                            <th>
                                 {window.i18nLib.t('previous_imports.file_name')}
                                 <i className="fa fa-fw fa-sort" data-field="fileName" onClick={this.onSort}></i>
                            </th>
                            <th>
                                {window.i18nLib.t('previous_imports.upload_date')}
                                <i className="fa fa-fw fa-sort" data-field="createdDate" onClick={this.onSort}></i>
                            </th>
                            <th>
                                {window.i18nLib.t('previous_imports.action')}
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