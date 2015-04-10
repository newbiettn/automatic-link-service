var map;

var link;

function emptyData () {
	map = {
			"name" : "",
			"children" : []
	};
}

function handleSearchInput() {
	var search_query = $('input[name="content"]').val();
	$.ajax({
		beforeSend : function() {
			$('.results').css({display: "none"});	
			$('#panel2-1').html('');
			$('.loading').css({
				display : "block"
			});
		},
		type : "POST",
		contentType : "application/json",
		url : "http://localhost:8080/rest/api/docs",
		data : JSON.stringify({
			content : search_query,
			name : "sdfsdfdf"
		}),
		success : function(data) {
			$('.loading').css({
				display : "none"
			});
			$('.results').css({display: "block"});	
			processData(data);
			visualize();
			
			generateResult();
			var cluster_names = getClusterName(data);
			var tree_data = displayClusterData(cluster_names);
			$('.cluster-label').html(tree_data);
			var doc_list = getDocListByClusterLabel(data, cluster_names[0]);
			displayDocList(doc_list);
			onClickNode(data);
		},
		dataType : 'json'
	});
}
function processData(data) {
	map = {
			"name" : "",
			"children" : []
	};
	
	var data_length = data.length;
	var particular;
	for (var i = 0; i < data_length; i++) {
		var cluster = {
			"name" : data[i]["cluster_label"],
			children : []
		};
		if (data[i]["results"] != null) {
			for (var j = 0; j < (data[i]["results"]).length; j++) {
				if (i == 0 && j==0) {
					particular = data[i]["results"][j]["myDoc"]["fileName"];
				}
				console.log(particular);
				var connect = [];
				connect = [particular];
				var doc = {
					"name" : data[i]["results"][j]["myDoc"]["fileName"],
					"connect": connect,
					"cluster" : i
				}
				cluster.children.push(doc);
			}
			map.children.push(cluster);
		}
	}
}


var svg;

