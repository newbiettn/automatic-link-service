package linkservice.searching.result;

import java.util.List;

public class SearchResultObjectByCluster {
	private String cluster_label;
	private List<SearchResultObject> results;
	
	public String getCluster_label() {
		return cluster_label;
	}
	public List<SearchResultObject> getResults() {
		return results;
	}
	public void setCluster_label(String cluster_label) {
		this.cluster_label = cluster_label;
	}
	public void setResults(List<SearchResultObject> results) {
		this.results = results;
	}
	
}
