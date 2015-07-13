/**
 * @jsx React.DOM
 */
var TemplateEditor = React.createClass({
  handleSave: function(opts) {
    var data = this.props.data;
    var items = data.template.items;
    var self = this;
    var content_raw = items.map(function(b,i){
      var this_ref = "template_body_"+self.props.level+"_"+i;
      if(self.refs[this_ref]){
        return self.refs[this_ref].getLastHtml();
      }
    }).join('');
    opts.content = Utils.prepareContentForSave(content_raw);
    this.props.sCB(opts);
  },
  bodyItems: function(){
    var data = this.props.data;
    var items = data.template.items;
    var body_items = [];

    var level=this.props.level;
    
    if(items){
      body_items = items.map(function(opt,i){
        if(opt.type === 'text'){
          var this_ref = "template_body_"+level+"_"+i;
          return(<TemplateBody index={i} level={level} key={i} ref={this_ref} data={data} content={opt.slice}/>);
        }else{
          var el=$.parseHTML(opt.slice);
          var suppressNav = false;
          var colName = $(el).attr('column');
          if(colName && colName.length>0){
            suppressNav=true;
          }

          var this_ref = "app_"+level+"_"+i;
          return(
            <App key={i} ref={this} level={level+1} index={i} suppressCollection={true} suppressRibbon={false} suppressNav={suppressNav}/>
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
    var index=this.props.index;
    var level=this.props.level;
    var this_ref = "template_header_"+level+"_"+index;
   
    return(
      <div className="row">
        <TemplateHeader level={level} index={index} ref={this_ref} data={this.props.data} mCB={mCB} aCB={aCB} sCB={this.handleSave} dCB={dCB}/>
        {this.bodyItems()}
      </div>
    );
  }
});

