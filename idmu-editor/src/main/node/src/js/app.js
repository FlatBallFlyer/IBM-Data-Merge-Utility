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
    <App />,
    document.getElementById("app_view")
  );
});
