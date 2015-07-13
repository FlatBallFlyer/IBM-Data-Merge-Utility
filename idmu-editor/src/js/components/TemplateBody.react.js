/**
 * @jsx React.DOM
 */

var BreadCrumb = React.createClass({
  handleClick: function() {
    this.props.bCB(this.props.data,this.props.index);
  },
  render: function(){
    return(<li><a onClick={this.handleClick} className="bread-crumb">{this.props.data.collection}.{this.props.data.name}.{this.props.data.columnName}</a></li>);
  }
});

var ContentEditable = React.createClass({
  render: function(){
    var level=this.props.level;
    var index=this.props.index;
    var this_ref = "contenteditable_"+level+"_"+index;
    id = "contenteditable"
    return(<div id={this_ref}
                bCB={this.props.bCB}
                className="ce_block"
                onInput={this.emitChange} 
                onBlur={this.emitChange}
                contentEditable
                dangerouslySetInnerHTML={{__html: this.props.html}}></div>);
  },
  shouldComponentUpdate: function(nextProps){
    var flag = ((nextProps.html !== this.getDOMNode().innerHTML) || (nextProps.update===true));
    return flag;
  },
  handleBookmarkClick: function(evt){
    if($(evt.target).prop('tagName')==='A'){
      this.props.bCB(evt);
    }
  },
  handleBookmarkRemove: function(evt){
    this.props.rCB(evt);
  },
  componentDidUpdate: function() {
    if (this.props.update || this.props.html !== this.getDOMNode().innerHTML) {
      var els = $.parseHTML(this.props.html);
      if(els){
        els.forEach(function(el){
          if($(el).prop('tagName') == "DIV" && $(el).attr("class") === "tkbookmark"){
            $(el).empty();
            var escd = $(el).prop('outerHTML').replace(Utils.htmlEncodeRegex(), function(i) {
              return '&#' + i.charCodeAt(0) + ';';
            });
            var tip = "collection="+$(el).attr("collection")+",name="+$(el).attr("name");
            if($(el).attr("column")) {
              tip+= ",column="+$(el).attr("column");
            }
            var a=$("<a contenteditable=\"false\" title=\""+tip+"\" class=\"toberemoved inline btn btn-primary btn-xs \" type=\"button\" value=\"tkBookmark\">tkBookmark&nbsp;|<span contenteditable=\"false\" class=\"glyphicon glyphicon-remove\"></span></a>");
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

    $(".toberemoved").bind("click",this.handleBookmarkClick);
    $(".glyphicon-remove").bind("click",this.handleBookmarkRemove);

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
    this.lastHtml = this.props.content;
    var state = {};
    state.content = this.props.content;
    state.crumbs = [];
    return state;
  },
  handleBookmarkRemove: function(evt){
    var this_ref = "contenteditable_"+this.props.level+"_"+this.props.index;
    var el = $(evt.target).parent().parent().remove();
    var cel = document.getElementById(this_ref);
    var html = $(cel).prop('innerHTML');
    var state = {content: html,update:true};
    this.setState(state);
  },
  handleBookmarkClick: function(evt){
    var el = $(evt.target).parent();
    var crumbs = this.state.crumbs;
    crumbs.push({collection: $(el).attr('collection'),
                 name: $(el).attr('name'),
                 columnName: $(el).attr('columnName')});
    this.setState({crumbs: crumbs});
  },
  componentWillReceiveProps: function(nextProps) {
    var state = {};
    state.content = nextProps.content;
    this.setState(state);
  },
  getLastHtml: function(){
    return this.lastHtml;
  },
  handleContentChange: function(evt){
    //this.setState({lastHtml: evt.target.value});
    //this.setState({content: this.lastHtml});
    this.lastHtml = evt.target.value;
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
  handleInsert: function(collection,name,columnName){
    var this_ref = "contenteditable_"+this.props.level+"_"+this.props.index;
    var el = document.getElementById(this_ref);
    var tip = "collection="+collection+"name="+name;
    if(columnName && columnName.length > 0){
      tip+=",column="+columnName;
    }
    var bkMark="<div class=\"tkbookmark\" contenteditable=\"false\"  collection=\""+collection+"\" name=\""+name+"\" columnName=\""+columnName+"\"></div>";
    this.pasteHtmlAtCaret(bkMark);
    el = document.getElementById(this_ref);

    var state = {content: $(el).prop('innerHTML'),update:true};
    this.setState(state);
  },
  handleBreadCrumbClick:function(opt,index){
    var crumbs=[];
    if(index < this.state.crumbs.length-1){
      crumbs = this.state.crumbs.slice(0,index+1);
      this.setState({crumbs: crumbs});
    }
  },
  render: function(){
    var title = "Insert Bookmark";
    var bCB = this.handleBreadCrumbClick;
    var level = this.props.level;
    var crumbs = this.state.crumbs.map(function(opt,i){
      var this_ref = "bread_crumb_"+level+"_"+i;
      return(<BreadCrumb key={i} ref={this_ref} level={level} index={i} bCB={bCB} data={opt}/>);
    });

    var index = this.props.index;
    var this_ref = "content_editable_"+level+"_"+index;
    var bk_ref = "insert_bkm_"+level+"_"+index;
    return(
      <div className="row no-margin ribbon-inside">
        <form>
          <div className="form-group col-xs-12 col-md-12">
            <div className="form-group col-xs-12 col-md-12">
              <ol className="breadcrumb">
                {crumbs}
              </ol>
            </div>
            <div className="form-group col-xs-12 col-md-12">
              <label for="name" className="control-label">Content</label>
              <ContentEditable html={this.state.content} ref={this_ref} level={level} index={index} id={this_ref} onChange={this.handleContentChange} update={this.state.update} bCB={this.handleBookmarkClick} rCB={this.handleBookmarkRemove}/>
            </div>
          </div>
        </form>
        <div>
          <div className="form-group col-xs-12 col-md-12">
            <InsertBookmarkTrigger ref={bk_ref} level={level} index={index} title={title} iCB={this.handleInsert}/>
          </div>            
        </div>
      </div>
    );
  }
});
