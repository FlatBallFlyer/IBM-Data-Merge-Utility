/**
 * @jsx React.DOM
 */
var TemplateRibbonItem = React.createClass({
  render: function() {
    var mCB = this.props.mCB;
    var aCB = this.props.aCB;
    var sCB = this.props.sCB;
    var dCB = this.props.dCB;
    var rCB = this.props.rCB;
    var addTplCB = this.props.addTplCB;
    var removeTplCB = this.props.removeTplCB;
    var index = this.props.index;
    var level = this.props.level;
    var this_ref = "template_editor_"+level+"_"+index;
    return(
      <div className="row ribbon-item">
        <TemplateEditor ref={this_ref} level={level} index={index} rCB={rCB} mCB={mCB} aCB={aCB} sCB={sCB} dCB={dCB} data={this.props.collection} selection={this.props.data} addTplCB={addTplCB} removeTplCB={removeTplCB}/>
      </div>
    );
  }
});
  
var TemplateRibbon = React.createClass({
  handleNavLeftClick: function(){
    this.handleNavClick(-1);
  },
  handleNavRightClick: function(){
    this.handleNavClick(1);
  },
  handleNavClick: function(direction) {
    var data = this.props.data;
    var idx = data.selectedRibbonIndex + (direction*1);
    if(idx < 0) {
      idx = data.templates.length-1;
    }else if(idx >= data.templates.length) {
      idx = 0;
    }
    this.props.selectHandler(idx,data.templates[idx]);
  },
  leftNav: function(){
    var navLCB = this.handleNavLeftClick;
    if(this.props.suppressNav){
      return(
        <div className="col-xs-1 col-height col-middle text-center ribbon-nav ribbon-nav-width">
          <span>&nbsp;</span>
        </div>
      );
    }else{
      return(
        <div onClick={navLCB}  className="col-xs-1 col-height col-middle text-center ribbon-nav ribbon-nav-width">
          <span>&lt;&lt;</span>
        </div>
      );
    }
  },
  rightNav: function(){
    var navRCB = this.handleNavRightClick;
    if(this.props.suppressNav){
      return(
        <div className="col-xs-1 col-height col-middle text-center ribbon-nav ribbon-nav-width">
          <span>&nbsp;</span>
        </div>);
    }else{
      return(
        <div onClick={navRCB} className="col-xs-1 col-height col-middle text-center ribbon-nav ribbon-nav-width">
          <span>&gt;&gt;</span>
        </div>);
    }
  },
  render: function(){
    var data = this.props.data;
    if(!data.templates || data.templates.length <= 0) {
      return(<div/>);
    }
    var selectedCollection = data.selectedCollection;
    var collection = data['data'];
    var templates=data.templates;
    var selectHandler = this.props.selectHandler;
    var item = templates[data.selectedRibbonIndex];
    var newRibbon = [];

    var mCB = this.props.mCB;
    var aCB = this.props.aCB;
    var sCB = this.props.sCB;
    var dCB = this.props.dCB;
    var rCB = this.props.rCB;
    var addTplCB = this.props.addTplCB;
    var removeTplCB = this.props.removeTplCB;

    var level = this.props.level;
    if(item) {
      var items = [item].map(function(opt,i){
        var this_ref = "ribbon_item_"+level+"_"+i;
        return(<TemplateRibbonItem ref={this_ref} level={level} index={i} key={i} rCB={rCB} mCB={mCB} aCB={aCB} sCB={sCB} dCB={dCB} data={opt} collection={data}  addTplCB={addTplCB}  removeTplCB={removeTplCB}/>);
      });

      newRibbon = [0].map(function(opt,i){
        return(
          <div key={i} className="row ribbon">
            <div className="row-height">
              {this.leftNav()}
              <div className="col-md-10 col-height ribbon-items">                
                {items}
              </div>
              {this.rightNav()}
            </div>
          </div>
        )}.bind(this));
    }else {
      newRibbon=[0].map(function(opt,i){
        return(<div/>);
      });
    }

    
    return(
      <div>{newRibbon}</div>
    );
  }
});
