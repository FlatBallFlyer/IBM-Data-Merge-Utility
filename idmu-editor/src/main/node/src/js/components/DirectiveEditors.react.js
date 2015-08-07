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

var DirectiveEditorsMixin = {
  getInitialState: function() {
    var state = $.extend(true, {}, this.props.directive);
    return state;
  },
  componentWillReceiveProps: function(nextProps) {
    var nextState = nextProps.directive;
    this.setState(nextState);
  },
  soft_fail: function() {
    var defaultChecked = this.state.softFail === true;
    return(
      <div className="form-group col-xs-12 col-md-12">
        <div className="checkbox">
          <label>
            <input id="softFail" type="checkbox" onChange={this.handleCheckboxChange} checked={defaultChecked}/>&nbsp;&nbsp;Soft fail?
          </label>
        </div>
      </div>
    );
  },
  description: function() {
    return(
      <div className="form-group col-xs-12 col-md-12">
        <label for="description" className="control-label">Description</label>
        <input  onChange={this.handleTextEditChange} className="form-control" id="description" type="text" value={this.state.description}/>
      </div>
    );
  },
  tags: function(){
    return (
      <div className="form-group col-xs-12 col-md-12">
        <label for="tags" className="control-label">Tags</label>
        <input  onChange={this.handleTextEditChange} className="form-control" id="tags" type="text"  value={this.state.tags}/>
      </div>
    );
  },
  from: function() {
    return(<div className="form-group col-xs-12 col-md-12">
            <label for="from" className="control-label">From</label>
            <input  onChange={this.handleTextEditChange} className="form-control" id="from" type="text" value={this.state.from}/>
    </div>);
  },
  to: function() {
    return(<div className="form-group col-xs-12 col-md-12">
            <label for="to" className="control-label">To</label>
            <input  onChange={this.handleTextEditChange} className="form-control" id="to" type="text" value={this.state.to}/>
    </div>);
  },
  collection_name: function() {
    return(
      <div className="form-group col-xs-6 col-md-6">
        <label for="collectionName" className="control-label">Collection Name</label>
        <input  onChange={this.handleTextEditChange} className="form-control" id="collectionName" type="text" value={this.state.collectionName}/>
      </div>);
  },
  collection_column: function() {
    return(
      <div className="form-group col-xs-6 col-md-6">
        <label for="collectionColumn" className="control-label">Collection Column</label>
        <input  onChange={this.handleTextEditChange} className="form-control" id="collectionColumn" type="text" value={this.state.collectionColumn}/>
      </div>
    );
  },
  not_last: function() {
    return(<div className="form-group col-xs-6 col-md-6">
            <label for="notLast" className="control-label">Not Last</label>
            <input  onChange={this.handleTextEditChange} className="form-control" id="notLast" type="text" value={this.state.notLast}/>
    </div>);
  },
  only_last: function() {
    return(<div className="form-group col-xs-6 col-md-6">
      <label for="onlyLast" className="control-label">Only Last</label>
      <input  onChange={this.handleTextEditChange} className="form-control" id="onlyLast" type="text" value={this.state.onlyLast}/>
    </div>);
  },
  from_column: function(){
    return(<div className="form-group col-xs-12 col-md-12">
            <label for="fromColumn" className="control-label">From Column</label>
            <input  onChange={this.handleTextEditChange} className="form-control" id="fromColumn" type="text" value={this.state.fromColumn}/>
    </div>);
  },
  to_column: function(){
    return(<div className="form-group col-xs-12 col-md-12">
            <label for="toColumn" className="control-label">To Column</label>
            <input  onChange={this.handleTextEditChange} className="form-control" id="toColumn" type="text" value={this.state.toColumn}/>
    </div>);
  },
  provider_tag: function(){
    return(<div className="form-group col-xs-6 col-md-6">
            <label for="provider.tag" className="control-label">Tag</label>
            <input  onChange={this.handleTextEditChange} className="form-control" id="provider.tag" type="text" value={this.state.provider.tag}/>
    </div>);
  },
  provider_value: function() {
    return(<div className="form-group col-xs-6 col-md-6">
            <label for="provider.value" className="control-label">Value</label>
            <input  onChange={this.handleTextEditChange} className="form-control" id="provider.value" type="text" value={this.state.provider.value}/>
    </div>);
  },
  provider_list: function() {
    return(<div className="form-group col-xs-6 col-md-6">
            <div className="checkbox">
              <label>
                <input id="provider.list" type="checkbox" onChange={this.handleCheckboxChange} checked={this.state.provider.list}/>&nbsp;&nbsp;List?
              </label>
            </div>
    </div>);
  },
  provider_source: function() {
    return(<div className="form-group col-xs-6 col-md-12">
            <label for="provider.source" className="control-label">Source</label>
            <input  onChange={this.handleTextEditChange} className="form-control" id="provider.source" type="text" value={this.state.provider.source}/>
    </div>);
  },
  provider_columns: function() {
    return(<div className="form-group col-xs-6 col-md-6">
            <label for="provider.columns" className="control-label">Columns</label>
            <input  onChange={this.handleTextEditChange} className="form-control" id="provider.columns" type="text" value={this.state.provider.columns}/>
    </div>);
  },
  provider_from: function(){
    return(<div className="form-group col-xs-6 col-md-6">
            <label for="provider.from" className="control-label">From</label>
            <input  onChange={this.handleTextEditChange} className="form-control" id="provider.from" type="text" value={this.state.provider.from}/>
    </div>);
  },
  provider_where: function() {
    return(<div className="form-group col-xs-6 col-md-6">
            <label for="provider.where" className="control-label">Where</label>
            <input  onChange={this.handleTextEditChange} className="form-control" id="provider.where" type="text" value={this.state.provider.where}/>
    </div>);
  },
  provider_condition: function() {
    return(<div className="form-group col-xs-6 col-md-12">
            <label for="provider.condition" className="control-label">Condition</label>
            <input  onChange={this.handleTextEditChange} className="form-control" id="provider.condition" type="text" value={this.state.provider.condition}/>
    </div>);
  },
  provider_static_data: function() {
    return(<div className="form-group col-xs-6 col-md-12">
            <label for="provider.staticData" className="control-label">Static Data</label>
            <textarea  rows={3} onChange={this.handleTextEditChange} className="form-control" id="provider.staticData" type="text" value={this.state.provider.staticData}/>
    </div>);
  },
  provider_url: function(){        
    return(<div className="form-group col-xs-6 col-md-6">
            <label for="provider.url" className="control-label">URL</label>
            <input  onChange={this.handleTextEditChange} className="form-control" id="provider.url" type="text" value={this.state.provider.url}/>
    </div>);
  }
};

