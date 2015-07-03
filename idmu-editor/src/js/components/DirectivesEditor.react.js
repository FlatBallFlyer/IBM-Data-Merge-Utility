/**
 * @jsx React.DOM
 */
var DirectivesEditorTrigger = React.createClass({
  handleClick: function(e) {
    var el = this.refs.directive_editor.getDOMNode();
    $(el).modal();
  },
  render: function() {
    var cb = this.props.cb;
    return(
      <span onClick={this.handleClick}>{this.props.title}<DirectivesEditor  key={this.props.title}  ref="directive_editor" title={this.props.title} data={this.props.data}  directive={this.props.directive} index={this.props.index}/></span>
    );
  }
});

var DirectivesEditor = React.createClass({
  mixins: [ModalMixin],
  
  getInitialState: function() {
    return {name: 'Blah', output: 'Output', description: ''};
  },
  handleSave: function(e) {
    console.debug(this.refs[this.props.directive.name].state);
  },
  changeDirective: function(dx,index){
    this.props.data.template.directive[index] = dx;
  },
  editor: function(type){
   if(type === 0){
     return(<RequireTags ref={this.props.directive.name} data={this.props.data} cb={this.changeDirective} directive={this.props.directive} index={this.props.index}/>);
   }else if(type === 1){
     return(<ReplaceValue  ref={this.props.directive.name} data={this.props.data} cb={this.changeDirective} directive={this.props.directive} index={this.props.index}/>);
   }else if(type === 2){
     return(<InsertSubTemplatesFromTagData  ref={this.props.directive.name} data={this.props.data} cb={this.changeDirective} directive={this.props.directive} index={this.props.index}/>);
   }else if(type === 10){
     return(<InsertSubTemplatesFromSQLRows  ref={this.props.directive.name} data={this.props.data} cb={this.changeDirective} directive={this.props.directive} index={this.props.index}/>);
   }else if(type === 11){
     return(<ReplaceValuesFromSQLRow  ref={this.props.directive.name} data={this.props.data} cb={this.changeDirective} directive={this.props.directive} index={this.props.index}/>);
   }else if(type === 12){
     return(<ReplaceValuesFromSQLColumn  ref={this.props.directive.name}  data={this.props.data} cb={this.changeDirective} directive={this.props.directive} index={this.props.index}/>);
   }else if(type === 21){
     return(<InsertSubTemplatesFromCSV  ref={this.props.directive.name}  data={this.props.data} cb={this.changeDirective} directive={this.props.directive} index={this.props.index}/>);
   }else if(type === 22){
     return(<ReplaceValuesFromCSVRow  ref={this.props.directive.name}  data={this.props.data} cb={this.changeDirective} directive={this.props.directive} index={this.props.index}/>);
   }else if(type === 23){
     return(<ReplaceValuesFromCSVColumn  ref={this.props.directive.name} data={this.props.data} cb={this.changeDirective} directive={this.props.directive} index={this.props.index}/>);
   }else if(type === 31){
     return(<InsertSubTemplatesFromHTMLTableRows  ref={this.props.directive.name} data={this.props.data} cb={this.changeDirective} directive={this.props.directive} index={this.props.index}/>);
   }else if(type === 32){
     return(<ReplaceValuesFromHTMLTableRows  ref={this.props.directive.name} data={this.props.data} cb={this.changeDirective} directive={this.props.directive} index={this.props.index}/>);
   }else if(type === 33){
     return(<ReplaceValuesFromHTMLTableColumn  ref={this.props.directive.name} data={this.props.data} cb={this.changeDirective} directive={this.props.directive} index={this.props.index}/>);
   }else if(type === 34){
     return(<ReplaceValuesFromHTML  ref={this.props.directive.name} data={this.props.data} cb={this.changeDirective} directive={this.props.directive} index={this.props.index}/>);
   }else{
     return(<div/>);
   }
  },
  render: function() {
    return (
      <div onClick={this.handleClick} className="modal" role="dialog" aria-hidden="true">
        <div className="modal-dialog">
          <div className="modal-content">
            <div className="modal-header">
              <h3>{this.props.directive.name}</h3>
            </div>
            <div className="modal-body">
              <div className="row">
                <div className="col-lg-12">
                  {this.editor(this.props.directive.type)}
                </div>
              </div>
              
            </div>
            <div className="modal-footer">
              <button type="button" className="btn btn-default" data-dismiss="modal">Close</button>
              <button onClick={this.handleSave} type="button" className="btn btn-primary">Save changes</button>
            </div>
          </div>
        </div>
      </div>
    );
  }
});
