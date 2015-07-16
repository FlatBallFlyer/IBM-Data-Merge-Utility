var express = require("express");
var fs = require("fs");
var app = express();
var path = require("path");
var bodyParser = require("body-parser");

app.use(function(req, res, next) {
  res.header("Access-Control-Allow-Origin", "*");
  res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
  next();
});

app.use("/", express.static("."));
app.use(bodyParser.json());       // to support JSON-encoded bodies
app.use(bodyParser.urlencoded({   // to support URL-encoded bodies
  extended: true
}));
app.use("/", express.static("."));


app.get("/idmu", function(req, res, next) {
  res.sendFile("src/html/app.html", {root: __dirname + "../../../"});
});

app.get("/idmu/templates", function(req, res, next) {
  res.sendFile("tools/test/templates.json", {root: __dirname + "../../../"});
});

app.get("/idmu/templates/:collection_name",function(req,res,next){
  var 
          templates = JSON.parse(fs.readFileSync("tools/test/templates.json", 'utf8')),
          collection_name = req.params.collection_name,
          result = [];

  for(var idx=0; idx<templates.length; idx++){
    var tpl = templates[idx];
    if(tpl.collection === collection_name){
      result.push(tpl);
    }
  }
  res.json(result);
});

app.get("/idmu/directives", function(req, res, next) {
  res.sendFile("tools/test/directives.json", {root: __dirname + "../../../"});
});

app.get("/idmu/template/:collection_id/:template_id", function(req, res, next) {
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



app.put("/idmu/templates/:collection_id", function(req, res, next) {
  var template = req.body.template;
  console.log(template);
  var collection_id = req.params.collection_id;
  var column_value = template.columnValue ?  "."+template.columnValue : "";
  var template_name = collection_id+"."+template.name+column_value+".json";
  var file_name = "tools/test/"+template_name;
  console.log("template name="+file_name);

  //first save the template
  fs.writeFile(file_name, JSON.stringify(template, null, 2), function(err) {
    if(err) {
      console.log(err);
    }else {
      console.log("JSON saved to " + file_name);
    }
  });

  //load 'templates' and modify the ribbon if needed .. and save
  var found = false;
  var templates = JSON.parse(fs.readFileSync("tools/test/templates.json", 'utf8'));
  var idx = 0;
  for(idx=0; idx<templates.length; idx++){
    var tpl = templates[idx];
    var lhs = tpl.collection+"."+tpl.name+"."+(tpl.columnValue ? tpl.columnValue : "");
    var rhs = collection_id + "." + template.name + (template.columnValue ? template.columnValue : "");
    if(lhs === rhs){
      found = true;
      break;
    }
  }
  console.log("found="+found);
  if(!found) {
    templates.push(template);
    fs.writeFileSync("tools/test/templates.json", JSON.stringify(templates, null, 2));
  }

  res.sendFile("tools/test/template.json", {root: __dirname + "../../../"});
});


var port = process.argv.length > 2 ? process.argv[2] : 9000;
console.log("Starting server at port " + port);
var server = app.listen(port);

module.exports = app;
