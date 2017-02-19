define(["jquery","bootstrap/modal"], function($,bm) {
	$(document).on('click', '[data-toggle="lightbox"]', function(event) {
	    event.preventDefault();
	    $(this).ekkoLightbox();
	});
   
});