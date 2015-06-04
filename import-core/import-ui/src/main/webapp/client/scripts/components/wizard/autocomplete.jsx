var React = require('react');
var AutoComplete = React.createClass({
    componentDidMount: function () {
        var $el = $(this.refs[this.props.refId].getDOMNode());
        var searchEngine = new Bloodhound({
            datumTokenizer: function (d) {
                return Bloodhound.tokenizers.whitespace(d[this.props.display]);
            },
            queryTokenizer: Bloodhound.tokenizers.whitespace,
            remote        : {
                url     : this.props.url,
                wildcard: '%QUERY'
            }
        });
        searchEngine.initialize();
        $el.typeahead(null, {
            name      : this.props.name,
            displayKey: this.props.display,
            source    : searchEngine.ttAdapter()
        });
        
        var self = this;
        
        $el.bind('typeahead:selected', function (obj, datum, name) {           
            self.props.onSelect(self.props.data,datum);
        });
    },
    render: function () {
        return (
            <div className="autocomplete">
                <input className="typeahead" placeholder={this.props.placeholder} ref={this.props.refId} type="text"/>
            </div>
        );
    }
});

module.exports = AutoComplete;