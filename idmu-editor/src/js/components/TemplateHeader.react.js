/**
 * @jsx React.DOM
 */

var TemplateHeader = React.createClass({
  getInitialState: function() {
    return {panelConfig: 'show-header'};
  },
  handleChangePanel: function(evt){
    console.log("click="+evt.target.id);
    this.setState({panelConfig: evt.target.id});
  },
  handleClickDirectives: function(evt) {
    //set state and open dialog ..
  },
  showPanel: function(){
    var mCB = this.props.mCB;
    var aCB = this.props.aCB;
    var changeCB = this.handleChangePanel;
    if(this.state.panelConfig === 'show-directives') {
      return(<Directives mCB={mCB} aCB={aCB} data={this.props.data} changeCB={changeCB}/>);
    }else {
      return(<HeaderPanel data={this.props.data} mCB={mCB} aCB={aCB} changeCB={changeCB}/>);
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
                <a role="button" data-toggle="collapse" data-parent="#accordion" href="#collapseOne" aria-expanded="true" aria-controls="collapseOne">
                  <span className="control-label-big">{label}</span>
                </a>
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

