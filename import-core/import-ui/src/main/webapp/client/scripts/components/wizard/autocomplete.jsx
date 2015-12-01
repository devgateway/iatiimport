var React = require('react');
var constants = require('./../../utils/constants');
var AutoComplete = React.createClass({
    componentDidMount: function () {
     if(this.props.context == constants.CHOOSE_PROJECTS){
    	 this.initializeProjectsAutocomplete();
     } else{
    	 this.initializeValuesAutocomplete();
     }
    },
    initializeProjectsAutocomplete: function(){
    	  var self = this;
    	  var language = this.props.language;
    	  var $el = $(this.refs[this.props.refId].getDOMNode());
          var searchEngine = new Bloodhound({
              datumTokenizer: function (d) {
                  return Bloodhound.tokenizers.whitespace(d.multilangFields.title[language]);
              },
              queryTokenizer: Bloodhound.tokenizers.whitespace,
              local: this.props.options
          });
          searchEngine.initialize();
          
          $el.typeahead(null, {
              name      : this.props.name,
              display: function(v){ return v.multilangFields.title[language];},
              source    : searchEngine.ttAdapter()
          });
          
         
          
          $el.bind('typeahead:selected', function (obj, datum, name) {           
              self.props.onSelect(datum);
          });

          if(this.props.value) {        	  
              $el.val(this.props.value.multilangFields.title[this.props.language]);
          }
    },
    initializeValuesAutocomplete: function(){
    	
    	var $el = $(this.refs[this.props.refId].getDOMNode());
    	var bloodhound = new Bloodhound({
    		limit: 10,
    		datumTokenizer: function(item) {
    		      return Bloodhound.tokenizers.whitespace(item.label);
    		  },
    		queryTokenizer: Bloodhound.tokenizers.whitespace,
    		local: this.props.options
    	});


    	bloodhound.initialize();
    	$el.typeahead({
    		hint: true,
    		highlight: true,
    		minLength: 1
    	},
    	{
    		name: 'values',
    		displayKey: function(item) {
    		    return item.label;        
    		},
    		source: bloodhound.ttAdapter(),
    		templates: {
    			empty: [
    			        '<div class="noitems">',
    			        ' No Items Found',
    			        '</div>'
    			        ].join('\n')
    		}
    	});

    	var self = this;
    	
    	$el.bind('typeahead:selected', function (obj, datum, name) {           
    		self.props.handleChange(self.props.data, datum.value);
    	});

    	if(this.props.value) {
    		$el.val(this.props.value);
    	}

    },
    componentDidUpdate: function(prevProps, prevState){
    	var $el = $(this.refs[this.props.refId].getDOMNode());
    	if(this.props.value) {
    		$el.val(this.props.value);
    	}
    },
    render: function () {       
        return (
            <div className="autocomplete">
                <input className="typeahead" placeholder={this.props.placeholder} ref={this.props.refId} type="text" />
            </div>
        );
    }
});

module.exports = AutoComplete;