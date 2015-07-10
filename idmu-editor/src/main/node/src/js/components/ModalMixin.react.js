/**
 * @jsx React.DOM
 */
var ModalMixin = {
  componentDidMount: function() {
    $(this.getDOMNode()).modal({ background: true
                               , keyboard: true
                               , show: false});
  },
  componentWillUnmount: function(){
    $(this.getDOMNode()).off('hidden');
  },
  handleClick: function(e) {
    e.stopPropagation();
  }
};
