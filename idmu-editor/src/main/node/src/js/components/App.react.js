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
var App = React.createClass({
  
  getInitialState: function() {
    return {
      selectedCollection: null, //"root",
      selectedRibbonItem: null,
      selectedRibbonIndex: 0,
      data: [],
      template: {'directives':[]}
    };
  },
  initRouter: function () {
    var routes = {
    };
    window.router = Router(routes);
    window.router.configure({html5history: supports_history_api()});
    window.router.init();
  },
  componentDidMount: function () {
    if(this.props.level === 0){
      this.initRouter();
    }
    this.loadDirectivesFromServer();
    this.loadCollectionsFromServer();
  },
  handleCollectionSelected: function(selectedCollection) {
    this.loadTemplatesFromServer(selectedCollection);
  },
  handleRibbonSelected: function(selectedRibbonIndex,selectedRibbonItem) {
    var collection = this.props.selection ? this.props.selection.collection : this.state.selectedCollection;
    this.loadTemplateFromServer(this.state.templates,
                                selectedRibbonIndex,
                                collection,
                                selectedRibbonItem);
  },
  handleAddTemplate: function(newTpl){
    this.addNewTemplateToServer(newTpl);
  },
  handleRemoveTemplate: function(tpl){
    this.removeTemplateToServer(tpl);
  },
  handleAddSubTemplate: function(sTpl){
    return(this.addSubTemplateToServer(sTpl));
  },
  handleMergeTemplate: function(tpl){
    var colValue = (tpl.columnValue && tpl.columnValue.length>0)?tpl.columnValue : "";
    var templateName = tpl.collection + "." + tpl.name + "." + colValue;
    var url = '/idmu/merge?DragonFlyFullName='+templateName;
    window.open(url,'IDMU-Merge');
  },
  loadCollectionsFromServer: function() {
    var params = {};
    $.ajax({
      url: '/idmu/collections',
      dataType: 'json',
      cache: false,
      data: params,
      success: function(data) {
        var final_list = data;
        var selectedCollection = this.props.selection ? this.props.selection.collection : (this.state.selectedCollection || final_list[0].collection);
        this.setState({data: final_list,selectedCollection: selectedCollection},function(){
          this.loadTemplatesFromServer(this.state.selectedCollection);
        }.bind(this));
      }.bind(this),
      error: function(xhr, status, err) {
        console.error(this.props.url, status, err.toString());
      }.bind(this)
    });
  },
  loadTemplatesFromServer: function(collection) {
    var params = {};
    var self = this;
    var sfx = self.props.level !== 0 ? "/"+self.props.selection.name : "";
    $.ajax({
      url: '/idmu/templates/'+collection+sfx,
      dataType: 'json',
      cache: false,
      data: params,
      success: function(data) {
        if(self.props.level !== 0 && data.length == 0){
          data.push({collection: collection, name: self.props.selection.name});
        }
        var sel = self.props.selection || data[0];
        var selectedRibbonIndex = self.props.level == 0 ? 0 : this.state.selectedRibbonIndex;
        self.loadTemplateFromServer(data,
                                    selectedRibbonIndex,
                                    sel.collection,
                                    sel);
      }.bind(this),
      error: function(xhr, status, err) {
        console.error(this.props.url, status, err.toString());
      }.bind(this)
    });
  },
  loadDirectivesFromServer: function() {
    var params = {};
    $.ajax({
      url: '/idmu/directives',
      dataType: 'json',
      cache: false,
      data: params,
      success: function(data) {
        this.directives = data;
      }.bind(this),
      error: function(xhr, status, err) {
        console.error(this.props.url, status, err.toString());
      }.bind(this)
    });
  },
  buildBodyItems: function(content){
    var result=[];
    var reg=new RegExp(Utils.tkBookmarkRegex());
    var items=[];
    var sIdx = 0;
    var eIdx = 0;
    while((result = reg.exec(content)) !== null) {
      eIdx = content.indexOf("/>",result.index);
      if(eIdx!=-1){
        var slice1 = content.slice(sIdx,result.index);
        var slice2 = content.slice(result.index,eIdx+2);
        sIdx = eIdx+2;

        var el=$.parseHTML(slice2);
        var collection = $(el).attr('collection');
        var name = $(el).attr('name');

        items.push({type: 'text',slice: slice1});
        var bk = {type: 'bookmark',slice: slice2};
        items.push(bk);
      }
    }

    var slice = content.slice(sIdx);
    if(slice && slice.length > 0){
      items.push({type: 'text', slice: slice});
    }else{
      if(content.length == 0){
        items.push({type: 'text', slice: ""});
      }
    }
    return items;
  },
  templateExist: function(collection,name){
    var url = '/idmu/template/'+encodeURIComponent(collection+'.'+name)+"./";
    var exist = false;
    $.ajax({
      async: false,
      url: url,
      dataType: 'json',
      cache: false,
      success: function(data) {
        exist = true;
      }.bind(this),
      error: function(xhr, status, err) {
        exist = false;
      }.bind(this)
    });
    return exist;
  },
  fixDirectives: function(in_dxs){
    var dxs = in_dxs;
    for(var i=0; i<dxs.length; i++){
      var dx = dxs[i];
      if(dx.tags && dx.tags.length > 0){
        dx.tags = dx.tags.join(",");
      }else {
        dx.tags = "";
      }

      if(dx.onlyLast && dx.onlyLast.length > 0){
        dx.onlyLast = dx.onlyLast.join(",");
      }else {
        dx.onlyLast = [];
      }

      if(dx.notLast && dx.notLast.length > 0){
        dx.notLast = dx.notLast.join(",");
      }else {
        dx.notLast = [];
      }
    }
    return dxs;
  },
  loadTemplateFromServer: function(templates,ribbonIndex,collection,ribbonItem){
    var id = ribbonItem.name;
    var columnValue = ribbonItem.columnValue;
    var sfx = (columnValue && columnValue.length>0) ? "."+columnValue : ".";
    var tplFullName = encodeURIComponent(collection+'.'+id+sfx);
    var url = '/idmu/template/'+tplFullName+"/";
    $.ajax({
      url: url,
      dataType: 'json',
      cache: false,
      success: function(data) {
        data.items = this.buildBodyItems(data.content);
        data.directives = this.fixDirectives(data.directives);
        this.setState({templates: templates,
                       template: data,
                       selectedRibbonIndex: ribbonIndex,
                       selectedRibbonItem: ribbonItem,
                       selectedCollection: collection});
      }.bind(this),
      error: function(xhr, status, err) {
        var dummy = {
          collection: collection,
          name: id,
          columnValue: columnValue
        };
        var dummyTemplate = {
          collection: collection,
          name: id,
          columnValue: columnValue,
          directives: [],
          content: "",
          error: true,
          errorMsg: err.toString()
        };
        dummyTemplate.items = this.buildBodyItems(dummyTemplate.content);
        var tmpTpl = templates[ribbonIndex];
        if(tmpTpl && (tmpTpl.collection !== collection || tmpTpl.name !== id || tmpTpl.columnValue !== columnValue)) {
          
          templates.splice(ribbonIndex, 0, dummy);
        }
        this.setState({templates: templates,
                       template: dummyTemplate,
                       selectedRibbonIndex: ribbonIndex,
                       selectedRibbonItem: ribbonItem,
                       selectedCollection: collection});
        console.error("FETCH TEMPLATE: "+url+"-", status, err.toString());
      }.bind(this)
    });
  },
  removeItemWithinList: function(level,index){
    //console.log('moving '+itemId+' to '+listId+':'+newIndex);
    //Probably want to fire an action creator here... but we'll just splice state manually
    var tpl = this.state.template;
    var newListInfo = $.extend(true, [], tpl['directives']);
    var oldItemArr = newListInfo;
    var item = oldItemArr.splice(index,1);
    tpl.directives = oldItemArr;
    this.setState({template: tpl});
  },
  moveItemWithinList: function(itemId, listId, newIndex){
    //console.log('moving '+itemId+' to '+listId+':'+newIndex);
    //Probably want to fire an action creator here... but we'll just splice state manually
    var tpl = this.state.template;
    var newListInfo = $.extend(true, [], tpl['directives']);
    var oldItemArr = newListInfo;
    var item = oldItemArr.splice(itemId,1);
    oldItemArr.splice(newIndex,0,item[0]);
    tpl.directives = oldItemArr;
    this.setState({template: tpl});
  },
  moveItemBetweenList: function(itemId, oldListId, oldIndex, newListId, newIndex){
    //console.log('moving '+itemId+' from '+oldListId+':'+oldIndex+' to '+newListId+':'+newIndex);
    //Probably want to fire an action creator here... but we'll just splice state manually
    var newListInfo = $.extend(true, [], this.directives); //this.state.directives);
    var oldItemArr = newListInfo;
    var item = oldItemArr.splice(oldIndex,1);
    var tpl = this.state.template;
    var newItemArr = tpl.directives;
    newItemArr.splice(newIndex,0,item[0]);
    tpl.directives = newItemArr;
    this.setState({template: tpl});
  },
  saveDirective: function(index,payload){
    //console.debug("save directive at "+index);
    var tpl = this.state.template;
    var directives = tpl.directives;
    directives[index] = payload;
    this.setState({template: tpl});
  },
  templateBody: function(payload){
    var opts = $.extend(true, {}, payload);

    if(opts.directives && opts.directives.length > 0){
      var dxs = opts.directives;
      for(var i=0; i<dxs.length; i++){
        var dx = dxs[i];
        if(dx.tags && dx.tags.length > 0){
          dx.tags = dx.tags.split(",");
        }else {
          dx.tags = [];
        }

        if(dx.onlyLast && dx.onlyLast.length > 0){
          dx.onlyLast = dx.onlyLast.split(",");
        }else {
          dx.onlyLast = [];
        }

        if(dx.notLast && dx.notLast.length > 0){
          dx.notLast = dx.notLast.split(",");
        }else {
          dx.notLast = [];
        }
      }
    }
    
    return({
      collection: opts.collection,
      name: opts.name,
      columnValue: opts.columnValue,
      outputFile: opts.outputFile,
      directives: opts.directives,
      content: opts.content,
      description: opts.description
    });
  },
  removeTemplateToServer: function(opts){
    var params = this.templateBody(opts);
    var collection = params.collection;
    var name = params.name;
    var sfx = (params.columnValue && params.columnValue.length>0) ? "."+params.columnValue : ".";
    var url = '/idmu/template/'+collection+"."+name+sfx+"_s/";
    $.ajax({
      url: url,
      contentType: "application/json",
      method: 'DELETE',
      cache: false,
      success: function(data) {
        if(data !== 'OK'){
          var msg = "Error occured while deleting the template "+(collection+"."+name+sfx);
          alert(msg);
        }else {
          this.setState({selectedCollection: null},function(){
            this.loadCollectionsFromServer();
          }.bind(this));
        }
      }.bind(this),
      error: function(xhr, status, err) {
        var netErr = "";
        netErr = err ? (" - "+err.toString()) : "";
        var msg = "Error occured while deleting the template "+(collection+"."+name+sfx)+netErr;
        alert(msg);
        console.error("DELETE:",status, err.toString());
      }.bind(this)
    });
  },
  addNewTemplateToServer: function(opts){
    var params = this.templateBody(opts);
    var collection = params.collection;
    var name = params.name;
    var sfx = (params.columnValue && params.columnValue.length>0) ? "."+params.columnValue : ".";    
    $.ajax({
      url: '/idmu/template/',
      dataType: 'json',
      contentType: "application/json",
      method: 'PUT',
      cache: false,
      data: JSON.stringify(params),
      success: function(data) {
        this.setState({selectedCollection: opts.collection},function(){
          this.loadCollectionsFromServer();
        }.bind(this));
      }.bind(this),
      error: function(xhr, status, err) {
        console.error(this.props.url, status, err.toString());
      }.bind(this)
    });
  },
  addSubTemplateToServer: function(opts){
    var result = false;
    var params = this.templateBody(opts);

    if(this.templateExist(params.collection,params.name)){
      result = true;
    }else {
      $.ajax({
        async: false,
        url: '/idmu/template/',
        dataType: 'json',
        contentType: "application/json",
        method: 'PUT',
        cache: false,
        data: JSON.stringify(params),
        success: function(data) {
          result = true;
        }.bind(this),
        error: function(xhr, status, err) {
          console.error(this.props.url, status, err.toString());
          result = false;
        }.bind(this)
      });
    }
    return result;
  },
  saveTemplateToServer: function(opts,collection) {
    var params = this.templateBody(opts);
    var url = '/idmu/template/';
    $.ajax({
      url: url,
      dataType: 'json',
      contentType: "application/json",
      method: 'PUT',
      cache: false,
      data: JSON.stringify(params),
      success: function(data) {
        this.setState({template: data});
      }.bind(this),
      error: function(xhr, status, err) {
        var netErr = "";
        if(xhr.responseText !== 'FORBIDDEN'){
          netErr = err ? (" - "+err.toString()) : "";
        }
        var name = params.name;
        var sfx = (params.columnValue && params.columnValue.length>0) ? "."+params.columnValue : ".";
        var tplName = collection+"."+name+sfx;
        var msg = "Error occured while saving the template "+tplName+netErr;
        alert(msg);
        console.error(url, status, netErr);
      }.bind(this)
    });
  },
  handleSave: function(opts) {
    //console.log(opts);
    this.saveTemplateToServer(opts,this.state.selectedCollection);
    this.loadCollectionsFromServer();
    this.loadTemplatesFromServer(this.state.selectedCollection);
  },
  render: function() {
    var className = this.props.level === 0 ? "app_view" : "app_view app_view_inner";
    return (
      <div className={className}>
        <div id="template_collection" className="row template_collection">
          {this.header()}
        </div>
        <div id="template_ribbon" className="row template_ribbon">
          <div>
            {this.ribbon()}
          </div>
        </div>
      </div>
    );
  },

  header: function(){
    if(this.props.level === 0){
      var index=0;
      var level=this.props.level;
      var this_ref = "ribbon_"+level+"_"+index;
      
      return(<TemplateCollection ref={this_ref} level={level} index={index} selectHandler={this.handleCollectionSelected} data={this.state}/>);
    }else{return (false);}
  },
  ribbon: function(){
    var ldirs;    
    var mCB = this.moveItemBetweenList;
    var aCB = this.moveItemWithinList;
    var sCB = this.handleSave;
    var dCB = this.saveDirective;
    var rCB = this.removeItemWithinList;
    var addTplCB = this.handleAddTemplate;
    var addSubTplCB = this.handleAddSubTemplate;
    var removeTplCB = this.handleRemoveTemplate;
    var mergeTplCB = this.handleMergeTemplate;
    var index=0;
    var level=this.props.level;
    var this_ref = "ribbon_"+level+"_"+index;

    ldirs = this.directives;
    return(<TemplateRibbon ref={this_ref} level={level} index={index} suppressNav={this.props.suppressNav} initHandler={this.handleCollectionSelected} selectHandler={this.handleRibbonSelected} data={this.state} mCB={mCB} aCB={aCB} sCB={sCB} dCB={dCB} rCB={rCB} addTplCB={addTplCB} addSubTplCB={addSubTplCB} mergeTplCB={mergeTplCB} removeTplCB={removeTplCB} ldirs={ldirs}/>);
  }
});

/*
*/
