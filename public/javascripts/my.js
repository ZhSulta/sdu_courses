$(document).ready(function(){
	
	$('#sortBy a').click(function (e) {
		var param = window.location.search.replace( "?", "" ).split('&')[0].split('=')[1];
		e.preventDefault();
		$('#sortBy #'+param).tab('show');
	});
	
});		
		