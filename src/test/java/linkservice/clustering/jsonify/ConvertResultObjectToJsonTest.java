package linkservice.clustering.jsonify;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import linkservice.clustering.ClusteringByFuzzyKMeans;
import linkservice.common.CommonRule;
import linkservice.common.GeneralConfigPath;
import linkservice.common.LinkServiceGetPropertyValues;
import linkservice.common.LoggerRule;
import linkservice.indexing.Indexer;
import linkservice.searching.Searcher;
import linkservice.searching.result.SearchResultObject;
import linkservice.searching.result.SearchResultObjectByCluster;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.mahout.common.HadoopUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.slf4j.Logger;

import com.owlike.genson.Genson;

public class ConvertResultObjectToJsonTest {
	@ClassRule
	public static LoggerRule testLogger = new LoggerRule();

	@ClassRule
	public static CommonRule commonRule = new CommonRule();

	private static Logger logger;

	private static Indexer indexer;

	private String index_dir = commonRule.getIndexDir();
	
	private Configuration conf;
	private Searcher searcher;
	
	LinkServiceGetPropertyValues myDocumentIndexedProp;
	
	@Before
	public void setUp() throws Exception {
		logger = testLogger.getLogger();
		indexer = commonRule.getIndexer();
		
		myDocumentIndexedProp = new LinkServiceGetPropertyValues(
				GeneralConfigPath.PROPERTIES_PATH);

		conf = new Configuration();
		
		// index and store in both local and hdfs
		indexer.runIndex();
		indexer.close();
		
		searcher = new Searcher(index_dir);
		
		HadoopUtil.delete(
				conf,
				new Path(myDocumentIndexedProp
						.getProperty("linkservice.mahout.clustering_root")));
	}
	@Test
	public void test() throws Exception {
		String searchQuery = "image";
		
		ClusteringByFuzzyKMeans clusteringByFuzzyKMeans = new ClusteringByFuzzyKMeans();

		List<SearchResultObject> results = searcher.search(searchQuery);
		List<SearchResultObjectByCluster> resultsByClusters = new ArrayList<SearchResultObjectByCluster>();
		if (results.size() > 0) {
			clusteringByFuzzyKMeans.run(results);
			resultsByClusters = OrganizeSearchResultObjectByClusters.run(results);
			Genson genson = new Genson();
			System.out.println("Search result size: " + results.size());
			System.out.println("Search result by clusters: " + resultsByClusters.size());
			String json = genson.serialize(resultsByClusters);
			logger.info(json+"");
		}
	}
}
