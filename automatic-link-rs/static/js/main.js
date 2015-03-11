$(document).ready(function() {
	$('.search-btn').click(function(){
		handleSearchInput();
	});
	$(document).keypress(function(e) {
		if (e.which == 13) {
			handleSearchInput();
		}
	});
});
function handleSearchInput() {
	var search_query = $('.search-field').val();
	$.ajax({
		beforeSend: function() {
			$('.result-container').html('');
			$('.loading').css({display: "block"});
		},
		type : "POST",
		contentType : "application/json",
		url : "http://localhost:8080/rest/api/docs",
		data : JSON.stringify({
			content : search_query,
			name : "sdfsdfdf"
		}),
		success : function(data) {
			$('.loading').css({display: "none"});
			console.log(data);
			generateResult();
			var cluster_names = getClusterName(data);
			var tree_data = prepareDataForTree(cluster_names);
			$('.tree').tree({
				data : tree_data
			});
			var doc_list = getDocListByClusterLabel(data, cluster_names[0]);
			displayDocList(doc_list);
			onClickNode(data);
		},
		dataType : 'json'
	});
}
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
	console.log(doc_list.length);
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
	$('.result-list').html('');
	for (var i = 0; i < length; i++) {
		var my_doc = doc_list[i]['myDoc'];
		var file_name = my_doc['fileName'];
		var fragment = my_doc['fragment'];
		var uri = my_doc['uri'];
		var linked_docs = doc_list[i]['linkedDocuments'];
		
		var html_linked_docs = "";
//		if (typeof linked_docs != 'undefined') {
			html_linked_docs = getHTMLDisplayLinkedDocs(linked_docs);
//		}
		
		
		var str = '<article class="large-12 columns search-item">';
		str += '<section class="item-detail">';
		str += '<header class="item-title">' + file_name + '</header>';
		str += '<div class="item-fragment">' + fragment + '</div>';
		str += '<div><span class="item-detail-uri">URI: </span> <span class="item-path"><a target="_blank" href="file://' + uri + '">' + uri + '</a></span></div>';
		str += '<div class="item-fragment">Have links to: ' + html_linked_docs + '</div>';
		str += '</section>';
		str += '</article>';

		$('.result-list').append(str);
	}
}
function getHTMLDisplayLinkedDocs(linked_docs) {
	var str = "";
	console.log(linked_docs.length);
	for (var i = 0; i < linked_docs.length; i++) {
		var my_doc = linked_docs[i];
		var file_name = my_doc['fileName'];
		var uri = my_doc['uri'];
		str += "<a href='file://" + uri + "'>" +file_name + "</a>";
		str += " ";
	}
	return str;
}
function onClickNode(data) {
	$('.tree').bind('tree.click', function(event) {
		var node = event.node;
		var doc_list = getDocListByClusterLabel(data, node.name);
		displayDocList(doc_list);
	});
}
function generateResult() {
	var str = '<div class="large-4 columns cluster-list-container">';
	str += '<div class="row">';
	str += '<h5 class="large-12 columns section-header cluster-list-container-header ">';
	str += 'Clustered results';
	str += '</h5>';
	str += '</div>';
	str += '<div class="row">';
	str += '<div class="large-12 columns tree"></div>';
	str += '</div>';
	str += '</div>';
	str += '<div class="large-8 columns doc-list-container">';
	str += '<div class="row">';
	str += '<h5 class="large-12 columns section-header doc-list-container-header">';
	str += 'Top search in results in the clusters';
	str += '</h5>';
	str += '</div>';
	str += '<div class="row result-list"></div>';
	str += '</div>';
	$('.result-container').html('').append(str);
}