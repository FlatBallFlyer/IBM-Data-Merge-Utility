/**
 * @jsx React.DOM
 */
var TemplateHeader = React.createClass({
  mixins: [TextEditMixin],
  getInitialState: function() {
    return {name: 'Blah', output: 'Output', description: ''};
  },
  componentWillReceiveProps: function(nextProps) {
    var data = nextProps.data;
    var tpl = data['template'];
    if(tpl){
      this.setState({
        name: tpl['name'],
        output: tpl['output'],
        description: (tpl['description'] || 'None')
      });
    }
  },
  handleClickDirectives: function(evt) {
    //set state and open dialog ..
  },
  render: function(){
    var data = this.props.data;
    var tpl = data['template'];
    var mCB = this.props.mCB;
    var aCB = this.props.aCB;
    if(tpl){
      var selectedCollection = data.selectedCollection;
      var columnValue = tpl['columnValue'] ? "."+tpl['columnValue'] : "";
      var name = tpl['name'];
      var label = tpl['collection']+"."+name+columnValue;

      var directives = tpl['directives'] ? tpl['directives'] : [];
      var options=directives.map(function(k,i){
        return (<option key={i} value={k.name} label={k.name}>{k.name}</option>);
      });
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
                              <DirectivesTrigger  mCB={mCB} aCB={aCB} data={this.props.data}/>
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
                </div>
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

