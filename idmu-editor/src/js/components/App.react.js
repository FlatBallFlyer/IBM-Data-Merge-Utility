/**
 * @jsx React.DOM
 */
var App = React.createClass({

  getInitialState: function() {
    return {
      selectedCollection: "0",
      selectedRibbonItem: null,
      selectedRibbonIndex: 0,
      data: {},
      directives: {'directives':[]},
      template: {'directives':[]}
    };
  },
  initRouter: function () {
    var routes = {
      //"/app": on_home
    };
    window.router = Router(routes);
    window.router.configure({html5history: supports_history_api()});
    window.router.init();
  },
  componentDidMount: function () {
    this.initRouter();
    this.loadTemplatesFromServer();
    //this.loadDirectivesFromServer();
  },
  componentDidUpdate: function() {
    //$('select').material_select();
  },
  handleCollectionSelected: function(selectedCollection) {
    var data = this.state.data;
    var keys = Object.keys(data);
    for(var i = 0, len = keys.length; i < len; i++) {
      var key = keys[i];
      if(key === selectedCollection) {
        data[key]['selected'] = true;
      }else{
        data[key]['selected'] = false;
      }
    }
    this.setState({
      selectedCollection: selectedCollection,
      data: data,
      selectedRibbonItem: null,
      selectedRibbonIndex: 0
    });
  },
  handleRibbonSelected: function(selectedRibbonIndex,selectedRibbonItem) {
    this.setState({selectedRibbonItem: selectedRibbonItem,selectedRibbonIndex: selectedRibbonIndex});
    this.loadTemplateFromServer(this.state.selectedCollection,
                                selectedRibbonItem['name'],
                                selectedRibbonItem['columnValue']);
  },
  loadTemplatesFromServer: function() {
    var params = {};
    $.ajax({
      url: '/idmu/templates',
      dataType: 'json',
      cache: false,
      data: params,
      success: function(data) {
        this.setState({data: data,selectedCollection: "root"});
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
    var newListInfo = $.extend(true, [], ALL_DIRECTIVES);
    var oldItemArr = newListInfo;
    var item = oldItemArr.splice(oldIndex,1);
    var tpl = this.state.template;
    var newItemArr = tpl.directives;
    newItemArr.splice(newIndex,0,item[0]);
    tpl.directives = newItemArr;
    this.setState({template: tpl});
  },
  render: function() {
    var mCB = this.moveItemBetweenList;
    var aCB = this.moveItemWithinList;
    return (
      <div className="container app_view">
        <div id="template_collection" className="row template_collection">
          <TemplateCollection selectHandler={this.handleCollectionSelected} data={this.state}/>
        </div>
        <div id="template_ribbon" className="row template_ribbon">
          <div className="container">
            <TemplateRibbon selectHandler={this.handleRibbonSelected} data={this.state} mCB={mCB} aCB={aCB}/>
          </div>
        </div>
      </div>
    );
  }
});
