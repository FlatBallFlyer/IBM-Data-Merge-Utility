/**
 * @jsx React.DOM
 */
var TemplateRibbonItem = React.createClass({
  handleItemClick: function(evt){
    this.props.cb(this.props.data);
  },
  render: function() {
    var mCB = this.props.mCB;
    var aCB = this.props.aCB;
    var sCB = this.props.sCB;
    var dCB = this.props.dCB;
    return(
      <div className="row ribbon-item">
        <TemplateEditor mCB={mCB} aCB={aCB} sCB={sCB} dCB={dCB} data={this.props.collection} selection={this.props.data}/>
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

    var navLCB = this.handleNavLeftClick;
    var navRCB = this.handleNavRightClick;
    var mCB = this.props.mCB;
    var aCB = this.props.aCB;
    var sCB = this.props.sCB;
    var dCB = this.props.dCB;
    
    if(item) {
      var items = [item].map(function(opt,i){
        return(<TemplateRibbonItem key={i} cb={selectHandler} mCB={mCB} aCB={aCB} sCB={sCB} dCB={dCB} data={opt} collection={data}/>);
      });
      
      newRibbon = [0].map(function(opt,i){
        return(
          <div key={i} id="ribbon" className="row ribbon">
            <div className="row-height">
              <div onClick={navLCB}  id="ribbon-left" className="col-xs-1 col-height col-middle text-center ribbon-nav ribbon-nav-width">
                <span>&lt;&lt;</span>
              </div>
              <div className="col-md-10 col-height ribbon-items">
                {items}
              </div>
              <div onClick={navRCB} id="ribbon-right" className="col-xs-1 col-height col-middle text-center ribbon-nav ribbon-nav-width">
                <span>&gt;&gt;</span>
              </div>
            </div>
          </div>
        )});
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
