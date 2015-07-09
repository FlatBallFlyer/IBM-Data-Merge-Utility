/**
 * @jsx React.DOM
 */

var InsertBookmarkTrigger = React.createClass({
  handleClick: function(e) {
    console.log("open modal");
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
              <button onClick={this.handleSave} type="button" className="btn btn-primary">Insert</button>
            </div>
          </div>
        </div>
      </div>
    );
  }
});

var ContentEditable = React.createClass({
    render: function(){
      return(<div id="contenteditable"
                  className="ce_block"
            onInput={this.emitChange} 
            onBlur={this.emitChange}
            contentEditable
            dangerouslySetInnerHTML={{__html: this.props.html}}></div>);
    },

    shouldComponentUpdate: function(nextProps){
        return nextProps.html !== this.getDOMNode().innerHTML;
    },
    componentDidUpdate: function() {
        if ( this.props.html !== this.getDOMNode().innerHTML ) {
          var els = $.parseHTML(this.props.html);
          if(els){
            els.forEach(function(el){
              if($(el).prop('tagName') == "DIV" && $(el).attr("class") === "tkbookmark"){
                $(el).empty();
                var escd = $(el).prop('outerHTML').replace(/[\u00A0-\u9999<>\&]/gim, function(i) {
                  return '&#' + i.charCodeAt(0) + ';';
                });
                var tip = "collection="+$(el).attr("collection")+",name="+$(el).attr("name");
                if($(el).attr("column")) {
                  tip+= ",column="+$(el).attr("column");
                }
                var a=$("<input title=\""+tip+"\" class=\"toberemoved inline btn btn-warning btn-xs\" type=\"button\" value=\"tkBookmark\"/>");
                $(el).append(a);
              }
            });
            var final_el = $("<div/>");
            els.forEach(function(el){
              $(final_el).append(el);
            });
            this.getDOMNode().innerHTML = final_el.prop('innerHTML');//this.props.html;
          }else {
            this.getDOMNode().innerHTML = this.props.html;
          }
        }
    },
    emitChange: function(){
        var html = this.getDOMNode().innerHTML;
        if (this.props.onChange && html !== this.lastHtml) {
          this.props.onChange({target: {value: html}});
        }
        this.lastHtml = html;
    }
});

var TemplateBody = React.createClass({
  mixins: [TextEditMixin],
  getInitialState: function() {
    return $.extend(true, {}, this.props.data.template);
  },
  componentWillReceiveProps: function(nextProps) {
    this.setState(nextProps.data.template);
  },
  handleContentChange: function(evt){
    this.lastHtml = evt.target.value;
    //console.log(this.lastHtml);
    this.setState({content: this.lastHtml});
  },
  pasteHtmlAtCaret: function(html) {
    /*taken from stackexchange http://stackoverflow.com/a/6691294 */
    var sel, range;
    if (window.getSelection) {
      sel = window.getSelection();
      if (sel.getRangeAt && sel.rangeCount) {
        range = sel.getRangeAt(0);
        range.deleteContents();
        var el = document.createElement("div");
        el.innerHTML = html;
        var frag = document.createDocumentFragment(), node, lastNode;
        while ( (node = el.firstChild) ) {
          lastNode = frag.appendChild(node);
        }
        var firstNode = frag.firstChild;
        range.insertNode(frag);

        // Preserve the selection
        if (lastNode) {
          range = range.cloneRange();
          range.setStartAfter(lastNode);
          range.collapse(true);
          sel.removeAllRanges();
          sel.addRange(range);
        }
      }
    }
  },
  handleInsert: function(colllection,name,columnName){
    var el = document.getElementById('contenteditable');
    el.focus();
    var tip = "collection="+collection+"name="+name;
    if(columnName && columnName.length > 0){
      tip+=",column="+columnName;
    }
    var bkMark="<div class=\"tkbookmark\" collection=\""+collection+"\" name=\""+name+"\" columnName=\""+columnName+"\"><input title=\""+tip+"\" class=\"toberemoved inline btn btn-warning btn-xs\" type=\"button\" value=\"tkBookmark..\"/></div>";
    this.pasteHtmlAtCaret(bkMark);
    this.setState({content: $(el).prop('innerHTML')});
  },
  render: function(){
    var title = "Insert Bookmark";
    return(
      <div className="row no-margin      ribbon-inside">
        <form>
          <div className="form-group col-xs-12 col-md-12">
            <div className="form-group col-xs-12 col-md-12">
              <label for="name" className="control-label">Content</label>
                <ContentEditable html={this.state.content} id="content" onChange={this.handleContentChange}/>
            </div>
          </div>
        </form>
        <div>
          <div className="form-group col-xs-12 col-md-12">
            <InsertBookmarkTrigger title={title} iCB={this.handleInsert}/>
          </div>            
        </div>
      </div>
    );
  }
});
