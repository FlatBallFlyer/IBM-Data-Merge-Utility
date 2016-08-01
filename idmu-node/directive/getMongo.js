/**
 * Represents a getSql Directive.
 * @constructor
 */
function getSql() {
	var template object;
	var status = "pending";
	var softFail = false;
	var objectName = "";
	var sqlColumns = "";
	var sqlTables = "";
	var sqlWhere = "";
	var sqlOrder = "";
	var resultSet;
	
	function execute(context) {
		if (status == "pending") {
			status = "running";
			var sql = "SELECT " + sqlColumns + " FROM " + sqlTables + " WHERE " + sqlWhere + " ORDER BY " + sqlOrder;
			sqlExecute(sql, function(results) {
				//success
				resultSet = results;
				template.merge(context);
			}, function(error) {
				// template.fail
 			});
		} else if (status == "running") {
			// for each row, add element to objectname
			status = "executed";
		}
		return;
	}
}