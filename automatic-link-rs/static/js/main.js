$(document).ready(function() {
	$.ajax({
		type : "GET",
		contentType: "application/json",
		url : "http://localhost:8080/rest/api/docs",
		//data: JSON.stringify({content: "ngoc", name : "sdfsdfdf"}),
		success : function(data) {
			alert(data.name);
		},
		//dataType: 'json'
	});
});