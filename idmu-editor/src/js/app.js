/**
* @jsx React.DOM
*/

function supports_history_api() {
  return !!(window.history && history.pushState);
}


var ALL_DIRECTIVES = [
  {
    "type":0,
    "name": "Require Tags",
    "description":"Directive Description (Optional)",
    "softFail":true,
    "tags":["Tag1"]
  },
  {
    "type":1,
    "name": "Replace Value",
    "description": "Directive Description (Optional)",
    "softFail":true,
    "from": "The value to replace (will be wrapped in {brackets})",
    "to": "The value to replace with"
  },
  {
    "sequence": 1,
    "type": 2,
    "name": "Insert Template From Tag Data",
    "softFail": false,
    "description": "TestInsertSubsTag",
    "collectionName": "",
    "collectionColumn": "",
    "notLast": ["empty"],
    "onlyLast": [],
    "provider": {
      "type": 2,
      "condition": 0,
      "tag": "Foo",
      "value": "",
      "list": false
    }
  },
  {
    "sequence": 1,
    "type": 10,
    "name": "Insert Template From SQL Rows",
    "softFail": false,
    "description": "TestInsertSubsSql",
    "collectionName": "",
    "collectionColumn": "",
    "notLast": ["empty"],
    "onlyLast": [],
    "provider": {
      "type": 1,
      "source": "",
      "columns": "A,B,C,1,2,3,4,5,6",
      "from": "",
      "where": ""
    }
  },
  {
    "sequence": 1,
    "type": 11,
    "name": "Replace From SQL Row",
    "softFail": false,
    "description": "TestReplaceRowSql",
    "provider": {
      "type": 1,
      "source": "",
      "columns": "A,B,C,1,2,3,4,5,6",
      "from": "",
      "where": ""
    }
  },
  {
    "sequence": 1,
    "type": 12,
    "name": "Replace From SQL Columns",
    "softFail": false,
    "description": "TestReplaceColSql",
    "fromColumn": "Foo",
    "toColumn": "",
    "provider": {
      "type": 1,
      "source": "",
      "columns": "A,B,C,1,2,3,4,5,6",
      "from": "",
      "where": ""
    }
  },
  {
    "sequence": 1,
    "type": 21,
    "name": "Insert Templates From CSV",
    "softFail": false,
    "description": "TestInsertSubsCsv",
    "collectionName": "",
    "collectionColumn": "",
    "notLast": ["empty"],
    "onlyLast": [],
    "provider": {
      "type": 3,
      "staticData": "A,B,C\n1,2,3\n4,5,6",
      "url": "",
      "tag": ""
    }
  },
  {
    "sequence": 1,
    "type": 22,
    "name": "Replace From CSV Row",
    "softFail": false,
    "description": "TestReplaceRowCsv",
    "provider": {
      "type": 3,
      "staticData": "A,B,C\n1,2,3\n4,5,6",
      "url": "",
      "tag": ""
    }
  },
  {
    "sequence": 1,
    "type": 23,
    "name": "Replace From CSV Column",
    "softFail": false,
    "description": "TestReplaceColCsv",
    "fromColumn": "Foo",
    "toColumn": "",
    "provider": {
      "type": 3,
      "staticData": "A,B,C\n1,2,3\n4,5,6",
      "url": "",
      "tag": ""
    }
  },
  {
    "sequence": 1,
    "type": 31,
    "name": "Insert Template From HTML Table",
    "softFail": false,
    "collectionName": "",
    "collectionColumn": "",
    "notLast": ["empty"],
    "onlyLast": [],
    "description": "TestInsertSubsHtml",
    "provider": {
      "type": 4,
      "staticData": "A,B,C\n1,2,3\n4,5,6",
      "url": "",
      "tag": ""
    }
  },
  {
    "sequence": 1,
    "type": 32,
    "name": "Replace From HTML Rows",
    "softFail": false,
    "description": "TestReplaceRowHtml",
    "provider": {
      "type": 4,
      "staticData": "A,B,C\n1,2,3\n4,5,6",
      "url": "",
      "tag": ""
    }
  },
  {
    "sequence": 1,
    "type": 33,
    "name": "Replace From HTML Column",
    "softFail": false,
    "description": "TestReplaceColHtml",
    "fromColumn": "Foo",
    "toColumn": "",
    "provider": {
      "type": 4,
      "staticData": "A,B,C\n1,2,3\n4,5,6",
      "url": "",
      "tag": ""
    }
  },
  {
    "sequence": 1,
    "type": 34,
    "name": "Replace From HTML Pattern",
    "softFail": false,
    "description": "TestMarkupSubsHtml",
    "pattern": "TestPattern",
    "provider": {
      "type": 4,
      "staticData": "A,B,C\n1,2,3\n4,5,6",
      "url": "",
      "tag": ""
    }
  }
];

$( document ).ready(function(){
  // Dropdown menu
  $(".dropdown-button").dropdown();
  React.initializeTouchEvents(true);
  React.render(
    <App />,
    document.getElementById("app_view")
  );
});
