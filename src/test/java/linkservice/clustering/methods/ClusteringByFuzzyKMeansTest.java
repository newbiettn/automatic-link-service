package linkservice.clustering.methods;

import java.io.IOException;
import java.util.List;

import linkservice.clustering.jsonify.OrganizeSearchResultObjectByClusters;
import linkservice.clustering.methods.ClusteringByFuzzyKMeans;
import linkservice.common.ClusterOutput;
import linkservice.common.CommonRule;
import linkservice.common.GeneralConfigPath;
import linkservice.common.LinkServiceGetPropertyValues;
import linkservice.common.LoggerRule;
import linkservice.common.SequenceFileFromLuceneIndex;
import linkservice.common.UpdatePath;
import linkservice.indexing.Indexer;
import linkservice.searching.Searcher;
import linkservice.searching.result.SearchResultObject;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.mahout.common.HadoopUtil;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.slf4j.Logger;

public class ClusteringByFuzzyKMeansTest {
	@ClassRule
	public static LoggerRule testLogger = new LoggerRule();

	@ClassRule
	public static CommonRule commonRule = new CommonRule();

	private static Logger logger;

	private static Indexer indexer;

	private String index_dir = commonRule.getIndexDir();

	Configuration conf;
	LinkServiceGetPropertyValues myDocumentIndexedProp;

	@Before
	public void setUp() throws IOException {
		conf = new Configuration();
		// get properties helper
		myDocumentIndexedProp = new LinkServiceGetPropertyValues(
				GeneralConfigPath.PROPERTIES_PATH);
	}

	@Test
	public void test1() throws Exception {

		ClusteringByFuzzyKMeans clusteringByFuzzyKMeans = new ClusteringByFuzzyKMeans();

		// uri to files
		String indexDir = clusteringByFuzzyKMeans.getIndexFileDir();
		String dataFileDir = clusteringByFuzzyKMeans.getDataFileDir();
		String sequenceFileDir = clusteringByFuzzyKMeans.getSequenceFileDir();
		String finalClusterOutputDir = clusteringByFuzzyKMeans
				.getFinalClusterOutputDir();
		String outputRootDir = clusteringByFuzzyKMeans.getOutputRootDir();
		String sparseVectorsDir = clusteringByFuzzyKMeans.getSparseVectorsDir();

		// create new indexer
		indexer = commonRule.getIndexer();
		FileSystem fs = FileSystem.get(conf);

		if (indexer.isIndexChanged()) {
			UpdatePath.cleanIfHaveNewIndex();
		}
		
		SequenceFileFromLuceneIndex lucene2Seq = new SequenceFileFromLuceneIndex(
				indexer, sequenceFileDir);

		// create index files
		indexer.runIndex();
		indexer.close();

		// convert index files to sequence files
		lucene2Seq.run();
		
		clusteringByFuzzyKMeans.generateSparseVectors(sequenceFileDir, sparseVectorsDir);
		
		// make a dumb search for testing purpose
		List<SearchResultObject> sampleResult = makeDumbSearch();
		for (SearchResultObject ele : sampleResult) {
			System.out.println(ele.getMyDoc().getUri());
		}
		if (sampleResult.size() > 0) {
			// run clustering using sequence files
			UpdatePath.cleanForNewClustering();
			clusteringByFuzzyKMeans.run(sampleResult);
			OrganizeSearchResultObjectByClusters.run(sampleResult);
		}
	}

	public List<SearchResultObject> makeDumbSearch() throws IOException,
			InvalidTokenOffsetsException, ParseException {
		Searcher searcher = new Searcher(index_dir);
		List<SearchResultObject> results = searcher.search("image");
		return results;
	}
}
