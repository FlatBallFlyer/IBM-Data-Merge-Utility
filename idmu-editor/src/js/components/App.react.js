/**
 * @jsx React.DOM
 */
var App = React.createClass({
  
  getInitialState: function() {
    return {
      selectedCollection: "root",
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
    this.initRouter();
    this.loadDirectivesFromServer();
    this.loadCollectionsFromServer();
    this.loadTemplatesFromServer("root");
  },
  handleCollectionSelected: function(selectedCollection) {
    this.loadTemplatesFromServer(selectedCollection);
  },
  handleRibbonSelected: function(selectedRibbonIndex,selectedRibbonItem) {
    this.setState({selectedRibbonItem: selectedRibbonItem,selectedRibbonIndex: selectedRibbonIndex});
    this.loadTemplateFromServer(this.state.selectedCollection,
                                selectedRibbonItem['name'],
                                selectedRibbonItem['columnValue']);
  },
  loadCollectionsFromServer: function() {
    var selectedCollection = this.state.selectedCollection;
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
          uniques[key] = {collection: key};
          console.log("key="+key);
        }
        var final_list = Object.keys(uniques).map(function(v){ return {collection: v}});
        console.debug(final_list);
        this.setState({data: final_list,selectedCollection: selectedCollection});
      }.bind(this),
      error: function(xhr, status, err) {
        console.error(this.props.url, status, err.toString());
      }.bind(this)
    });
  },
  loadTemplatesFromServer: function(collection) {
    var params = {};
    $.ajax({
      url: '/idmu/templates/'+collection,
      dataType: 'json',
      cache: false,
      data: params,
      success: function(data) {
        this.setState({templates: data,selectedCollection: collection}, function(){
          var sel = this.state.selectedRibbonItem || data[0];
          this.loadTemplateFromServer(sel.collection,sel.name,sel.columnValue);
          
        }.bind(this));
        
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
  loadTemplateFromServer: function(collection,id,columnValue) {
    var params = {};
    if(columnValue){
      params['columnValue'] = columnValue;
    }
    $.ajax({
      url: '/idmu/templates/'+collection+'/'+id,
      dataType: 'json',
      cache: false,
      data: params,
      success: function(data) {
        this.setState({template: data});
      }.bind(this),
      error: function(xhr, status, err) {
        console.error(this.props.url, status, err.toString());
      }.bind(this)
    });
  },
  moveItemWithinList: function(itemId, listId, newIndex){
    console.log('moving '+itemId+' to '+listId+':'+newIndex);
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
    console.log('moving '+itemId+' from '+oldListId+':'+oldIndex+' to '+newListId+':'+newIndex);
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
    console.debug("save directive at "+index);
    var tpl = this.state.template;
    var directives = tpl.directives;
    directives[index] = payload;
    this.setState({template: tpl});
  },
  saveTemplateToServer: function(opts,collection) {
    console.log("save template...");

    var params = {template:{}};
    params.template = $.extend({},this.state.template,opts);
    $.ajax({
      url: '/idmu/templates/'+collection,
      dataType: 'json',
      method: 'PUT',
      cache: false,
      data: params,
      success: function(data) {
        this.setState({template: data});
      }.bind(this),
      error: function(xhr, status, err) {
        console.error(this.props.url, status, err.toString());
      }.bind(this)
    });
  },
  handleSave: function(opts) {
    this.saveTemplateToServer(opts,this.state.selectedCollection);
    this.loadCollectionsFromServer();
    this.loadTemplatesFromServer(this.state.selectedCollection);
  },
  render: function() {
    var mCB = this.moveItemBetweenList;
    var aCB = this.moveItemWithinList;
    var sCB = this.handleSave;
    var dCB = this.saveDirective;
    return (
      <div className="container app_view">
        <div id="template_collection" className="row template_collection">
          <TemplateCollection selectHandler={this.handleCollectionSelected} data={this.state}/>
        </div>
        <div id="template_ribbon" className="row template_ribbon">
          <div className="container">
            <TemplateRibbon initHandler={this.handleCollectionSelected} selectHandler={this.handleRibbonSelected} data={this.state} mCB={mCB} aCB={aCB} sCB={sCB} dCB={dCB}/>
          </div>
        </div>
      </div>
    );
  }
});
