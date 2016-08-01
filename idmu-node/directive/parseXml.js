/**
 * Represents a Add Parse Json Directive.
 * @constructor
 */
function parseJson() {
	var template object;
	var status = "pending";
	var softFail = false;
	var objectName = "";
	var fromObject = "";
	var fromUrl = "";
	var fromValue = "";
	
	function execute(context) {
		if (status == "pending") {
			if fromObject != "" {
				context.property(objectName) = json.parse(context.property(fromObject);
				status = "executed";
			} else if fromUrl != "" {
				status = "running";
				// get url success=function(value) {
					fromValue = value;
					template.merge(context)
					} fail=function(error) {
						template.error(context, softFail, error);
					}
			} else if fromValue != "" {
				context.property(objectName) = json.parse(fromValue);
				status = "executed";
			}
		} else if (status == "running") {
			context.property(objectName) = json.parse(Value);
			status = "executed";
		}
		return;
	}
}