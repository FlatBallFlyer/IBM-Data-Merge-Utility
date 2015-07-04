/**
 * @jsx React.DOM
 */
var HeaderPanel  = React.createClass({
  mixins: [TextEditMixin],
  getInitialState: function() {
    return this.props.data.template;
  },
  componentWillReceiveProps: function(nextProps) {
    this.setState(nextProps.data.template);
  },
  render: function(){
    var mCB = this.props.mCB;
    var aCB = this.props.aCB;
    var changeCB = this.props.changeCB;
    var data = this.props.data;
    var tpl = this.state;

    var selectedCollection = data.selectedCollection;
    var columnValue = tpl['columnValue'] ? "."+tpl['columnValue'] : "";
    var name = tpl['name'];
    var label = tpl['collection']+"."+name+columnValue;

    var directives = tpl['directives'] ? tpl['directives'] : [];
    var options=directives.map(function(k,i){
      return (<option key={i} value={k.name} label={k.name}>{k.name}</option>);
    });
      
    return(
      <div className="panel-body">
        <div className="row">
          <div className="col-lg-12">

            <form>
              <div className="form-group col-xs-4 col-md-4">
                <label for="name" className="control-label">Name</label>
                
                <input className="form-control" id="name" type="text" value={this.state.name} onChange={this.handleTextEditChange}/>
              </div>
                        
              <div className="form-group col-xs-4 col-md-4">
                <div className="input-group">
                  <label for="name" className="control-label">Directives</label>
                  <select id="directives" className="form-control">
                    {options}
                  </select>
                  <span className="input-group-btn input-group-btn-directives">
                    <button id="show-directives" onClick={changeCB} type="button" className="btn btn-primary btn-xs directive-btn">Configure</button>
                  </span>
                </div>
              </div>
              
              <div className="form-group col-xs-4 col-md-4">
                <label for="name" className="control-label">Output</label>
                <input className="form-control" id="output" type="text" value={this.state.output} onChange={this.handleTextEditChange}/>
              </div>

              <div className="form-group col-xs-12 col-md-12">
                <label for="name" className="control-label">Description</label>
                <textarea className="form-control" id="description" rows="2" onChange={this.handleTextEditChange}  value={this.state.description}/>
              </div>
                        
            </form>
                      
          </div>
        </div>
      </div>);
  }
});

