/**
 * @jsx React.DOM
 */
var TemplateEditor = React.createClass({
  handleSave: function() {
    var data = this.props.data;
    var items = data.template.items;
    var self = this;
    var content_raw = items.map(function(b,i){
      if(b.type === 'text') {
        var this_ref = "template_body_"+self.props.level+"_"+i;
        if(self.refs[this_ref]){
          return self.refs[this_ref].getText();
        }
      }else if(b.type === 'bookmark') {
        return(b.slice);
      }
    }).join('');

    var index=this.props.index;
    var level=this.props.level;
    var header_ref = "template_header_"+level+"_"+index;
    
    var opts = this.refs[header_ref].getHeaderValues();
    opts.content = Utils.prepareContentForSave(content_raw);
    this.props.sCB(opts);
  },
  bodyItems: function(){
    var data = this.props.data;
    var items = data.template.items;
    var body_items = [];

    var level=this.props.level;
    var sCB = this.handleSave
    if(items){
      body_items = items.map(function(opt,i){
        if(opt.type === 'text'){
          var this_ref = "template_body_"+level+"_"+i;
          return(<TemplateBody sCB={sCB} index={i} level={level} key={i} ref={this_ref} data={data} content={opt.slice}/>);
        }else if(level < config("max_depth")){
          var el=$.parseHTML(opt.slice);
          var suppressNav = false;
          var collection = $(el).attr('collection');
          var name = $(el).attr('name');
          var colName = $(el).attr('column');
          
          if(colName && colName.length>0){
            suppressNav=true;
          }

          var this_ref = "app_"+level+"_"+i;
          var selection = {collection:collection,name: name,colValue:colName};
          return(
            <App key={i} ref={this} level={level+1} index={i} selection={selection}  suppressNav={suppressNav}/>
          );
        }else {
          return(<div><h5>Exceeded max sub-template depth.</h5></div>);
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

