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
