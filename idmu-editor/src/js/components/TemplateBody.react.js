/**
 * @jsx React.DOM
 */

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
  handleInsert: function(){
    var el = document.getElementById('contenteditable');
    el.focus();
    var tip = "collection=blah,blah blah";
    var bkMark="<div class=\"tkbookmark\" collection=\"mame\" name=\"myname\"><input title=\""+tip+"\" class=\"toberemoved inline btn btn-warning btn-xs\" type=\"button\" value=\"tkBookmark..\"/></div>";
    this.pasteHtmlAtCaret(bkMark);
    this.setState({content: $(el).prop('innerHTML')});
  },
  render: function(){
    return(
      <div className="row no-margin      ribbon-inside">
        <form>
          <div className="form-group col-xs-12 col-md-12">
            <div className="form-group col-xs-12 col-md-12">
              <label for="name" className="control-label">Content</label>
                <ContentEditable html={this.state.content} id="content" onChange={this.handleContentChange}/>
            </div>
            <div className="form-group col-xs-12 col-md-12">
              <input onClick={this.handleInsert} type="button" className="btn btn-primary" value="Insert Template"/>
            </div>            
          </div>
        </form>
      </div>);
  }
});
