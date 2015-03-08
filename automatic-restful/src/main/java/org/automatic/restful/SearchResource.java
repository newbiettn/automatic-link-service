package org.automatic.restful;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import linkservice.clustering.methods.ClusteringByFuzzyKMeans;
import linkservice.clustering.methods.jsonify.ConvertResultObjectToJson;
import linkservice.searching.Searcher;
import linkservice.searching.result.SearchResultObject;
import linkservice.searching.result.SearchResultObjectByCluster;

import org.automatic.restful.pojo.Query;

@Path("searchresource")
public class SearchResource {
	
	@GET
	//@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String getIt() throws Exception {
//		String searchQuery = "image";
//		ClusteringByFuzzyKMeans clusteringByFuzzyKMeans = new ClusteringByFuzzyKMeans();
//		String indexDir = clusteringByFuzzyKMeans.getIndexFileDir();
		Searcher searcher = new Searcher("sdfsf");
//		Set<SearchResultObject> results = searcher.search(searchQuery);
//		Set<SearchResultObjectByCluster> resultsByClusters = new HashSet<SearchResultObjectByCluster>();
//		if (results.size() > 0) {
//			clusteringByFuzzyKMeans.run(results);
//			resultsByClusters = ConvertResultObjectToJson.run(results);
//		}
		return "Got it!";
		//return Response.status(201).entity(resultsByClusters).build();
	}
}