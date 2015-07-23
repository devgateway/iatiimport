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
        var language = this.props.language;
        $el.typeahead(null, {
            name      : this.props.name,
            display: function(v){ return v.fields.title[language];},
            source    : searchEngine.ttAdapter()
        });
        
        var self = this;
        
        $el.bind('typeahead:selected', function (obj, datum, name) {           
            self.props.onSelect(datum);
        });

        if(this.props.value) {
            $el.val(this.props.value.title);
        }
    },
    render: function () {
        var language = this.props.language;
        var value = this.props.value ? this.props.value.fields.title[language] : undefined;
        return (
            <div className="autocomplete">
                <input className="typeahead" placeholder={this.props.placeholder} ref={this.props.refId} type="text" />
            </div>
        );
    }
});

module.exports = AutoComplete;