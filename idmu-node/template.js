/**
 * Represents a template.
 * @constructor
 * @param {template} from - Clone Constructor
 */
function template() {
	var collection string;
	var name string;
	var variant string;
	var content string;
	var contentType string;
	var contentDisposition string;
	var status = "ready";
	var directives [];
	var bookmarks [];
	
	function merge(context) {
		if (status == "merged") {
			return content;
		} else {
			directives.forEach(function(entry) {
				entry.execute(context);
			});
			replaceAll(context);
			status = "merged";
			context.replaceStack.forEach(function(entry) {
				content.repalce(entry.from, etnry.to);
			});
		}
	}
}