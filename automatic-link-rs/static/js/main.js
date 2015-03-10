$(document).ready(function() {
	$('.search-btn').click(function(){
		$searchStr = $('.search-field').val();
		$.ajax({
			type : "GET",
			contentType: "application/json",
			url : "http://localhost:8080/rest/api/docs",
			data: JSON.stringify({content: "ngoc", name : "sdfsdfdf"}),
			success : function(data) {
				var cluster_names = getClusterName(data);
				var tree_data = prepareDataForTree(cluster_names);
				$('.tree').tree({
					data : tree_data
				});
				var doc_list = getDocListByClusterLabel(data, cluster_names[0]);
				displayDocList(doc_list);
			},
			dataType: 'json'
		});
	});
});
function getClusterName(search_result) {
	var cluster_names = [];
	var length = search_result.length;
	for (var i = 0; i < length; i++) {
		var cluster_label = search_result[i]["cluster_label"];
		cluster_names[i] = cluster_label;
	}
	return cluster_names;
}
function getDocListByClusterLabel(search_result, cluster_label) {
	var doc_list = [];
	var length = search_result.length;
	for (var i = 0; i < length; i++) {
		var label = search_result[i]["cluster_label"];
		if (cluster_label == label) {
			doc_list = search_result[i]['results'];
			break;
		}
	}
	return doc_list;
}
function prepareDataForTree(cluster_names) {
	var tree_data = [];
	var length = cluster_names.length;
	for (var i = 0; i < length; i++) {
		var node = {};
		node['label'] = cluster_names[i];
		tree_data[i] = node;
	}
	return tree_data;
}
function displayDocList(doc_list) {
	var length = doc_list.length;
	for (var i = 0; i < length; i++) {
		var my_doc = doc_list[i]['myDoc'];
		var file_name = my_doc['fileName'];
		var fragment = my_doc['fragment'];
		var uri = my_doc['uri'];
		
		var str = '<article class="row search-item">';
		str += '<figure class="large-1 columns item-figure type-pdf"></figure>';
		str += '<section class="large-11 columns item-detail">';
		str += '<header class="item-title">' + file_name + '</header>';
		str += '<div class="item-fragment">' + fragment +'</div>';
		str += '<div class="item-path">' + uri +'</div>';
		str += '</section>';
		str += '</article>';
		
		$('.result-list').append(str);
	}
}