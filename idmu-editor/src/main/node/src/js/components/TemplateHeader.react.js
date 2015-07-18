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
    var rCB = this.props.rCB;
    var level = this.props.level;
    var index = this.props.index;
    var changeCB = this.handleChangePanel;
    if(this.state.panelConfig === 'show-directives') {
      var this_ref="directives_panel_"+level+"_"+index;
      return(<Directives level={level} index={index} ref={this_ref} rCB={rCB} dCB={dCB} mCB={mCB} aCB={aCB} data={this.props.data} changeCB={changeCB}/>);
    }else {
      var p_ref = "header_panel_"+level+"_"+index;
      return(<HeaderPanel level={level} index={index} ref={p_ref} rCB={rCB} dCB={dCB} data={this.props.data} mCB={mCB} aCB={aCB} changeCB={changeCB}/>);
    }
  },
  insertAddButton: function(){
    var data = this.props.data;
    var tpl = data.template;
    var selection={collection: data.selectedCollection,
                   name: tpl.name};

    var level=this.props.level;
    var index=this.props.index;
    var this_ref = Utils.thisRef(level,index,"add_template_trigger");
    var addTplCB = this.props.addTplCB;
    if(level===0){
      return(<AddTemplateTrigger level={level} index={index} ref={this_ref} title={"Add Template"} selection={selection} addTplCB={addTplCB}/>);
    }else{
      return(<div/>);
    }
  },
  insertRemoveButton: function(){

    var data = this.props.data;
    var tpl = data.template;
    var selection={collection: data.selectedCollection,
                   name: tpl.name};
    
    var level=this.props.level;
    var index=this.props.index;
    var this_ref = Utils.thisRef(level,index,"remove_template_trigger");
    var removeTplCB = this.props.removeTplCB;
    if(this.props.level===0){
      //return(<div/>);
      return(<RemoveTemplateTrigger level={level} index={index} ref={this_ref} selection={selection} removeTplCB={removeTplCB}/>);
    }else{
      return(<div/>);
    }
  },
  insertSaveButton: function(){
    var level=this.props.level;
    var index=this.props.index;
    var show_directives_id="show-directives_"+level+"_"+index;
    return(
      <a role="button" aria-expanded="true">
        <span className="input-group-btn input-group-btn-directives">
          <button onClick={this.handleSave} id={show_directives_id} type="button" className="btn btn-primary">Save</button>
        </span>
      </a>
    );
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
      var classes = "row no-margin";
      if(this.props.suppressNav){
        classes += " col-md-12 "
      }
      return(
        <div className={classes}>
          <div className="panel-group" id={accordian_id} role="tablist" aria-multiselectable="true">
            <div className="panel panel-default">
              <div className="panel-heading" role="tab" id={panel_heading_id}>
                <div className="row">
                <div className="col-xs-8 col-md-8">
                  <a role="button" data-toggle="collapse" data-parent={"#"+accordian_id} href={"#"+collapse_id} aria-expanded="true" aria-controls={collapse_id}>
                    <span className="control-label-big">{label}</span>
                  </a>
                </div>
                <div className="col-xs-1 col-md-1">
                  {this.insertSaveButton()}
                </div>
                <div className="col-xs-1 col-md-1">
                  {this.insertAddButton()}
                </div>
                <div className="col-xs-1 col-md-1">
                  {this.insertRemoveButton()}
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

