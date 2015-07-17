var CONFIG_KEYS = {
  "ga.id": "",
  "max_depth": 5
};

var config = (function(keys) {

  function getValue(key) {
    return keys[key] !== undefined ? keys[key] : "";
  }

  return getValue;
})(CONFIG_KEYS || {});
