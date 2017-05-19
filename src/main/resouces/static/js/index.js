$(document).ready(function(){
	/*loadImgs(
		[
		"img/bgPic.jpg",
		"img/01.jpg"
		]
	,$("#loadImg"));*/
})

function loadComplete(){
	$(".loading-container").delay(1000).slideUp(500);
	setTimeout(function(){
		$('#cover-heading').addClass("animated bounce");
		$('#text').addClass("animated fadeInUp");
		$('#contactBtn').addClass("animated fadeInUp");
	},1100);
}