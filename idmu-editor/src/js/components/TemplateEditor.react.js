/**
 * @jsx React.DOM
 */
var TemplateEditor = React.createClass({
  render: function() {
    var mCB = this.props.mCB;
    var aCB = this.props.aCB;
    return(
      <div className="row ">
        <TemplateHeader data={this.props.data} mCB={mCB} aCB={aCB}/>
        <TemplateBody data={this.props.data}/>
        <TemplateFooter data={this.props.data}/>
      </div>
    );
  }
});

