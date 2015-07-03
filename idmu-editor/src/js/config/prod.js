var CONFIG_KEYS = {
  "ga.id": ""
};

var config = (function(keys) {

  function getValue(key) {
    return keys[key] !== undefined ? keys[key] : "";
  }

  return getValue;
})(CONFIG_KEYS || {});
