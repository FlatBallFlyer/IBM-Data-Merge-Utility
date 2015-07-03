/**
 * @jsx React.DOM
 */
var DirectivesTrigger = React.createClass({
  handleClick: function(e) {
    var el = this.refs.payload.getDOMNode();
    $(el).modal();
  },
  render: function() {
    var mCB = this.props.mCB;
    var aCB = this.props.aCB;
    return(
      <div onClick={this.handleClick}>
        <button type="button" className="btn btn-primary btn-xs directive-btn">Configure</button>
        <Directives mCB={mCB} aCB={aCB} ref="payload" data={this.props.data}/>
      </div>
    );
  }
});


var DirectivesListMixin = {
  getInitialState: function() {
    return({directives: []});
  },
  handleSort: function(e){
    if(this.props.sort == false){
      return;
    }
    if (e.from.dataset.sortableListId==this.props.id) {
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
    return (
      <ul ref="" className="list-group" data-sortable-list-id={id}>
        {
          
          this.props.directives.map(function(opt,i){
            var item_id = i;
            return(<li className="list-group-item" key={item_id} data-sortable-item-id={item_id}>
              <span className="drag-handle">::</span>{opt['name']}
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
  render: function() {
    var title = this.props.title;
    var id = this.props.id;
    var data = this.props.data;
    return (
      <ul ref="" className="list-group" data-sortable-list-id={id}>
        {
          
          this.props.directives.map(function(opt,i){
            var item_id = i;
            return(<li className="list-group-item" key={item_id} data-sortable-item-id={item_id}>
              <span className="drag-handle">::</span><DirectivesEditorTrigger title={opt['name']} data={data} directive={opt} index={i}/>
            </li>);
          })
         }
      </ul>
    );
  }
});


var Directives = React.createClass({
  mixins: [ModalMixin],
  getInitialState: function() {
    return {name: 'Blah', output: 'Output', description: ''};
  },
  handleSave: function(e) {
    console.debug("handle save..");
  },
  render: function() {
    var lhs = ALL_DIRECTIVES;
    var rhs=this.props.data['template'] ? this.props.data['template']['directives'] : [];
    var betweenCB = this.moveItemBetweenList;
    var withinCB = this.moveItemWithinList;
    var lhsID = "left";
    var rhsID = "right";
    var mCB = this.props.mCB;
    var aCB = this.props.aCB;
    return (
      <div onClick={this.handleClick} className="modal" role="dialog" aria-hidden="true">
        <div className="modal-dialog">
          <div className="modal-content">
            <div className="modal-header">
              <h3>Configure Directives</h3>
            </div>
            <div className="modal-body">

              <div className="row">
                <div className="col-lg-12">
                  <div className="from-directives col-xs-6 col-md-6">
                    <LHSList sort={false} key={lhsID} id={lhsID} title={"LHS"} directives={lhs} moveItemBetweenList={mCB} moveItemWithinList={aCB}/>
                  </div>
                  <div className="to-directives col-xs-6 col-md-6">
                    <RHSList sort={true} key={rhsID} id={rhsID} data={this.props.data} title={"RHS"} directives={rhs} moveItemBetweenList={mCB} moveItemWithinList={aCB}/>
                  </div>
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

