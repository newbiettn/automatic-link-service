package linkservice.searching;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import linkservice.common.CommonRule;
import linkservice.common.LoggerRule;
import linkservice.document.MyDocument;
import linkservice.indexing.Indexer;
import linkservice.searching.result.SearchResultObject;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.mahout.common.HadoopUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;

/**
 * Test unit for {@link linkservice.searching.Searcher}
 * 
 * @author newbiettn
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SearcherTest {
	@ClassRule
	public static LoggerRule testLogger = new LoggerRule();

	@ClassRule
	public static CommonRule commonRule = new CommonRule();

	private static Logger logger;

	private static Indexer indexer;

	private String index_dir = commonRule.getIndexDir();
	
	private Configuration conf;
	private Searcher searcher;

	@Before
	public void setUp() throws Exception {
		logger = testLogger.getLogger();
		indexer = commonRule.getIndexer();

		conf = new Configuration();
		
		// index and store in both local and hdfs
		indexer.runIndex();
		indexer.close();
		
		searcher = new Searcher(index_dir);
	}

	@Test
	public void test1ForTerm() throws Exception {
		List<SearchResultObject> docs = searcher.search("image");
		for (SearchResultObject searchResultObj : docs) {
			MyDocument myDoc = searchResultObj.getMyDoc();
			logger.info(myDoc.getFragment()+"");
		}
		assertNotNull(docs.size());
	}

	
	@After 
	public void tearDown() throws IOException {
		HadoopUtil.delete(conf, new Path(commonRule.getIndexDir()));
	}
}
