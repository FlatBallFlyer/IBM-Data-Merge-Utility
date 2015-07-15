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
  }
};

/*
    var content_raw = this.refs.template_body.state.content,
        content = content_raw.replace(/<a(>|.*?[^?]>)/g,"");
    content = content.replace(/div class=\"tkbookmark\"/g,"tkBookmark");
    content = content.replace(/><\/div>/g,"/>");
    opts.content = content;

*/
