/**
 * 
 */
var $frame;
$(document).ready( function() {
	$('#livePageContent').on('click', '.insert', addCollection);
	$('#livePageContent').on('click', '.add', function() {
		addTemplate($(this).closest('.collection'));
	});

	// Call Sly on frame
	initFrame($("#rootFrame"), $frame);
	var $collection = $('#rootCollection');
	addTemplate($collection, $frame);
	addTemplate($collection, $frame);
	addTemplate($collection, $frame);

	alert('ready');
});
function initFrame($node, $frame) {
	$frame = new Sly("#rootFrame", {
		horizontal: true,
		itemNav: 'basic',
		smart: true,
		activateOn: 'click',
		scrollBar: '.scrollbar',
		dragHandle: true,
		dynamicHandle: true,
		clickBar: true,

		// Buttons
		prev: '.prev',
		next: '.next'
	});
	$frame.init();	
}

function addTemplate($collection, $frame) {
	var $template = $('#TEMPLATE_ID').clone();
	$template.attr("id",'c1')
			.removeClass('hidden');
	$collection.find('.slidee').append($template);
	$frame.reload();
}

function addCollection() {
	var $collection = $('#COLLECTION_ID').clone();
	$collection.attr("id", 'f1')
			.removeClass('hidden');
	$(this).before($collection);
	$frm = new Sly
	addTemplate($collection);
}

