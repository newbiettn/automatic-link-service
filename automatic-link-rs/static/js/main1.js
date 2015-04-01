var map = {
		"name" : "",
		"children" : []
};
var link = [];

function handleSearchInput() {
	var search_query = $('.search-field').val();
	$.ajax({
		beforeSend : function() {
			$('.result-container').html('');
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
			processData(data);
			visualize();
		},
		dataType : 'json'
	});
}
function processData(data) {
	var data_length = data.length;
	for (var i = 0; i < data_length; i++) {
		var cluster = {
			"name" : data[i]["cluster_label"],
			children : []
		};
		if (data[i]["results"] != null) {
			for (var j = 0; j < (data[i]["results"]).length; j++) {
				var connect = [];
				connect = ["37956", "9539"];
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

var w = 1280,
	h = 800,
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

var div = d3.select("body").insert("div", "h2")
	.style("top", "90px")
	.style("left", "100px")
	.style("width", w + "px")
	.style("height", w + "px")
	.style("position", "absolute")
	.style("-webkit-backface-visibility", "hidden");

var svg = div.append("svg:svg")
	.attr("width", w)
	.attr("height", w)
	.append("svg:g")
	.attr("transform", "translate(" + rx + "," + ry + ")");

function visualize() {
	
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
		.on("mouseover", mouseover)
		.on("mouseout", mouseout)
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
}
function mouseout(d) {
	svg.selectAll('.link')
		.classed('link-dim', false);
	svg.selectAll('.node')
		.classed('link-dim', false);
}
d3.json("js/data.json", function(data) {
	//console.log(data);
	processData(data);
	//console.log(map);
	visualize();
	
});

//$(document).ready(function() {
//	$('.search-btn').click(function() {
//		handleSearchInput();
//	});
//	$(document).keypress(function(e) {
//		if (e.which == 13) {
//			handleSearchInput();
//		}
//	});
//});