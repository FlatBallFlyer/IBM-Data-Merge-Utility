/**
 * @jsx React.DOM
 */
var TemplateBody = React.createClass({
  mixins: [TextEditMixin],
  getInitialState: function() {
    return {content: 'Something here..'};
  },  
  componentWillReceiveProps: function(nextProps) {
    var data = nextProps.data;
    var tpl = data['template'];
    if(tpl){
      this.setState({
        content: (tpl['content'] || 'None')
      });
    }
  },
  render: function(){
    return(
      <div className="row no-margin      ribbon-inside">
        <form>
          <div className="form-group col-xs-12 col-md-12">
            <div className="form-group col-xs-12 col-md-12">
              <label for="name" className="control-label">Content</label>
              <textarea  onChange={this.handleTextEditChange} className="form-control" id="content" rows="10"  value={this.state.content}/>
            </div>
          </div>
        </form>
      </div>);
  }
});
