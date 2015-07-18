/**
* @jsx React.DOM
*/

function supports_history_api() {
  return !!(window.history && history.pushState);
}


$(document).ready(function(){
  var level=0;
  var index=0;
  var this_ref = "app_"+level+"_"+index;
  React.initializeTouchEvents(true);
  React.render(
    <div className="container"><App level={level} index={index} suppressCollection={false} suppressRibbon={false} suppressNav={false}/></div>,
    document.getElementById("app_view")
  );
});
