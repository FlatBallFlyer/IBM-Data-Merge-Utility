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
var MergeTemplateTrigger = React.createClass({
  handleClick: function(e) {
    this.props.mergeTplCB(this.props.selection);
  },
  render: function() {
    return(
      <a role="button" aria-expanded="true">
        <span className="input-group-btn input-group-btn-directives">
          <button onClick={this.handleClick} type="button" className="btn">Merge</button>
        </span>
      </a>
    );
  }
});
