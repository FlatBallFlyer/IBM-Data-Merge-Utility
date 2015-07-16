/**
 * @jsx React.DOM
 */
var RemoveTemplateTrigger = React.createClass({
  handleClick: function(e) {
    this.props.removeTplCB(this.props.selection);
  },
  render: function() {
    return(
      <a role="button" aria-expanded="true">
        <span className="input-group-btn input-group-btn-directives">
          <button onClick={this.handleClick} type="button" className="btn">Remove</button>
        </span>
      </a>
    );
  }
});
