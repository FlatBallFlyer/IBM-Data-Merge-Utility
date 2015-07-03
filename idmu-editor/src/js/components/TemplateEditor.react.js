/**
 * @jsx React.DOM
 */
var TemplateEditor = React.createClass({
  componentDidUpdate: function() {
  },
  render: function() {
    var data = this.props.data;
    var selectedTemplate = data['selectedTemplate'];
    var mCB = this.props.mCB;
    var aCB = this.props.aCB;
    var theTemplate = [0].map(function(opt,i){
        return(<Template data={data} mCB={mCB} aCB={aCB}/>);
    });
    
    return(
      <div>        
        {theTemplate}
      </div>
    );
  }
});