function visualize() {
	link = [];
	
	var w = 850,
		h = 850,
		rx = w / 2,
		ry = h / 2,
		m0,
		rotate = 0;
	
	var splines = [];
	
	var cluster = d3.layout.cluster()
		.size([ 360, ry - 120 ])
		.sort(function(a, b) {
			return d3.ascending(a.key, b.key);
		});
	
	var bundle = d3.layout.bundle();
	
	var line = d3.svg.line.radial()
		.interpolate("bundle")
		.tension(.65)
		.radius(function(d) { return d.y; })
		.angle(function(d) { return d.x / 180 * Math.PI; });
	
	var div = d3.select("#panel2-1").insert("div")
		.style("left", "0px")

	d3.select(".cluster-visualization")
		.style("height", h + "px")
	d3.select(".cluster-label")
		.style("height", h + "px")
	svg = div.append("svg:svg")
		.attr("width", w)
		.attr("height", w)
		.append("svg:g")
		.attr("transform", "translate(" + rx + "," + ry + ")");

	var nodes = cluster.nodes(map);
	link = createLink(nodes);
	splines = bundle(link);
	
	var path = svg.selectAll("path.link")
    	.data(link)
    	.enter().append("svg:path")
    	.attr("class", function(d) {
    		return "link source-" + d.source.name + " target-" + d.target.name; })
    	.attr("d", function(d, i) { return line(splines[i]); });
	
	svg.selectAll("g.node")
		.data(nodes.filter(function(n) { return !n.children; }))
		.enter().append("svg:g")
		.attr("class", "node")
		.attr("id", function(d) { return "node-" + d.name; })
		.attr("transform", function(d) { return "rotate(" + (d.x - 90) + ")translate(" + d.y + ")"; })
		.append("svg:text")
		.attr("dx", function(d) { return d.x < 180 ? 8 : -8; })
		.attr("dy", ".10em")
		.attr("text-anchor", function(d) { return d.x < 180 ? "start" : "end"; })
		.attr("transform", function(d) { return d.x < 180 ? null : "rotate(180)"; })
		.style('fill', function(d){
			return getColor(d.cluster);
   	   	})
		.text(function(d) { return d.name; })
		.on("click", mouseover)
		.on("mouseout", mouseout);
	
	$('.node').mousemove(setPopupPosition);
}
function createLink(nodes) {
	var map = {}, imports = [];
	
	nodes.forEach(function(d) {
		map[d.name] = d;
    });
	
	nodes.forEach(function(d) {
		if (d.connect) {
			d.connect.forEach(function(n){
				imports.push({source: map[d.name], target: map[n]});
			});
		}
	});
	return imports;
}
function getColor(val){
	var color;
	if(val == 1){
		color= '#DF3A01'
	} else if (val == 2){
		color = '#B45F04'
	} else if (val == 3){
		color = '#B18904'
	} else if (val == 4){
		color = '#868A08'
	} else if (val == 5){
		color = '#0489B1'
	} else if (val == 6){
		color = '#B404AE'
	} else if (val == 7){
		color = '#8A0886'
	} else if (val == 8){
		color = '#DF01A5'
	}
	
	return color
}
function mouseover(d) {
	
	svg.selectAll('.node')
		.classed('link-dim', true);
	svg.selectAll('.link')
		.classed('link-dim', true);
	
	d.connect.forEach(function(n){
		svg.select("#node-" + n)
			.classed('link-dim', false);
	});
	svg.select("#node-" + d.name)
		.classed('link-dim', false);
	svg.selectAll(".source-" + d.name)
		.classed('link-dim', false);
	
	
	$("#node-info").empty();
	$("#gameTemplate").tmpl( {
		name: d.name,
		rating: "5"	
	}).appendTo( "#node-info" );
	$("#node-info").show();
}
function mouseout(d) {
	svg.selectAll('.link')
		.classed('link-dim', false);
	svg.selectAll('.node')
		.classed('link-dim', false);
	
	$("#node-info").hide();
}
function setPopupPosition(e){
	e = jQuery.event.fix(e);
	mouseX = e.pageX //- $('#gia-interactive').offset().left
	mouseY = e.pageY
	
	$('.gia-popup').css({
		top: mouseY,
		left: mouseX
	})

}
d3.json("js/data.json", function(data) {
	//processData(data);
	//visualize();
	
	//list
//	generateResult();
//	var cluster_names = getClusterName(data);
//	var tree_data = displayClusterData(cluster_names);
//	$('.cluster-label').html(tree_data);
//	var doc_list = getDocListByClusterLabel(data, cluster_names[0]);
//	displayDocList(doc_list);
//	onClickNode(data);
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
	console.log(doc_list.length);
	return doc_list;
}
function displayClusterData(cluster_names, data) {
	var str = '<div>';
	var length = cluster_names.length;
	for (var i = 0; i < length; i++) {
		var name = cluster_names[i];
		name = name.replace(/\s+/g, '');
		str += '<h5 class="cluster-name">';
		str += '<a data-name="'+ cluster_names[i] + '" class="cluster_name">';
		str += cluster_names[i];
		str += '</a>';
		str += '</h5>';	
	}
	str += '</div>';
	return str;
}
function displayDocList(doc_list) {
	var length = doc_list.length;
	$('.list_content').html('');
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
		
		var str = '<article class="large-35 search-item">';
		str += '<section class="item-detail">';
		str += '<header class="item-title">' + file_name + '</header>';
		str += '<div class="item-fragment">' + fragment + '</div>';
		str += '<div><span class="item-detail-uri">URI: </span> <span class="item-path"><a target="_blank" href="file://' + uri + '">' + uri + '</a></span></div>';
		str += '<div class="item-fragment">Have links to: ' + html_linked_docs + '</div>';
		str += '</section>';
		str += '</article>';
		$('.list_content').append(str);
		
		$('#panel2-2').pajinate({
			num_page_links_to_display : 10,
			items_per_page : 6,
			item_container_id : '.list_content',
			nav_panel_id : '.page_navigation',
			nav_label_first : '<<',
			nav_label_last : '>>',
			nav_label_prev : '<',
			nav_label_next : '>',
		});
		$('.less').hide();
		$('.more').hide();
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
	$('.cluster_name').click(function(){
		cluster_name = $(this).data('name');
		console.log(cluster_name);
		var doc_list = getDocListByClusterLabel(data, cluster_name);
		displayDocList(doc_list);
	});
	
	
}
function generateResult() {
	var str = '<div class="row result-list"></div>';
	str += '</div>';
	$('.list_content').html('').append(str);
}

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