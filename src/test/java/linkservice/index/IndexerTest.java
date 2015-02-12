package linkservice.index;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;

import static org.junit.Assert.assertEquals;

import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.mahout.common.HadoopUtil;

import linkservice.common.CommonRule;
import linkservice.common.TestLogger;
import linkservice.index.Indexer;

/**
 * Test unit for {@link linkservice.index.Indexer}
 *
 * @author newbiettn
 * @version 1.0
 * 
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class IndexerTest {

	@ClassRule
	public static TestLogger testLogger = new TestLogger();

	@ClassRule
	public static CommonRule commonRule = new CommonRule();

	private static Logger logger;

	private static Indexer indexer;

	private String index_dir = commonRule.getIndexDir();

	private String data_dir = commonRule.getDataDir();

	@BeforeClass
	public static void setUp() throws Exception {
		logger = testLogger.getLogger();
		indexer = commonRule.getIndexer();

		// empty directory first before test to avoid duplicated
		Configuration conf = new Configuration();
		HadoopUtil.delete(conf, new Path(commonRule.getIndexDir()));

		// index and store in both local and hdfs
		indexer.runIndex();
		indexer.close();
	}

	/**
	 * The unit test to verify if all documents are indexed
	 * 
	 */
	@Test
	public void test1IndexWriter() throws Exception {
		logger.info("Inside testIndexWriter()");

		Collection<File> files = FileUtils.listFiles(new File(data_dir), null,
				true);
		IndexWriter writer = indexer.getWriter();
		logger.info("Number of files: " + files.size());
		assertEquals("file size " + files.size() + " differs with writer size "
				+ writer.numDocs(), files.size(), writer.numDocs());
		writer.close();
	}

	/**
	 * The unit test to verify if we can read all indexes from index files
	 * 
	 */
	@Test
	public void test2IndexReader() throws IOException {
		logger.info("Inside testIndexReader()");

		Collection<File> files = FileUtils.listFiles(new File(data_dir), null,
				true);
		Directory directory = FSDirectory.open(new File(index_dir));
		IndexReader reader = DirectoryReader.open(directory);

		assertEquals(files.size() + " is different with " + reader.maxDoc(),
				files.size(), reader.maxDoc());
		assertEquals("reader document count is not the same", files.size(),
				reader.numDocs());

		reader.close();
	}
	
	/**
	 * Observe all terms and its corresponding frequencies after indexing 
	 * 
	 */
	@Test
	public void test3GetAllTerm() throws IOException {
		logger.info("---------------test5GetAllTerm() begins-------------");
		Directory directory = FSDirectory.open(new File(index_dir));
		IndexReader reader = DirectoryReader.open(directory);
		Fields fields = MultiFields.getFields(reader);
		for (String field : fields) {
			Terms terms = fields.terms(field);
			TermsEnum termsEnum = terms.iterator(null);
			BytesRef text;
			while ((text = termsEnum.next()) != null) {
				logger.info("[" + text.utf8ToString() + "] occurs: "
						+ termsEnum.docFreq() + " times in the doc collection");
			}
		}
		logger.info("---------------test5GetAllTerm() ends-------------");
	}

	// @Test
	// public void test4Term() throws Exception {
	// Directory directory = FSDirectory.open(new File(IndexerTest.INDEX_DIR));
	// IndexReader reader = DirectoryReader.open(directory);
	// IndexSearcher searcher = new IndexSearcher(reader);
	// Term t = new Term("contents", "programming");
	// Query query = new TermQuery(t);
	// TopDocs docs = searcher.search(query, 1000);
	// assertEquals(1, docs.totalHits);
	// reader.close();
	// }
	//
}