/*
  {
    "type":0,Tags
    "name": "Require Tags",
    "description":"Directive Description (Optional)",
    "softFail":true, //|false
    "tags":["Tag1"],
   }
 */
var RequireTags = React.createClass({
  mixins: [TextEditMixin,DirectiveEditorsMixin],
  render: function() {
    return (
      <div>
        <form>
          {this.description()}
          {this.tags()}
          {this.soft_fail()}
        </form>
      </div>
    );
  }
});


/*
{
   "type":1
   "name": "Replace Value",
   "description":Directive Description (Optional),
   "softFail":true|falseDirective level Soft Fail Indicator,
   "from":The value to replace (will be wrapped in {brackets}),
   "to":The value to replace with
}
*/
var ReplaceValue = React.createClass({
  mixins: [TextEditMixin,DirectiveEditorsMixin],
  render: function() {
    return (
      <div>
        <form>
          {this.description()}
          {this.from()}
          {this.to()}
          {this.soft_fail()}
        </form>
      </div>
    );
  }          
});


/*
{
   "sequence": 1,
   "type": 2
   "name": "Insert Template From Tag Data",
   "softFail": false,
   "description": "TestInsertSubsTag",
   "collectionName": "",
   "collectionColumn": "",
   "notLast": ["empty"],
   "onlyLast": [],
   "provider": {
      "type": 2,
      "condition": 0,
      "tag": "Foo",
      "value": "",
      "list": false
   }
}
*/
var InsertSubTemplatesFromTagData = React.createClass({
  mixins: [TextEditMixin,DirectiveEditorsMixin],
  render: function() {
    return (
      <div>
        <form>
          {this.description()}
          {this.not_last()}          
          {this.only_last()}
          {this.provider_condition()}
          {this.provider_tag()}
          {this.provider_value()}
          {this.provider_list()}
          {this.soft_fail()}
        </form>
      </div>
    );
  }
});


