$(document).ready(function() {
	$.ajax({
		type : "POST",
		headers: {
			"Content-Type": "application/json",
			"Accept": "application/json"
		},
		contentType: "application/json",
		url : "http://localhost:8080/myapp/searchresource/getIt",
		data: JSON.stringify({content: "image", name : "sdfsdfdf"}),
		success : function(data) {
			alert(data.name);
		},
		//dataType: 'json'
	});
});