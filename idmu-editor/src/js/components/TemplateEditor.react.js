/**
 * @jsx React.DOM
 */
var TemplateEditor = React.createClass({
  handleSave: function(opts) {
    var content_raw = this.refs.template_body.state.content,
        content = content_raw.replace(/<a(>|.*?[^?]>)/g,"");
    content = content.replace(/div class=\"tkbookmark\"/g,"tkBookmark");
    content = content.replace(/><\/div>/g,"/>");
    opts.content = content;
    this.props.sCB(opts);
  },
  bodyItems: function(){
    var data = this.props.data;
    var items = data.template.items;
    var body_items = [];
    if(items){
      body_items = items.map(function(opt,i){
        if(opt.type === 'text'){
          return(<TemplateBody key={i} ref="template_body" data={data} content={opt.slice}/>);
        }else{
          var el=$.parseHTML(opt.slice);
          var suppressNav = false;
          var colName = $(el).attr('column');
          if(colName && colName.length>0){
            suppressNav=true;
          }
          return(
              <App key={i} suppressCollection={true} suppressRibbon={false} suppressNav={suppressNav}/>
          );
        }
      });
    }
    return body_items;
  },
  render: function() {
    var mCB = this.props.mCB;
    var aCB = this.props.aCB;
    var sCB = this.props.sCB;
    var dCB = this.props.dCB;
    return(
      <div className="row">
        <TemplateHeader ref="template_header" data={this.props.data} mCB={mCB} aCB={aCB} sCB={this.handleSave} dCB={dCB}/>
        {this.bodyItems()}
      </div>
    );
  }
});