/*
{
   "sequence": 1,
   "type": 10
   "name": "Insert Template From SQL Rows",
   "softFail": false,
   "description": "TestInsertSubsSql",
   "collectionName": "",
   "collectionColumn": "",
   "notLast": ["empty"],
   "onlyLast": [],
   "provider": {
     "type": 1,
     "source": "",
     "columns": "A,B,C,1,2,3,4,5,6",
     "from": "",
     "where": ""
    }
}
*/
var InsertSubTemplatesFromSQLRows = React.createClass({
  mixins: [TextEditMixin,DirectiveEditorsMixin],
  render: function() {
    return (<div>
        <form>
          {this.description()}
          {this.not_last()}
          {this.only_last()}
          {this.provider_source()}
          {this.provider_columns()}
          {this.provider_from()}
          {this.provider_where()}          
          {this.soft_fail()}
        </form>
    </div>
    );
  }
});

/*
{
  "sequence": 1,
  "type": 11
  "name": "Replace From SQL Row",
  "softFail": false,
  "description": "TestReplaceRowSql",
  "provider": {
    "type": 1,
    "source": "",
    "columns": "A,B,C,1,2,3,4,5,6",
    "from": "",
    "where": ""
  }
}
*/
var ReplaceValuesFromSQLRow = React.createClass({
  mixins: [TextEditMixin,DirectiveEditorsMixin],
  render: function() {
    return (<div>
        <form>
          {this.description()}
          {this.provider_source()}
          {this.provider_columns()}
          {this.provider_from()}
          {this.provider_where()}
          {this.soft_fail()}
        </form>
    </div>
    );
  }
});


/*
{
   "sequence": 1,
   "type": 12
   "name": "Replace From SQL Columns",
   "softFail": false,
   "description": "TestReplaceColSql",
   "fromColumn": "Foo",
   "toColumn": "",
   "provider": {
   "type": 1,
   "source": "",
   "columns": "A,B,C,1,2,3,4,5,6",
   "from": "",
   "where": ""
   }
}
*/
var ReplaceValuesFromSQLColumn = React.createClass({
  mixins: [TextEditMixin,DirectiveEditorsMixin],
  render: function() {
    return (<div>
        <form>
          {this.description()}
          {this.from_column()}
          {this.to_column()}
          {this.provider_source()}
          {this.provider_columns()}
          {this.provider_from()}
          {this.provider_where()}
          {this.soft_fail()}
        </form>
    </div>
    );
  }
});


/*
{
   "sequence": 1,
   "type": 21"
   "name": "Insert Templates From CSV",
   "softFail": false,
   "description": "TestInsertSubsCsv",
   "collectionName": "",
   "collectionColumn": "",
   "notLast": ["empty"],
   "onlyLast": [],
   "provider": {
   "type": 3,
   "staticData": "A,B,C\n1,2,3\n4,5,6",
   "url": "",
   "tag": ""
   }
}
*/
var InsertSubTemplatesFromCSV = React.createClass({
  mixins: [TextEditMixin,DirectiveEditorsMixin],
  render: function() {
    return (<div>
      <form>
        {this.description()}
        {this.not_last()}
        {this.only_last()}
        {this.provider_static_data()}
        {this.provider_url()}
        {this.provider_tag()}          
        {this.soft_fail()}
        </form>
    </div>
    );
  }
});


