/*
* Copyright 2015, 2015 IBM
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
*/
/**
 * @jsx React.DOM
 */
var DirectivesEditorTrigger = React.createClass({
  handleClick: function(e) {
    var this_ref = "directives_editor_"+this.props.level+"_"+this.props.index;
    var el = this.refs[this_ref].getDOMNode();
    $(el).modal();
  },
  removeDirective: function(e){
    e.stopPropagation();
    var index = this.props.index;
    var level = this.props.level;
    var this_ref = "directives_editor_"+level+"_"+index;
    this.props.rCB(level,index);
  },
  render: function() {
    var index = this.props.index;
    var level = this.props.level;
    var this_ref = "directives_editor_"+level+"_"+index;
    var dCB = this.props.dCB;
    return(
      <span onClick={this.handleClick}>{this.props.title} <span className="glyphicon glyphicon-remove pull-right" onClick={this.removeDirective} style={{cursor: 'pointer',zIndex:20,color:"red"}}></span><DirectivesEditor  key={this.props.title}  ref={this_ref} level={level} index={index} title={this.props.title} data={this.props.data}  directive={this.props.directive} dCB={dCB}/></span>
    );
  }
});

var DirectivesEditor = React.createClass({
  mixins: [ModalMixin],
  
  handleSave: function(e) {
    var index = this.props.index;
    var level = this.props.level;
    var this_ref = this.props.directive.description+"_"+level+"_"+index;
    var payload = $.extend(true, {}, this.refs[this_ref].state);
    this.props.dCB(this.props.index,payload);
  },
  editor: function(type_in){
    var type = parseInt(type_in);
    var index = this.props.index;
    var level = this.props.level;
    var this_ref = this.props.directive.description+"_"+level+"_"+index;
    
   if(type === 0){
     return(<RequireTags ref={this_ref} index={index} level={level} data={this.props.data} directive={this.props.directive}/>);
   }else if(type === 1){
     return(<ReplaceValue ref={this_ref} data={this.props.data} directive={this.props.directive}/>);
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
     return(false);
   }
  },
  render: function() {
    return (
      <div onClick={this.handleClick} className="modal" role="dialog" aria-hidden="true">
        <div className="modal-dialog">
          <div className="modal-content">
            <div className="modal-header">
              <h3>{this.props.directive.description}</h3>
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
              <button onClick={this.handleSave} type="button" className="btn btn-primary" data-dismiss="modal">Save changes</button>
            </div>
          </div>
        </div>
      </div>
    );
  }
});
