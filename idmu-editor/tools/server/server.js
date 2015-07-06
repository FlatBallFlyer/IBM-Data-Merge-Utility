var express = require("express");
var fs = require("fs");
var app = express();
var path = require("path");
var bodyParser = require("body-parser");

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
  console.log("length="+templates[collection_id].ribbon.length);
  for(var idx=0; idx<templates[collection_id].ribbon.length; idx++){
    if(templates[collection_id].ribbon[idx].name === template.name){
      found = true;
    }
  }
  console.log("found="+found);
  if(!found) {
    var rx = {
      name: template.name,
      label: template.description
    };
    if(template.columnValue){
      rx['columnValue'] = template.columnValue;
    }
    templates[collection_id].ribbon.push(rx);
    fs.writeFileSync("tools/test/templates.json", JSON.stringify(templates, null, 2));
  }

  res.sendFile("tools/test/template.json", {root: __dirname + "../../../"});
});


var port = process.argv.length > 2 ? process.argv[2] : 9000;
console.log("Starting server at port " + port);
var server = app.listen(port);

module.exports = app;
