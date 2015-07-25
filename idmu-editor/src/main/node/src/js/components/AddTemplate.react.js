/**
 * @jsx React.DOM
 */
var AddTemplateTrigger = React.createClass({
  handleClick: function(e) {
    var mdl_ref = Utils.thisRef(this.props.level,this.props.index,"add_template_editor");
    var el = this.refs[mdl_ref].getDOMNode();
    $(el).modal();
  },
  render: function() {
    var index = this.props.index;
    var level = this.props.level;
    var mdl_ref = Utils.thisRef(level,index,"add_template_editor");
    var addTplCB = this.props.addTplCB;
    return(
      <a role="button" aria-expanded="true">
        <span className="input-group-btn input-group-btn-directives">
          <button onClick={this.handleClick} type="button" className="btn">Add</button>
        </span>
        <AddTemplate  ref={mdl_ref} level={level} index={index} title={this.props.title} selection={this.props.selection} addTplCB={addTplCB}/>
      </a>
    );
  }
});

var AddTemplate = React.createClass({
  mixins: [ModalMixin,TextEditMixin],
  getInitialState: function() {
    return $.extend(true, {}, this.props.selection);
  },
  componentWillReceiveProps: function(nextProps) {
    this.setState(nextProps.selection);
  },
  handleAdd: function(){
    this.props.addTplCB(this.state);
  },
  insertCollection: function(){
    if(this.props.level === 0){
      return(
        <div className="form-group col-xs-6 col-md-6">
          <label for="collection" className="control-label">Collection</label>
          <input  onChange={this.handleTextEditChange} className="form-control" id="collection" type="text" value={this.state.collection}/>
        </div>);
    }else{
      return(
        <div className="form-group col-xs-6 col-md-6">
          <label for="collection" className="control-label">Collection</label>
          <input  readOnly onChange={this.handleTextEditChange} className="form-control" id="collection" type="text" value={this.state.collection}/>
        </div>);
    }
  },
  insertName: function(){
    if(this.props.level === 0){
      return(
        <div className="form-group col-xs-6 col-md-6">
          <label for="collection" className="control-label">Name</label>
          <input  onChange={this.handleTextEditChange} className="form-control" id="name" type="text" value={this.state.name}/>
        </div>
      );
    }else{
      return(
        <div className="form-group col-xs-6 col-md-6">
          <label for="collection" className="control-label">Name</label>
          <input readOnly onChange={this.handleTextEditChange} className="form-control" id="name" type="text" value={this.state.name}/>
        </div>
      );
    }
  },
  insertColumn: function(){
    return(
      <div className="form-group col-xs-6 col-md-6">
        <label for="collection" className="control-label">Column Value</label>
        <input  onChange={this.handleTextEditChange} className="form-control" id="columnValue" type="text" value={this.state.columnValue}/>
      </div>
    );
  },
  render: function(){
    return (
      <div onClick={this.handleClick} className="modal" role="dialog" aria-hidden="true">
        <div className="modal-dialog">
          <div className="modal-content">
            <div className="modal-header">
              <h3>{this.props.title}</h3>
            </div>
            <div className="modal-body">
              <div className="row">
                <div className="col-lg-12">
                  <form>
                    {this.insertCollection()}
                    {this.insertName()}
                    {this.insertColumn()}
                  </form>
                </div>
              </div>
              
            </div>
            <div className="modal-footer">
              <button type="button" className="btn btn-default" data-dismiss="modal">Close</button>
              <button onClick={this.handleAdd} type="button" className="btn btn-primary" data-dismiss="modal">Add</button>
            </div>
          </div>
        </div>
      </div>
    );
  }
});