/*
{
   "sequence": 1,
   "type": 22
   "name": "Replace From CSV Row",
   "softFail": false,
   "description": "TestReplaceRowCsv",
   "provider": {
   "type": 3,
   "staticData": "A,B,C\n1,2,3\n4,5,6",
   "url": "",
   "tag": ""
   }
}
*/
var ReplaceValuesFromCSVRow = React.createClass({
  mixins: [TextEditMixin,DirectiveEditorsMixin],
  render: function() {
    return (<div>
      <form>
        {this.description()}
        {this.provider_static_data()}
        {this.provider_url()}
        {this.provider_tag()}
        {this.soft_fail()}
      </form>
    </div>
    );
  }
});


/*
{
   "sequence": 1,
   "type": 23
   "name": "Replace From CSV Column",
   "softFail": false,
   "description": "TestReplaceColCsv",
   "fromColumn": "Foo",
   "toColumn": "",
   "provider": {
   "type": 3,
   "staticData": "A,B,C\n1,2,3\n4,5,6",
   "url": "",
   "tag": ""
   }
}
*/
var ReplaceValuesFromCSVColumn = React.createClass({
  mixins: [TextEditMixin,DirectiveEditorsMixin],
  render: function() {
    return (<div>
        <form>
          {this.description()}
          {this.from_column()}
          {this.to_column()}
          {this.provider_static_data()}
          {this.provider_url()}
          {this.provider_tag()}
          {this.soft_fail()}
        </form>
    </div>
    );
  }
});


/*
{
   "sequence": 1,
   "type": 31,
   "name": "Insert Template From HTML Table",
   "softFail": false,
   "collectionName": "",
   "collectionColumn": "",
   "notLast": ["empty"],
   "onlyLast": [],
   "description": "TestInsertSubsHtml",
   "provider": {
   "type": 4,
   "staticData": "A,B,C\n1,2,3\n4,5,6",
   "url": "",
   "tag": ""
   }
}
*/
var InsertSubTemplatesFromHTMLTableRows = React.createClass({
  mixins: [TextEditMixin,DirectiveEditorsMixin],
  render: function() {
    return (<div>
      <form>
        {this.description()}
        {this.not_last()}
        {this.only_last()}
        {this.provider_static_data()}
        {this.provider_url()}
        {this.provider_tag()}
        {this.soft_fail()}
      </form>
    </div>
    );
  }
});


/*
{
   "sequence": 1,
   "type": 32
   "name": "Replace From HTML Rows",
   "softFail": false,
   "description": "TestReplaceRowHtml",
   "provider": {
   "type": 4,
   "staticData": "A,B,C\n1,2,3\n4,5,6",
   "url": "",
   "tag": ""
   }
}
*/
var ReplaceValuesFromHTMLTableRows = React.createClass({
  mixins: [TextEditMixin,DirectiveEditorsMixin],
  render: function() {
    return (<div>
      <form>
        {this.description()}
        {this.provider_static_data()}
        {this.provider_url()}
        {this.provider_tag()}
        {this.soft_fail()}
      </form>
    </div>
    );
  }
});


/*
{
   "sequence": 1,
   "type": 33,
   "name": "Replace From HTML Column",
   "softFail": false,
   "description": "TestReplaceColHtml",
   "fromColumn": "Foo",
   "toColumn": "",
   "provider": {
   "type": 4,
   "staticData": "A,B,C\n1,2,3\n4,5,6",
   "url": "",
   "tag": ""
   }
}
*/
var ReplaceValuesFromHTMLTableColumn = React.createClass({
  mixins: [TextEditMixin,DirectiveEditorsMixin],
  render: function() {
    return (<div>
      <form>
        {this.description()}
        {this.from_column()}
        {this.to_column()}
        {this.provider_static_data()}
        {this.provider_url()}
        {this.provider_tag()}
        {this.soft_fail()}
      </form>
    </div>
    );
  }
});

/*
{
   "sequence": 1,
   "type": 34,
   "name": "Replace From HTML Pattern",
   "softFail": false,
   "description": "TestMarkupSubsHtml",
   "pattern": "TestPattern",
   "provider": {
   "type": 4,
   "staticData": "A,B,C\n1,2,3\n4,5,6",
   "url": "",
   "tag": ""
   }
}
*/
var ReplaceValuesFromHTML = React.createClass({
  render: function() {
    return (false);
  }
});

