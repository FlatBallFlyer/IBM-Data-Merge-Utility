/**
 * Represents a Add Replace from Object Directive.
 * @constructor
 */
function replaceFromObject() {
	var template object;
	var status = "pending";
	var softFail = false;
	var objectName = "";
	var encoding = "none";
	
	function execute(context) {
		if (status == "pending") {
			// todo type checking
			var theObject = context.property(objectName);
			for (var property in theObject) {
			    if (theObject.hasOwnProperty(property)) {
					context.addReplaceValue(encoding, property, theObject.property(property));
			    }
			}
			status = "executed";
		}
		return;
	}
}