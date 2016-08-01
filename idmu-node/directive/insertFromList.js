/**
 * Represents a getSql Directive.
 * @constructor
 */
function insertFromList() {
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
			var theList = context.property(objectName);
			theList.forEach( function(entry) {
				for (var bookmark in template.bookmarks) {
					if (bookmark.name.regex(bookmarkPattern)) {
						// newTemplate get template(collection, name, object.property(varyOn));
						bookmark.insert(newTemplate.merge());
					}
				}
			});
			status = "executed";
		}
		return;
	}
}