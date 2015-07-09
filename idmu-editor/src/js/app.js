/**
* @jsx React.DOM
*/

function supports_history_api() {
  return !!(window.history && history.pushState);
}


$(document).ready(function(){
  // Dropdown menu
  //$(".dropdown-button").dropdown();
  React.initializeTouchEvents(true);
  React.render(
    <div className="container"><App suppressCollection={false} suppressRibbon={false}/></div>,
    document.getElementById("app_view")
  );
});
