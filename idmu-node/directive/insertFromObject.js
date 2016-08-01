/**
 * Represents a getSql Directive.
 * @constructor
 */
function insertFromObject() {
	var template object;
	var status = "pending";
	var softFail = false;
	var objectName = "";
	var bookmarkPattern = ".*";
	var notLast = [];
	var onlyLast = [];
	var onlyFirst = [];
	
	function execute(context) {
		if (status == "pending") {
			status = "running";
			var theObject = context.property(objectName);
			for (var property in theObject) {
				for (var bookmark in template.bookmarks) {
					if (bookmark.name.regex(bookmarkPattern)) {
						// newTemplate get template(collection, name, object.property(varyOn));
						bookmark.insert(newTemplate.merge());
					}
				}
			}
			status = "executed";
		}
		return;
	}
}