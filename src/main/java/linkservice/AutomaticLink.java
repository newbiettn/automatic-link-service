package linkservice;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import linkservice.clustering.jsonify.OrganizeSearchResultObjectByClusters;
import linkservice.clustering.methods.ClusteringByFuzzyKMeans;
import linkservice.searching.Searcher;
import linkservice.searching.result.SearchResultObject;
import linkservice.searching.result.SearchResultObjectByCluster;

import com.owlike.genson.Genson;

public class AutomaticLink {
	
	static Logger logger = LoggerFactory.getLogger(AutomaticLink.class);
	
	public String run(String query) throws Exception {
		String path = getClass().getCanonicalName();
		Searcher searcher = new Searcher("/Users/newbiettn/Dropbox/Git/automatic-link-serivce/output/index_files");
		ClusteringByFuzzyKMeans clusteringByFuzzyKMeans = new ClusteringByFuzzyKMeans();

		List<SearchResultObject> results = searcher.search(query);
		List<SearchResultObjectByCluster> resultsByClusters = new ArrayList<SearchResultObjectByCluster>();
		
		String json = "";
		if (results.size() > 0) {
			clusteringByFuzzyKMeans.run(results);
			resultsByClusters = OrganizeSearchResultObjectByClusters
					.run(results);
			Genson genson = new Genson();
			logger.info("Search result size: " + results.size());
			logger.info("Search result by clusters: "
					+ resultsByClusters.size());
			json = genson.serialize(resultsByClusters);
			logger.info(json + "");
		}
		return json;
	}
}
