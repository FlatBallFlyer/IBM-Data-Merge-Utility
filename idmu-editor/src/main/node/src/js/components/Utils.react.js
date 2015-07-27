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
var Utils = {
  tkBookmarkRegex: function(){
    return (/\<tkBookmark/g);
  },
  htmlEncodeRegex: function(){
    return(/[\u00A0-\u9999<>\&]/gim);
  },
  prepareContentForSave: function(content_raw){
    var content = content_raw.replace(/<a(>|.*?[^?]>)/g,"");
    content = content.replace(/div class=\"tkbookmark\"/g,"tkBookmark");
    content = content.replace(/><\/div>/g,"/>");
    return content;
  },
  thisRef: function(level,index,label){
    return(label+"_"+level+"_"+index);
  },
  uuid: function() {
    function s4() {
      return Math.floor((1 + Math.random()) * 0x10000)
        .toString(16)
        .substring(1);
    }
    return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
      s4() + '-' + s4() + s4() + s4();
  }
};

/*
    var content_raw = this.refs.template_body.state.content,
        content = content_raw.replace(/<a(>|.*?[^?]>)/g,"");
    content = content.replace(/div class=\"tkbookmark\"/g,"tkBookmark");
    content = content.replace(/><\/div>/g,"/>");
    opts.content = content;

*/
