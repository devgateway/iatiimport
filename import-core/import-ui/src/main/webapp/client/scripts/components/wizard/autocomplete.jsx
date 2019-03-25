var _ = require('lodash/dist/lodash.underscore');
var React = require('react');
var constants = require('./../../utils/constants');
var AutoComplete = React.createClass({
    componentDidMount: function () {
     if(this.props.context == constants.CHOOSE_PROJECTS){
    	 this.initializeProjectsAutocomplete();
     } else if(this.props.context == constants.SELECT_DATASOURCE) {
         this.initializeRemoteAutocomplete();
     }  else{
    	 this.initializeValuesAutocomplete();
     }
    },
    removeAccents: function(str) {
       if (str) {
           return str.normalize('NFD').replace(/[\u0300-\u036f]/g, '');
       }
       
       return str;
    },
    initializeProjectsAutocomplete: function(){
    	  var self = this;
    	  var language = this.props.language;
    	  var $el = $(this.refs[this.props.refId].getDOMNode());
          var searchEngine = new Bloodhound({
              limit: 100,
              datumTokenizer: function (d) {
                  var title = d.multilangFields.title[language];
                  return Bloodhound.tokenizers.whitespace(self.removeAccents(title));
              },
              queryTokenizer: function(query) {
                  return Bloodhound.tokenizers.whitespace(self.removeAccents(query));
              }, 
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
        var self = this;
    	var $el = $(this.refs[this.props.refId].getDOMNode());
    	var bloodhound = new Bloodhound({
        limit: 100,
    		datumTokenizer: function(item) {
    		     return Bloodhound.tokenizers.whitespace(self.removeAccents(item.label));
    		  },    		 
    		queryTokenizer: function(query) {
    		     return Bloodhound.tokenizers.whitespace(self.removeAccents(query));
    		 },    		   
    		local: this.props.options
    	});


    	bloodhound.initialize();
    	$el.typeahead({
    		hint: true,
    		highlight: false,
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

      $el.bind('typeahead:selected', function (obj, datum, name) {
    		self.props.handleChange(self.props.data, datum.value);
    	});
      $el.bind('typeahead:autocompleted', function (obj, datum, name) {
        self.props.handleChange(self.props.data, datum.value);
    	});


        $el.bind("paste", $.proxy(function(e){
           e.preventDefault();
            //var pastedData = e.originalEvent.clipboardData.getData('text');
            //var option = _.find(this.props.options, { 'label':  pastedData});
            //$(e.target).trigger('typeahead:selected', option);

        }, this));

    	if (this.props.value) {
    		$el.val(this.props.value);
    	} else {
    	   $el.val('');
    	}

    },
    initializeRemoteAutocomplete: function(){
        var $el = $(this.refs[this.props.refId].getDOMNode());
        var bloodhound = new Bloodhound({
        limit: 100,
        datumTokenizer: function(item) {
                  return Bloodhound.tokenizers.whitespace(item.name);
         },
            queryTokenizer: Bloodhound.tokenizers.whitespace,
            remote: {
                url: '/importer/data-source/reporting-orgs/%QUERY',
                wildcard: '%QUERY'
              }
        });


        bloodhound.initialize();
        $el.typeahead({
            hint: true,
            highlight: true,
            minLength: 4
        },
        {
            name: 'autocomplete',
            displayKey: function(item) {
                return item.name;
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
            self.props.onSelect(datum);
      });
      $el.bind('typeahead:autocompleted', function (obj, datum, name) {
        self.props.onSelect(datum);
       });


        $el.bind("paste", $.proxy(function(e){
           e.preventDefault();
         }, this));

        if (this.props.value) {
            $el.val(this.props.value);
        } else {
           $el.val('');
        }
    },
    componentDidUpdate: function(prevProps, prevState){
    	var $el = $(this.refs[this.props.refId].getDOMNode());
    	if (this.props.value) {
    		$el.val(this.props.value);
    	} else {
    	   $el.val('');
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
