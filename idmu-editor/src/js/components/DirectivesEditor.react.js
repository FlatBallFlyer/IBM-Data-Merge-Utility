/**
 * @jsx React.DOM
 */
var DirectivesEditorTrigger = React.createClass({
  handleClick: function(e) {
    console.log(this.refs);
    var this_ref = "directives_editor_"+this.props.level+"_"+this.props.index;
    var el = this.refs[this_ref].getDOMNode();
    $(el).modal();
  },
  render: function() {
    var index = this.props.index;
    var level = this.props.level;
    var this_ref = "directives_editor_"+level+"_"+index;
    var dCB = this.props.dCB;
    return(
      <span onClick={this.handleClick}>{this.props.title}<DirectivesEditor  key={this.props.title}  ref={this_ref} level={level} index={index} title={this.props.title} data={this.props.data}  directive={this.props.directive} index={this.props.index} dCB={dCB}/></span>
    );
  }
});

var DirectivesEditor = React.createClass({
  mixins: [ModalMixin],
  
  handleSave: function(e) {
    var index = this.props.index;
    var level = this.props.level;
    var this_ref = this.props.directive.name+"_"+level+"_"+index;
    var payload = this.refs[this_ref].state;
    this.props.dCB(this.props.index,payload);
  },
  editor: function(type_in){
    var type = parseInt(type_in);
    var index = this.props.index;
    var level = this.props.level;
    var this_ref = this.props.directive.name+"_"+level+"_"+index;
    
   if(type === 0){
     return(<RequireTags ref={this_ref} index={index} level={level} data={this.props.data} directive={this.props.directive}/>);
   }else if(type === 1){
     return(<ReplaceValue data={this.props.data} directive={this.props.directive}/>);
   }else if(type === 2){
     return(<InsertSubTemplatesFromTagData  ref={this_ref} index={index} level={level} data={this.props.data} directive={this.props.directive}/>);
   }else if(type === 10){
     return(<InsertSubTemplatesFromSQLRows  ref={this_ref} index={index} level={level} data={this.props.data} directive={this.props.directive}/>);
   }else if(type === 11){
     return(<ReplaceValuesFromSQLRow  ref={this_ref} index={index} level={level} data={this.props.data} directive={this.props.directive}/>);
   }else if(type === 12){
     return(<ReplaceValuesFromSQLColumn  ref={this_ref} index={index} level={level}  data={this.props.data} directive={this.props.directive}/>);
   }else if(type === 21){
     return(<InsertSubTemplatesFromCSV  ref={this_ref} index={index} level={level}  data={this.props.data} directive={this.props.directive}/>);
   }else if(type === 22){
     return(<ReplaceValuesFromCSVRow  ref={this_ref} index={index} level={level}  data={this.props.data} directive={this.props.directive}/>);
   }else if(type === 23){
     return(<ReplaceValuesFromCSVColumn  ref={this_ref} index={index} level={level} data={this.props.data} directive={this.props.directive}/>);
   }else if(type === 31){
     return(<InsertSubTemplatesFromHTMLTableRows  ref={this_ref} index={index} level={level} data={this.props.data} directive={this.props.directive}/>);
   }else if(type === 32){
     return(<ReplaceValuesFromHTMLTableRows  ref={this_ref} index={index} level={level} data={this.props.data} directive={this.props.directive} />);
   }else if(type === 33){
     return(<ReplaceValuesFromHTMLTableColumn  ref={this_ref} index={index} level={level} data={this.props.data} directive={this.props.directive}/>);
   }else if(type === 34){
     return(<ReplaceValuesFromHTML  ref={this_ref} index={index} level={level} data={this.props.data} directive={this.props.directive} />);
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
