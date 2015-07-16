/**
 * @jsx React.DOM
 */
var TextEditMixin = {
  handleTextEditChange: function(event) {
    var state = $.extend(true, {}, this.state);
    this.resolve_set(event.target['id'],state,event.target.value);
    this.setState(state);
  },
  handleCheckboxChange: function(event) {
    var state = $.extend(true, {}, this.state);;
    this.resolve_set(event.target['id'],state,event.target.checked);
    this.setState(state);
  },
  resolve_set: function(path, obj,value) {
    var schema = obj;  // a moving reference to internal objects within obj
    var pList = path.split('.');
    var len = pList.length;
    for(var i = 0; i < len-1; i++) {
      var elem = pList[i];
      if( !schema[elem] ) schema[elem] = {}
      schema = schema[elem];
    }
    schema[pList[len-1]] = value;
    
    return path.split('.').reduce( function( prev, curr ) {
      return prev[curr];
    }, obj || this );
  }
};
