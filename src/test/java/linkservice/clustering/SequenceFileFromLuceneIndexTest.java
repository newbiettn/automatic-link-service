package linkservice.clustering;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Collection;

import linkservice.common.AbstractLinkServiceTest;
import linkservice.index.Indexer;

import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SequenceFileFromLuceneIndexTest extends AbstractLinkServiceTest{
	static Logger logger = LoggerFactory.getLogger(SequenceFileFromLuceneIndexTest.class);
	
	public static Indexer indexer;

	private static SequenceFileFromLuceneIndex lucene2Seq;
	
	@BeforeClass
	public static void setUp() throws Exception {
		indexer = getIndexer();
		lucene2Seq = new SequenceFileFromLuceneIndex(indexer);
	}
	
	@Test
	public void testRunDirectories() throws Exception {
		logger.info("Inside testRunDirectories()");
		lucene2Seq.run();
		Collection<File> files = FileUtils.listFiles(new File(DATA_DIR), null, true);
		assertEquals(files.size(), lucene2Seq.getSequenceSize());
	}
}
