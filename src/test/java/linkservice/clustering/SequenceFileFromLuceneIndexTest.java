package linkservice.clustering;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Collection;

import linkservice.common.CommonRule;
import linkservice.common.TestLogger;
import linkservice.index.Indexer;

import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.mahout.common.HadoopUtil;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SequenceFileFromLuceneIndexTest {
	@ClassRule
	public static TestLogger testLogger = new TestLogger();

	@ClassRule
	public static CommonRule commonRule = new CommonRule();

	private static Logger logger;

	private static Indexer indexer;

	private static SequenceFileFromLuceneIndex lucene2Seq;

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
		
		lucene2Seq = new SequenceFileFromLuceneIndex(indexer,
				commonRule.getSequence_file_dir());
	}

	@Test
	public void testRunDirectories() throws Exception {
		logger.info("Inside testRunDirectories()");
		lucene2Seq.run();
		Collection<File> files = FileUtils.listFiles(
				new File(commonRule.getDataDir()), null, true);
		assertEquals(files.size(), lucene2Seq.getSequenceSize());
	}
}
