
/*
 * Editor client script for DB table customer
 * Copyright 2015, 2015 IBM
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
  
(function($){

$(document).ready(function() {
	var customerTable;
	var theCustomer;
	
	var editor = new $.fn.dataTable.Editor( {
		"ajax": "php/table.customer.php",
		"table": "#customer",
		"fields": [
			{
				"label": "Customer Name",
				"name": "customer.name",
				"type": "text"
			},
			{
				"label": "Primary Contact",
				"name": "customer.primary",
				"type": "text"
			},
			{
				"label": "Address",
				"name": "customer.street",
				"type": "text"
			},
			{
				"label": "City",
				"name": "customer.city",
				"type": "text"
			},
			{
				"label": "State",
				"name": "customer.state",
				"type": "text"
			},
			{
				"label": "Zip",
				"name": "customer.zip",
				"type": "text"
			},
			{
				"label": "Phone",
				"name": "customer.phone",
				"type": "text"
			}
		]
	} );

	$('#customer').dataTable( {
		"dom": "Tfrtip",
		"ajax": "php/table.customer.php",
		"columns": [
			{"data": "customer.name"	},
			{"data": "customer.revenue"	},
			{"data": "customer.profit"	},
			{"data": "customer.primary"	},
			{"data": "customer.city"	},
			{"data": "customer.state"	},
			{"data": "customer.phone"	}
		],
		"tableTools": {
			"sRowSelect": "os",
			"fnRowSelected": function ( nodes ) {setCustomer(customerTable, nodes);},			
			"aButtons": [
				{ "sExtends": "editor_create", "editor": editor },
				{ "sExtends": "editor_edit",   "editor": editor },
				{ "sExtends": "editor_remove", "editor": editor }
			]
		}
	} );
	customerTable = $('#customer').DataTable();

	function setCustomer(table, nodes) {
		theCustomer = table.row(nodes).data().customer.idcustomer;
		theName = table.row(nodes).data().customer.name;
		var event = new CustomEvent('customerSet');
		$( ".listener" ).trigger( "customerSet", [theName, theCustomer]  );
	}

} );

}(jQuery));

