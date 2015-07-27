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
var TemplateCollection = React.createClass({
  handleChange: function(event) {
    this.props.selectHandler(event.target.value);
  },
  render: function() {
    var
        data = this.props.data,
        selectedValue = data['selectedCollection'],
        collection = data.data;
    
    var options=collection.map(function(k,i){
      return (<option key={i} value={k.collection} label={k.collection}>{k.collection}</option>);
    });
    
    var id = "template-collection-"+this.props.level+"-"+this.props.index;
    return(
      <div className="row no-margin">
        <form className="form-vertical">
          <div className="form-group  col-sm-6">
            <label for={id} className="col-sm-4 control-label control-label-big">Collections</label>
            <div className="col-sm-12">
              <select id={id} className="form-control input-lg" onChange={this.handleChange} value={selectedValue}>
                {options}
              </select>
            </div>
          </div>
        </form>
      </div>
    );
  }
});
