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
  handleClickDirectives: function(evt) {
    //set state and open dialog ..
  },
  handleSave: function(evt) {
    var opts = {};
    opts = $.extend({},opts,this.refs.header_panel.state);
    this.props.sCB(opts);
  },
  showPanel: function(){
    var mCB = this.props.mCB;
    var aCB = this.props.aCB;
    var changeCB = this.handleChangePanel;
    if(this.state.panelConfig === 'show-directives') {
      return(<Directives ref="directives_panel" mCB={mCB} aCB={aCB} data={this.props.data} changeCB={changeCB}/>);
    }else {
      return(<HeaderPanel ref="header_panel" data={this.props.data} mCB={mCB} aCB={aCB} changeCB={changeCB}/>);
    }
  },
  render: function(){
    var data = this.props.data;
    var tpl = data.template;
    if(tpl){
      var columnValue = tpl['columnValue'] ? "."+tpl['columnValue'] : "";
      var name = tpl['name'];
      var label = tpl['collection']+"."+name+columnValue;
      return(
        <div className="row no-margin">
          <div className="panel-group" id="accordion" role="tablist" aria-multiselectable="true">
            <div className="panel panel-default">
              <div className="panel-heading" role="tab" id="headingOne">
                <div className="row">
                <div className="col-xs-10 col-md-10">
                  <a role="button" data-toggle="collapse" data-parent="#accordion" href="#collapseOne" aria-expanded="true" aria-controls="collapseOne">
                    <span className="control-label-big">{label}</span>
                  </a>
                </div>
                <div className="col-xs-1 col-md-1">
                  <a role="button" aria-expanded="true">
                    <span className="input-group-btn input-group-btn-directives">
                      <button onClick={this.handleSave} id="show-directives" type="button" className="btn btn-primary">Save</button>
                    </span>
                  </a>
                </div>
                <div className="col-xs-1 col-md-1">
                  &nbsp;
                </div>
                </div>
              </div>

              <div id="collapseOne" className="panel-collapse collapse" role="tabpanel" aria-labelledby="headingOne">
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

