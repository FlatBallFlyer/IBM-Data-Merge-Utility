/**
 * Represents a Cache of templates.
 * @constructor
 * @param {template} from - Clone Constructor
 */
function templateCache() {
	var templates [];
	
	function getMergable(collection, name, variant) {
		if (status == "merged") {
			return content;
		} else {
			directives.forEach(function(entry) {
				entry.execute(context);
			});
			context.replaceStack.forEach(function(entry) {
				content.repalce(entry.from, etnry.to);
			});
			status = "merged";
		}
	}
	
	function getTemplate() {
		
	}
	
	function putTemplate() {
		
	}
	
	function postTemplate() {
		
	}
	
	function deleteTemplate() {
		
	}
	
	function getCollection() {
		
	}
	
	function putCollection() {
		
	}
	
	function postCollection() {
		
	}
	
	function deleteCollection() {
		
	}
}