var React = require('react');
var common = require('./../../../utils/common');
var constants = require('./../../../utils/constants');
var _ = require('lodash/dist/lodash.underscore');
var Transactions = React.createClass({   
    getTransactions: function() {
        var transactions = [];
        if (this.props.project.transactionFields) {
            for (var key in this.props.project.transactionFields) {                
                var transaction = this.props.project.transactionFields[key]; 
                transactions.push(transaction);                                        
            }            
        }
        
        return transactions;
    },
    getProvider: function(transaction) {
       var provider = transaction['providing-org'];
       if (provider) {
           return provider;
       } else {
           if (this.props.project.organizationFields) {
               for (var key in this.props.project.organizationFields) {
                   var org = this.props.project.organizationFields[key];
                   
                   if (org.role === constants.ROLE_FUNDING) {
                       provider = org.value;
                   }                   
               }              
           }
       }
       
       return provider;
    },
    render: function () {         
       var transactions = this.getTransactions();
       return (<div className="section_group_class" >               
             <div className="section_title_class"><span>{this.props.i18nLib.t('project_preview.transactions')}</span><span></span></div>               
               <table className="box_table table-borders">
               <tbody>
               <tr>
               <td className="box_field_name">{this.props.i18nLib.t('project_preview.transaction_date')}</td>
               <td className="box_field_name">{this.props.i18nLib.t('project_preview.transaction_provider')}</td>
               <td className="box_field_name">{this.props.i18nLib.t('project_preview.transaction_type')}</td>
               <td className="box_field_name">{this.props.i18nLib.t('project_preview.transaction_value')}</td>               
               </tr>
               {transactions.map(function(transaction, index) {
                   return (<tr key={index}>
                   <td className="box_field_value ">
                      {transaction.date}
                   </td>
                    <td className="box_field_value ">
                      {this.getProvider(transaction)}
                   </td>
                    <td className="box_field_value ">
                      {this.props.i18nLib.t('project_preview.transaction_' + transaction.subtype)}
                    </td>
                    <td className="box_field_value ">
                      {transaction.currency || this.props.project.stringFields['default-currency']} {common.formatNumber(transaction.value)}
                    </td>                   
                   </tr>)
                 }.bind(this))                   
               }
               </tbody>
              </table>                
        </div>);
    }
});

module.exports = Transactions;