/**
 * @jsx React.DOM
 */
var TemplateEditor = React.createClass({
  handleSave: function(opts) {
    opts.content = this.refs.template_body.state.content;
    this.props.sCB(opts);
  },
  render: function() {
    var mCB = this.props.mCB;
    var aCB = this.props.aCB;
    var sCB = this.props.sCB;
    return(
      <div className="row ">
        <TemplateHeader ref="template_header" data={this.props.data} mCB={mCB} aCB={aCB} sCB={this.handleSave}/>
        <TemplateBody ref="template_body" data={this.props.data}/>
        <TemplateFooter ref="template_footer" data={this.props.data}/>
      </div>
    );
  }
});

