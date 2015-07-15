/**
 * @jsx React.DOM
 */
var HeaderPanel  = React.createClass({
  mixins: [TextEditMixin],
  getInitialState: function() {
    return $.extend(true, {}, this.props.data.template);
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
      return (<option key={i} value={k.description} label={k.description}>{k.description}</option>);
    });

    var level=this.props.level;
    var index=this.props.index;
    var name_id="name"; //+level+"_"+index;
    var directives_id="directives"; //+level+"_"+index;
    var output_id="output"; //+level+"_"+index;
    var description_id="description";//+level+"_"+index;
    
    return(
      <div className="panel-body">
        <div className="row">
          <div className="col-lg-12">

            <form>
              <div className="form-group col-xs-4 col-md-4">
                <label for={name_id} className="control-label">Name</label>
                
                <input className="form-control" id={name_id} type="text" value={this.state.name} onChange={this.handleTextEditChange}/>
              </div>
                        
              <div className="form-group col-xs-4 col-md-4">
                <div className="input-group">
                  <label for={directives_id} className="control-label">Directives</label>
                  <select id={directives_id} className="form-control">
                    {options}
                  </select>
                  <span className="input-group-btn input-group-btn-directives">
                    <button id="show-directives" onClick={changeCB} type="button" className="btn directive-btn">Configure</button>
                  </span>
                </div>
              </div>
              
              <div className="form-group col-xs-4 col-md-4">
                <label for={output_id} className="control-label">Output</label>
                <input className="form-control" id={output_id} type="text" value={this.state.output} onChange={this.handleTextEditChange}/>
              </div>

              <div className="form-group col-xs-12 col-md-12">
                <label for={description_id} className="control-label">Description</label>
                <textarea className="form-control" id={description_id} rows="2" onChange={this.handleTextEditChange}  value={this.state.description}/>
              </div>
                        
            </form>
                      
          </div>
        </div>
      </div>);
  }
});

