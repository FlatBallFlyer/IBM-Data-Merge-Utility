var express = require("express");
var fs = require("fs");
var app = express();
var path = require("path");

app.get("/test", function(req, res, next) {
  res.json({});
});

app.use("/", express.static("."));

app.get("/idmu", function(req, res, next) {
  res.sendFile("src/html/app.html", {root: __dirname + "../../../"});
});

app.get("/idmu/templates", function(req, res, next) {
  res.sendFile("tools/test/templates.json", {root: __dirname + "../../../"});
});

app.get("/idmu/directives", function(req, res, next) {
  res.sendFile("tools/test/directives.json", {root: __dirname + "../../../"});
});

app.get("/idmu/templates/:collection_id/:template_id", function(req, res, next) {
  var column_value = req.query.columnValue ?  "."+req.query.columnValue : "";
  var template = req.params.collection_id+"."+req.params.template_id+column_value+".json";
  var file_name = "tools/test/"+template;
  console.log("template name="+file_name);
  fs.exists(file_name, function (exists) {
    if(exists){
      res.sendFile(file_name, {root: __dirname + "../../../"});
    }else{
      res.sendFile("tools/test/template.json", {root: __dirname + "../../../"});
    }
  });
});


var port = process.argv.length > 2 ? process.argv[2] : 9000;
console.log("Starting server at port " + port);
var server = app.listen(port);

module.exports = app;
