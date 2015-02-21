package linkservice.searching;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import linkservice.common.CommonRule;
import linkservice.common.LoggerRule;
import linkservice.index.IndexerTest;
import linkservice.indexing.Indexer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.mahout.common.HadoopUtil;
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

	private Searcher searcher;

	@Before
	public void setUp() throws Exception {
		logger = testLogger.getLogger();
		indexer = commonRule.getIndexer();

		// empty directory first before test to avoid duplicated
		Configuration conf = new Configuration();
		HadoopUtil.delete(conf, new Path(commonRule.getIndexDir()));

		// index and store in both local and hdfs
		indexer.runIndex();
		indexer.close();

		try {
			searcher = new Searcher(index_dir);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

//	@Test
//	public void test4Term() throws Exception {
//		Directory directory = FSDirectory.open(new File(index_dir));
//		IndexReader reader = DirectoryReader.open(directory);
//		IndexSearcher searcher = new IndexSearcher(reader);
//		Term t = new Term("contents", "image");
//		Query query = new TermQuery(t);
//		TopDocs docs = searcher.search(query, 1000);
//		assertEquals(1, docs.totalHits);
//		reader.close();
//	}

	@Test
	public void test1SimpleSearch() throws IOException,
			InvalidTokenOffsetsException {
		searcher.search();
	}
}
