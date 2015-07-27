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

var ContentEditable = React.createClass({
  mixins: [TextEditMixin],
  getInitialState: function() {
    var level=this.props.level;
    var index=this.props.index;
    var this_ref = "contenteditable_"+level+"_"+index;
    var state={};
    state[this_ref]=this.props.content;
    return(state);
  },
  componentWillReceiveProps: function(nextProps) {
    var level=this.props.level;
    var index=this.props.index;
    var this_ref = "contenteditable_"+level+"_"+index;
    var state = {};
    state[this_ref] = nextProps.content;
    this.setState(state);
  },
  render: function(){
    var level=this.props.level;
    var index=this.props.index;
    var this_ref = "contenteditable_"+level+"_"+index;
    return(<textarea id={this_ref}
                     rows={3}
                     className="form-control" 
                     onChange={this.handleTextEditChange}
                     value={this.state[this_ref]}/>);
  }
});

var TemplateBody = React.createClass({
  mixins: [TextEditMixin],
  getInitialState: function() {
    var state = {};
    state.content = this.props.content;
    return state;
  },
  getText: function(){
    var level = this.props.level;
    var index = this.props.index;
    var this_ref = "#contenteditable_"+level+"_"+index;
    return($(this_ref).val()); 
 },
  componentWillReceiveProps: function(nextProps) {
    var state = {};
    state.content = nextProps.content;
    this.setState(state);
  },
  pasteTextAtCaret: function(text) {
    var this_ref = "#contenteditable_"+this.props.level+"_"+this.props.index;
    var cursorPos = $(this_ref).prop('selectionStart'),
        v = $(this_ref).val(),
        textBefore=v.substring(0,cursorPos),
        textAfter=v.substring(cursorPos,v.length),
        final_text = textBefore+text+textAfter;
    $(this_ref).val(final_text);
    return(final_text);
  },
  handleInsert: function(collection,name,columnName){
    var bkMark="<tkBookmark class=\"tkbookmark\" collection=\""+collection+"\" name=\""+name+"\" columnName=\""+columnName+"\" />";
    var final_text = this.pasteTextAtCaret(bkMark);
    var state = {content: final_text};
    this.setState(state);
  },
  heading: function(){
    if(this.props.index === 0){
      return(<label for="name" className="control-label">Content</label>);
    }else{
      return(false);
    }
  },
  insertBookmarkTrigger: function(){
    var title = "Insert Bookmark";
    var level = this.props.level;
    var index = this.props.index;
    var bk_ref = "insert_bkm_"+level+"_"+index;
    if(this.props.hasInsertDirective){
      return(
        <div className="col-xs-12 col-md-12">
          <InsertBookmarkTrigger ref={bk_ref} level={level} index={index} title={title} iCB={this.handleInsert}/>
        </div>
      );
    }else{
      return(false);
    }
  },
  render: function(){
    var level = this.props.level;
    var index = this.props.index;
    var this_ref = "content_editable_"+level+"_"+index;
    return(
      <div className="row no-margin ribbon-inside">
        <form>
          <div className="form-group col-xs-12 col-md-12">
            {this.heading()}
            <ContentEditable content={this.state.content} ref={this_ref} level={level} index={index} id={this_ref}/>
          </div>
        </form>
        {this.insertBookmarkTrigger()}
      </div>
    );
  }
});
