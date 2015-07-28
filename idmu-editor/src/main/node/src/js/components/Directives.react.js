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
var DirectivesListMixin = {
  getInitialState: function() {
    return({directives: []});
  },
  handleSort: function(e){
    if(this.props.sort === false){
      return;
    }
    if (e.from.dataset.sortableListId===this.props.id) {
      this.props.moveItemWithinList(
        e.item.dataset.sortableItemId,
        e.from.dataset.sortableListId,
        e.newIndex
      );
    }
    else{
      this.props.moveItemBetweenList(
        e.item.dataset.sortableItemId,
        e.from.dataset.sortableListId,
        e.oldIndex,
        this.props.id,
        e.newIndex);
    }
  }
};

var LHSList = React.createClass({
  mixins: [SortableMixin,DirectivesListMixin],

  sortableOptions: {
    ref: "sortable",
    group: "lhs",
    model: "directives",
    sort: false,
    handle: ".drag-handle",
    scroll:true
  },
  render: function() {
    var title = this.props.title;
    var id = this.props.id;
    var ldirs = this.props.ldirs;
    return (
      <ul ref="" className="list-group" data-sortable-list-id={id}>
        {
          ldirs.map(function(opt,i){
            var item_id = i;
            return(<li className="list-group-item" key={item_id} data-sortable-item-id={item_id}>
              <span className="drag-handle">::</span>{opt['description']}
            </li>);
          })
         }
      </ul>
    );
  }
});

var RHSList = React.createClass({
  mixins: [SortableMixin,DirectivesListMixin],

  sortableOptions: {
    ref: "sortable",
    group: {
      name: "rhs",
      put: ["lhs"]
    },
    model: "directives",
    handle: ".drag-handle",
    scroll: true
  },
  getRHS: function(){
    var title = this.props.title;
    var data = this.props.data;
    var dCB = this.props.dCB;
    var rCB = this.props.rCB;

    var level=this.props.level;
    if(this.props.directives.length === 0){
      return(<li className="list-group-item">&nbsp;</li>);
    }else{
      return(
        this.props.directives.map(function(opt,i){
          var item_id = i;
          var this_ref = "directives_editor_trigger_"+level+"_"+i;
          return(
            <li className="list-group-item" key={item_id} data-sortable-item-id={item_id}>
              <span className="drag-handle">::</span>
              <DirectivesEditorTrigger ref={this_ref} level={level} index={i} title={opt['description']} data={data} directive={opt} rCB={rCB} dCB={dCB} />
            </li>
          );
        })
      );
    }
  },
  render: function() {
    var id = this.props.id;
    return (
      <ul ref="" className="list-group" data-sortable-list-id={id}>
        {this.getRHS()}
      </ul>
    );
  }
});

var Directives = React.createClass({
  getInitialState: function() {
    return {name: 'Blah', output: 'Output'};
  },
  handleSave: function(e) {
    console.debug("handle save..");
  },
  render: function() {
    var lhs = this.props.data.ldirs;
    var rhs=this.props.data['template'] ? this.props.data['template']['directives'] : [];
    var betweenCB = this.moveItemBetweenList;
    var withinCB = this.moveItemWithinList;
    var lhsID = "lhs";
    var rhsID = "rhs";
    var mCB = this.props.mCB;
    var aCB = this.props.aCB;
    var dCB = this.props.dCB;
    var rCB = this.props.rCB;
    var level=this.props.level;
    var index=this.props.index;
    var ldirs=this.props.ldirs;
    
    return (

      <div className="panel-body">
        <div className="row">
          <div className="col-lg-12">
            <div className="from-directives col-xs-6 col-md-6">
              <LHSList level={level} index={index} ref={"lhs_"+level+"_"+index} sort={false} key={lhsID} id={lhsID} title={"LHS"} directives={lhs} moveItemBetweenList={mCB} moveItemWithinList={aCB} ldirs={ldirs}/>
            </div>
            <div className="to-directives col-xs-6 col-md-6">
              <RHSList level={level} index={index} ref={"lhs_"+level+"_"+index} sort={true} key={rhsID} id={rhsID} data={this.props.data} title={"RHS"} directives={rhs} moveItemBetweenList={mCB} moveItemWithinList={aCB} dCB={dCB} rCB={rCB} ldirs={ldirs}/>
            </div>
          </div>
        </div>
        <div className="row">
          <div className="col-xs-12 col-md-12">
            <div className="col-xs-11 col-md-11">&nbsp;</div>
            <div className="col-xs-1 col-md-1">
              <span className="input-group-btn">
                <button id="show-header" onClick={this.props.changeCB} type="button" className="btn btn-xs btn-primary">Back</button>
              </span>
            </div>
          </div>
        </div>
      </div>
    );
  }
});

