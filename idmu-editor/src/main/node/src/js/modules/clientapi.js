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
var ClientApi = function(options) {
  this.url = options.url;
  this.name = "generic";
};

ClientApi.prototype._since = function(days) {
  if(!days || parseInt(days) === NaN) {
    return undefined;
  }

  var now =  Math.floor((new Date()).getTime() / 1000);
  var since = now - (days * 24 * 3600);
  return since;
};

ClientApi.prototype._get = function(url, data) {
  var jqxhr = $.ajax({
    method: "GET",
    url: url,
    context: this.options,
    dataType: "json",
    timeout: 10000,
    data: data,
  }).done(function(data){
    if(this && this.done) {
      this.done(data);
    }
  }).fail(function(){
    if(this && this.fail) {
      this.fail();
    }
  }).always(function(){
    if(this && this.always) {
      this.always();
    }
  });
};

ClientApi.prototype.get = function(options) {

  if(!options) {
    return;
  }

  this.options = options;

  var url = this.url;

  var id = this.options.id;
  if(id) {
    url += "/" + encodeURIComponent(id);
  }
  var data = {};
  this._get(url, data);
}
