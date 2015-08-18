/*
* Copyright 2015, 2015 IBM
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
*/
/**
 * @jsx React.DOM
 */
var TextEditMixin = {
  handleSelectionChange: function(event) {
    var state = $.extend(true, {}, this.state);
    this.resolve_set(event.target['id'],state,event.target.value);
    this.setState(state);
  },
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
      if( !schema[elem] ){schema[elem] = {};}
      schema = schema[elem];
    }
    schema[pList[len-1]] = value;
    
    return path.split('.').reduce( function( prev, curr ) {
      return prev[curr];
    }, obj || this );
  }
};
