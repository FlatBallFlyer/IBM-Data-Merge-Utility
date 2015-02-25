
/*
 * Editor client script for DB table contact
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
	var theCustomer=0;
	var contactTable;
	
	var editor = new $.fn.dataTable.Editor( {
		"table": "#contact",
	    "ajax": {
	        url: "php/table.contact.php",
	        type: "POST",
	        data: function ( d ) {d.customerId = theCustomer;}
	    },
		"fields": [
			{
				name: "contact.idcustomer",
				type: "hidden",
				def: function () {return theCustomer;}
			},
			{
				"label": "Name",
				"name": "contact.name",
				"type": "text",
			},
			{
				"label": "Contact Preference",
				"name": "contact.preference",
				"type": "text",
			},
			{
				"label": "eMail",
				"name": "contact.email",
				"type": "text",
			},
			{
				"label": "Phone",
				"name": "contact.phone",
				"type": "text"
			}
		]	
	} );

	contactTable = $('#contact').DataTable( {
		"dom": "Tfrtip",
	    "ajax": {
	        url: "php/table.contact.php",
	        type: "POST",
	        data: function ( d ) {d.customerId = theCustomer;}
	    },
		"columns": [
			{ "data": "contact.name"   },
			{ "data": "contact.preference"   },
			{ "data": "contact.email"   },
			{ "data": "contact.phone" }
		],
		"tableTools": {
			"sRowSelect": "os",
			"aButtons": [
							{ "sExtends": "editor_create", "editor": editor },
							{ "sExtends": "editor_edit",   "editor": editor },
							{ "sExtends": "editor_remove", "editor": editor }
						]
		}
	} );
	
	$('#contactListener').on('customerSet', function( event, name, id ) {
		$('#contactListener').html( name ); 
		theCustomer = id;
		contactTable.ajax.reload();
	});	
		
} );

}(jQuery));

