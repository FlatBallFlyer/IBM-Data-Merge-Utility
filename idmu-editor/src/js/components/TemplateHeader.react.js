/**
 * @jsx React.DOM
 */
var TemplateHeader = React.createClass({
  getInitialState: function() {
    return {panelConfig: 'show-header'};
  },
  handleChangePanel: function(evt){
    this.setState({panelConfig: evt.target.id});
  },
  getHeaderValues: function(){
    var opts = {};
    var this_ref = "header_panel_"+this.props.level+"_"+this.props.index;
    if(this.refs[this_ref]) {
      opts = $.extend({},opts,this.refs[this_ref].state);
    }
    return opts;
  },
  handleSave: function(evt) {
      this.props.sCB();
  },
  showPanel: function(){
    var mCB = this.props.mCB;
    var aCB = this.props.aCB;
    var dCB = this.props.dCB;
    var level = this.props.level;
    var index = this.props.index;
    var changeCB = this.handleChangePanel;
    if(this.state.panelConfig === 'show-directives') {
      var this_ref="directives_panel_"+level+"_"+index;
      return(<Directives level={level} index={index} ref={this_ref} dCB={dCB} mCB={mCB} aCB={aCB} data={this.props.data} changeCB={changeCB}/>);
    }else {
      var this_ref = "header_panel_"+level+"_"+index;
      return(<HeaderPanel level={level} index={index} ref={this_ref} dCB={dCB} data={this.props.data} mCB={mCB} aCB={aCB} changeCB={changeCB}/>);
    }
  },
  render: function(){
    var data = this.props.data;
    var tpl = data.template;

    var level=this.props.level;
    var index=this.props.index;
    var accordian_id = "accordian_"+level+"_"+index;
    var panel_heading_id = "headingOne_"+level+"_"+index;
    var show_directives_id="show-directives_"+level+"_"+index;
    var collapse_id = "collapseOne_"+level+"_"+index;
    if(tpl){
      var columnValue = tpl['columnValue'] ? "."+tpl['columnValue'] : "";
      var name = tpl['name'];
      var label = tpl['collection']+"."+name+columnValue;
      return(
        <div className="row no-margin">
          <div className="panel-group" id={accordian_id} role="tablist" aria-multiselectable="true">
            <div className="panel panel-default">
              <div className="panel-heading" role="tab" id={panel_heading_id}>
                <div className="row">
                <div className="col-xs-10 col-md-10">
                  <a role="button" data-toggle="collapse" data-parent={"#"+accordian_id} href={"#"+collapse_id} aria-expanded="true" aria-controls={collapse_id}>
                    <span className="control-label-big">{label}</span>
                  </a>
                </div>
                <div className="col-xs-1 col-md-1">
                  <a role="button" aria-expanded="true">
                    <span className="input-group-btn input-group-btn-directives">
                      <button onClick={this.handleSave} id={show_directives_id} type="button" className="btn btn-primary">Save</button>
                    </span>
                  </a>
                </div>
                <div className="col-xs-1 col-md-1">
                  &nbsp;
                </div>
                </div>
              </div>

              <div id={collapse_id} className="panel-collapse collapse" role="tabpanel" aria-labelledby={panel_heading_id}>
                {this.showPanel()}
              </div>
            </div>
          </div>
        </div>
      );
    }else{
      return(<div/>);
    }
  }
});

