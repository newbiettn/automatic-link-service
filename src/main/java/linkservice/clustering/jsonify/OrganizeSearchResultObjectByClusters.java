package linkservice.clustering.jsonify;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import linkservice.document.MyDocument;
import linkservice.searching.result.SearchResultObject;
import linkservice.searching.result.SearchResultObjectByCluster;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.mahout.clustering.classify.WeightedPropertyVectorWritable;
import org.apache.mahout.clustering.classify.WeightedVectorWritable;
import org.apache.mahout.clustering.iterator.ClusterWritable;
import org.apache.mahout.common.Pair;
import org.apache.mahout.common.iterator.sequencefile.PathType;
import org.apache.mahout.common.iterator.sequencefile.SequenceFileDirValueIterable;
import org.apache.mahout.math.NamedVector;
import org.apache.mahout.math.Vector;
import org.apache.mahout.utils.clustering.ClusterDumper;
import org.apache.mahout.utils.vectors.VectorHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class OrganizeSearchResultObjectByClusters {
	static Logger logger = LoggerFactory
			.getLogger(OrganizeSearchResultObjectByClusters.class);
	
	static Configuration conf = new Configuration();
	
	public static List<SearchResultObjectByCluster> run(List<SearchResultObject> searchReturnedBySearcher) {
		List<SearchResultObjectByCluster> setOfSearchResultObjectByCluster = new ArrayList<SearchResultObjectByCluster>();
		
		Iterable<ClusterWritable> iterable = new SequenceFileDirValueIterable<ClusterWritable>(
				new Path("output/clustering/final_clusters/clusters-1-final", "part-*"),
				PathType.GLOB, conf);
		Iterator<ClusterWritable> iterator = iterable.iterator();
		// iterator of clusters
		System.out.println("clusterWritable");
		while (iterator.hasNext()) {
			
			// handle each clusters
			ClusterWritable clusterWritable = iterator.next();
			SearchResultObjectByCluster searchResultObjectByCluster = write(clusterWritable, searchReturnedBySearcher);
			//logger.info(searchResultObjectByCluster.getResults().size()+"");
			setOfSearchResultObjectByCluster.add(searchResultObjectByCluster);
		}
		return setOfSearchResultObjectByCluster;
	}

	public static SearchResultObjectByCluster write(ClusterWritable clusterWritable, List<SearchResultObject> searchReturnedBySearcher) {
		SearchResultObjectByCluster searchResultObjectByCluster = new SearchResultObjectByCluster();
		String[] dictionary = VectorHelper.loadTermDictionary(conf,
				"output/sparse_vectors/dictionary.file-0");
		int numTopFeatures = 1;

		for (Pair<String, Double> item : getTopPairs(clusterWritable.getValue()
				.getCenter(), dictionary, numTopFeatures)) {
			String term = item.getFirst();
			//set label for cluster by getting the best term
			searchResultObjectByCluster.setCluster_label(term);
		}

		// get top terms for the clusters
		Map<Integer, List<WeightedPropertyVectorWritable>> clusterIdToPoints = ClusterDumper
				.readPoints(new Path("output/clustering/final_clusters/clusteredPoints"),
						Long.MAX_VALUE, conf);

		// get list of points for the cluster
		List<WeightedPropertyVectorWritable> points = clusterIdToPoints
				.get(clusterWritable.getValue().getId());

		// loop the list
		if (points != null) {
			Iterator<WeightedPropertyVectorWritable> iterator = points.iterator();
			List<SearchResultObject> s = new ArrayList<SearchResultObject>();
			while (iterator.hasNext()) {
				WeightedVectorWritable point = iterator.next();
				NamedVector nv = (NamedVector) point.getVector();
				String docId = nv.getName();
				
				
				for (SearchResultObject searchResultObject : searchReturnedBySearcher) {
					MyDocument doc = searchResultObject.getMyDoc();
					
					if (doc.getId().equals(docId)) {
						s.add(searchResultObject);
					}
				}
			}
			
			searchResultObjectByCluster.setResults(s);
		}
		return searchResultObjectByCluster;
	}
	
	private static Collection<Pair<String, Double>> getTopPairs(Vector vector,
			String[] dictionary, int numTerms) {
		List<TermIndexWeight> vectorTerms = Lists.newArrayList();

		for (Vector.Element elt : vector.nonZeroes()) {
			vectorTerms.add(new TermIndexWeight(elt.index(), elt.get()));
		}

		// Sort results in reverse order (ie weight in descending order)
		Collections.sort(vectorTerms, new Comparator<TermIndexWeight>() {
			@Override
			public int compare(TermIndexWeight one, TermIndexWeight two) {
				return Double.compare(two.weight, one.weight);
			}
		});

		Collection<Pair<String, Double>> topTerms = Lists.newLinkedList();

		for (int i = 0; i < vectorTerms.size() && i < numTerms; i++) {
			int index = vectorTerms.get(i).index;
			String dictTerm = dictionary[index];
			if (dictTerm == null) {
				logger.error("Dictionary entry missing for {}", index);
				continue;
			}
			topTerms.add(new Pair<String, Double>(dictTerm,
					vectorTerms.get(i).weight));
		}

		return topTerms;
	}

	private static class TermIndexWeight {
		private final int index;
		private final double weight;

		TermIndexWeight(int index, double weight) {
			this.index = index;
			this.weight = weight;
		}
	}
}
