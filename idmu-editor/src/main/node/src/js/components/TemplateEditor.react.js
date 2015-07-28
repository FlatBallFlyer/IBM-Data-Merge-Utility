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
  hasInsertDirectives: function(){
    //2,10,21,31
    var data = this.props.data;
    var template = data.template;
    var found = false;
    for(var idx=0; idx < template.directives.length; idx++) {
      var type = parseInt(template.directives[idx].type);
      if(type === 2 || type === 10 || type === 21 || type === 31){
        found = true;
        break;
      }
    }
    return found;
  },
  bodyItems: function(){
    var data = this.props.data;
    var tpl = data.template;
    var items = data.template.items;
    var body_items = [];

    var index=this.props.index;
    var level=this.props.level;
    var sCB = this.handleSave;
    var hasInsertDirective = this.hasInsertDirectives();
    var ldirs = this.props.ldirs;
    if(items){
      body_items = items.map(function(opt,i){
        if(opt.type === 'text'){
          var this_ref = "template_body_"+level+"_"+(index+i);
          
          return(<TemplateBody sCB={sCB} index={index+i} level={level} key={index+i} ref={this_ref} data={data} content={opt.slice} hasInsertDirective={hasInsertDirective}/>);
          
        }else if(level < config("max_depth")){
          var el=$.parseHTML(opt.slice);
          var suppressNav = false;
          var collection = $(el).attr('collection');
          var name = $(el).attr('name');
          var colName = $(el).attr('column');
          
          if(opt.missingTemplate===true) {
            return(
              <div key={app_key} className="row no-margin col-xs-12 col-md-12">
                <h5 className="template-not-exist">Template {collection}.{name}. does not exist</h5>
              </div>
            );
          }else {
          
            if(!colName || colName.length === 0){
              suppressNav=true;
            }

            var app_ref = "app_"+level+"_"+(index+i);
            var selection = {collection:collection,name: name,columnValue:null};//colName};
            var app_key=collection+"."+name+"."+colName+"."+(level+1)+"."+(index+1);
            return(
              <App key={app_key} ref={app_key} level={level+1} index={index+i} selection={selection}  suppressNav={suppressNav} ldirs={ldirs}/>
            );
          }
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
    var rCB = this.props.rCB;
    var addTplCB = this.props.addTplCB;
    var removeTplCB = this.props.removeTplCB;
    var mergeTplCB = this.props.mergeTplCB;
    var index=this.props.index;
    var level=this.props.level;
    var this_ref = "template_header_"+level+"_"+index;
    var ldirs = this.props.ldirs;
   
    return(
      <div className="row">
        <TemplateHeader level={level} index={index} ref={this_ref} data={this.props.data} mCB={mCB} aCB={aCB} sCB={this.handleSave} dCB={dCB}  rCB={rCB} addTplCB={addTplCB} mergeTplCB={mergeTplCB} removeTplCB={removeTplCB} suppressNav={this.props.suppressNav} ldirs={ldirs}/>
        {this.bodyItems()}
      </div>
    );
  }
});

