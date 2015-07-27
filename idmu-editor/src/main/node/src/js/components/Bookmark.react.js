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
var Tk = React.createClass({
  render: function(){
    return(<div><h3>Tk</h3></div>);
  }
});

var InsertBookmarkTrigger = React.createClass({
  handleClick: function(e) {
    var el = this.refs.bookmark_editor.getDOMNode();
    $(el).modal();
  },
  render: function() {
    var iCB = this.props.iCB;
    return(
      <div>
        <span className="btn btn-primary" onClick={this.handleClick}>Insert Template</span>
        <BookmarkEditor  key={this.props.title}  ref="bookmark_editor" title={this.props.title} iCB={iCB}/>
      </div>
    );
  }
});

var BookmarkEditor = React.createClass({
  mixins: [TextEditMixin,ModalMixin],
  getInitialState: function() {
    return({collection:'', name: '', columnName: ''});
  },
  handleSave: function(e) {
    this.props.iCB(this.state.collection,this.state.name,this.state.columnName);
  },
  render: function() {
    return (
      <div onClick={this.handleClick} className="modal" role="dialog" aria-hidden="true">
        <div className="modal-dialog">
          <div className="modal-content">
            <div className="modal-header">
              <h3>Insert Bookmark</h3>
            </div>
            <div className="modal-body">
              <div className="row">
                <div className="col-lg-12">
                  <form>
                    <div className="form-group col-xs-6 col-md-6">
                      <label for="description" className="control-label">Collection</label>
                      <input  onChange={this.handleTextEditChange} className="form-control" id="collection" type="text" value={this.state.collection}/>
                    </div>
                    <div className="form-group col-xs-6 col-md-6">
                      <label for="description" className="control-label">Name</label>
                      <input  onChange={this.handleTextEditChange} className="form-control" id="name" type="text" value={this.state.name}/>
                    </div>
                    <div className="form-group col-xs-6 col-md-6">
                      <label for="description" className="control-label">Column Name</label>
                      <input  onChange={this.handleTextEditChange} className="form-control" id="columnName" type="text" value={this.state.columnName}/>
                    </div>
                  </form>
                </div>
              </div>              
            </div>
            <div className="modal-footer">
              <button type="button" className="btn btn-default" data-dismiss="modal">Cancel</button>
              <button onClick={this.handleSave} type="button" data-dismiss="modal" className="btn btn-primary">Insert</button>
            </div>
          </div>
        </div>
      </div>
    );
  }
});
