/*
* Copyright 2015, 2015 IBM
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
*/
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
