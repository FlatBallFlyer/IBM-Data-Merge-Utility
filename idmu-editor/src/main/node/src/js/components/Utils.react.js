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
