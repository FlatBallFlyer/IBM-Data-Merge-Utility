/**
 * Represents a Add Replace from List Directive.
 * @constructor
 */
function replaceFromObject() {
	var template object;
	var status = "pending";
	var softFail = false;
	var objectName = "";
	var fromProperty = "";
	var toProperty = "";
	var encoding;
	
	function execute(context) {
		if (status == "pending") {
			// todo - type checking
			var list = context.property(objectName);
			list.forEach(function(entry) {
				context.addReplaceValue(encoding, 
						entry.property(fromProperty), 
						entry.property(toProperty));
			});
			status = "executed";
		}
		return;
	}
}