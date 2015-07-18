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
      directives: [],
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
    this.loadTemplateFromServer(collection,
                                selectedRibbonItem['name'],
                                selectedRibbonItem['columnValue']);
    this.setState({selectedRibbonItem: selectedRibbonItem,selectedRibbonIndex: selectedRibbonIndex});
  },
  handleAddTemplate: function(newTpl){
    this.addNewTemplateToServer(newTpl);
  },
  handleRemoveTemplate: function(tpl){
    this.removeTemplateToServer(tpl);
  },
  loadCollectionsFromServer: function() {
    var params = {};
    $.ajax({
      url: '/idmu/templates',
      dataType: 'json',
      cache: false,
      data: params,
      success: function(data) {
        var uniques={};
        var first = data[0];
        for(var idx=0; idx < data.length; idx++) {
          var key = data[idx].collection;
          var name = data[idx].name;
          var columnValue = data[idx].columnValue;
          uniques[key] = {collection: key};
        }
        var final_list = Object.keys(uniques).map(function(v){ return {collection: v}});

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
    $.ajax({
      url: '/idmu/templates/'+collection,
      dataType: 'json',
      cache: false,
      data: params,
      success: function(data) {
        self.setState({selectedRibbonIndex: 0,templates: data,selectedCollection: collection}, function(){
          var sel = self.props.selection || data[0];
          self.loadTemplateFromServer(sel.collection,sel.name,sel.columnValue);
        });
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
        this.setState({directives: data});
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
    var found = false;
    while((result = reg.exec(content)) !== null) {
      eIdx = content.indexOf("/>",result.index);
      if(eIdx!=-1){
        var slice1 = content.slice(sIdx,result.index);
        var slice2 = content.slice(result.index,eIdx+2);
        sIdx = eIdx+2;
        items.push({type: 'text',slice: slice1});
        items.push({type: 'bookmark',slice: slice2});
      }
    }
    var slice = content.slice(sIdx);
    if(slice && slice.length > 0){
      items.push({type: 'text', slice: slice});
    }else{
      items.push({type: 'text', slice: ""});
    }
    return items;
  },
  loadTemplateFromServer: function(collection,id,columnValue) {
    var sfx = (columnValue && columnValue.length>0) ? "."+columnValue : ".";
    $.ajax({
      url: '/idmu/template/'+encodeURIComponent(collection+'.'+id+sfx)+"/",
      dataType: 'json',
      cache: false,
      success: function(data) {
        data.items = this.buildBodyItems(data.content);

        var text = data.content.replace(Utils.tkBookmarkRegex(),"\<div class=\"tkbookmark\"  contenteditable=\"false\"");
        data.content = text;
        this.setState({template: data});
      }.bind(this),
      error: function(xhr, status, err) {
        console.error(this.props.url, status, err.toString());
      }.bind(this)
    });
  },
  removeItemWithinList(level,index){
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
    var newListInfo = $.extend(true, [], this.state.directives);
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
  templateBody: function(opts){
    return({
      collection: opts.collection,
      name: opts.name,
      columnValue: opts.columnValue,
      output: opts.output,
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
    $.ajax({
      url: '/idmu/template/'+collection+"."+name+sfx,
      contentType: "application/json",
      method: 'DELETE',
      cache: false,
      success: function(data) {
        this.setState({selectedCollection: null},function(){
          this.loadCollectionsFromServer();
        }.bind(this));
      }.bind(this),
      error: function(xhr, status, err) {
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
      url: '/idmu/templates/'+collection+"."+name+sfx,
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
  saveTemplateToServer: function(opts,collection) {
    var params = this.templateBody(opts);
    var name = params.name;
    var sfx = (params.columnValue && params.columnValue.length>0) ? "."+params.columnValue : ".";    
    $.ajax({
      url: '/idmu/templates/'+collection+"."+name+sfx,
      dataType: 'json',
      contentType: "application/json",
      method: 'PUT',
      cache: false,
      data: JSON.stringify(params),
      success: function(data) {
        this.setState({template: data});
      }.bind(this),
      error: function(xhr, status, err) {
        console.error(this.props.url, status, err.toString());
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
    }else{return (<div/>);}
  },
  ribbon: function(){
    var mCB = this.moveItemBetweenList;
    var aCB = this.moveItemWithinList;
    var sCB = this.handleSave;
    var dCB = this.saveDirective;
    var rCB = this.removeItemWithinList;
    var addTplCB = this.handleAddTemplate;
    var removeTplCB = this.handleRemoveTemplate;
    var index=0;
    var level=this.props.level;
    var this_ref = "ribbon_"+level+"_"+index;
    return(<TemplateRibbon ref={this_ref} level={level} index={index} suppressNav={this.props.suppressNav} initHandler={this.handleCollectionSelected} selectHandler={this.handleRibbonSelected} data={this.state} mCB={mCB} aCB={aCB} sCB={sCB} dCB={dCB} rCB={rCB} addTplCB={addTplCB} removeTplCB={removeTplCB}/>);
  }
});
