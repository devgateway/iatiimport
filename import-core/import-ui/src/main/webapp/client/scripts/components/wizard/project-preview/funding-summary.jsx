var React = require('react');
var common = require('./../../../utils/common');
var constants = require('./../../../utils/constants');
var FundingSummary = React.createClass({
    getFundingInfo: function() {
        var result = {};
        if (this.props.project.transactionFields) {
            for (var key in this.props.project.transactionFields) {
                var transaction = this.props.project.transactionFields[key]
                var transactionType = constants.FIELD_NAMES.TRANSACTION + '_' + transaction.subtype;
                if (result[transactionType]) {
                    result[transactionType] += transaction.value ? parseFloat(transaction.value) : 0;
                } else {
                    result[transactionType] = transaction.value ? parseFloat(transaction.value) : 0;
                }                
            }
        }
       
        return result;           
    },
    
    render: function () {  
        var fundingSum = this.getFundingInfo();
        var totalCommitments = common.formatNumber(fundingSum[constants.FIELD_NAMES.COMMITMENTS]);
        var totalDisbursements = common.formatNumber(fundingSum[constants.FIELD_NAMES.DISBURSEMENTS]);
        var totalExpenditure = common.formatNumber(fundingSum[constants.FIELD_NAMES.EXPENDITURE]);
        return (<div className="summary_section_group" >
                <div className="summary_field_value">
                <span>{this.props.i18nLib.t('project_preview.funding_information')}</span><span></span>
                </div>
                
                <div className="block">
                <div className="summary_field_name block">{this.props.i18nLib.t('project_preview.total_commitments')}</div>
                <div className="section_field_value block">{this.props.project.stringFields['default-currency']} {totalCommitments}</div>
                </div>
                
                <div className="block">
                <div className="summary_field_name block">{this.props.i18nLib.t('project_preview.total_disbursements')}</div>
                <div className="section_field_value block">{this.props.project.stringFields['default-currency']} {totalDisbursements}</div>
                 </div>
                
                <div className="block">
                <div className="summary_field_name block">{this.props.i18nLib.t('project_preview.total_expenditure')}</div>
                <div className="section_field_value block">{this.props.project.stringFields['default-currency']} {totalExpenditure}</div>
                 </div>              
                
               </div>);
    }
});

module.exports = FundingSummary;