
var i18n = (function(keys) {

  function getValue(key) {
    return keys[key] !== undefined ? keys[key] : "???";
  }

  return getValue;
})(I18N_KEYS || {});

var locale = (function() {
  return function() { return i18n("name"); };
})();

var i18nFormat = function(message, param) {
  if(message && param) {
    return message.replace("%s", param);
  } else {
    if(message && !param) {
      return message;
    } else if(param) {
      return param;
    }
  }
  return "";
};

window.addEventListener("load", function() {
  moment.locale(locale());
  numeral.language(locale());
});
