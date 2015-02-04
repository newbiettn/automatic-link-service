package linkservice.index;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.assertEquals;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import linkservice.index.Indexer;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class IndexerTest {
	static Logger log = Logger.getLogger(IndexerTest.class.getName());
	
	public static Indexer indexer;
	public static String indexDir;
	public static String dataDir;
	
	@BeforeClass
	public static void setUp() throws Exception {
		indexDir = "src/test/resources/index";
		dataDir = "src/test/resources/samples/data/test/alt.atheism";
		
		//empty directory
		FileUtils.cleanDirectory(new File(indexDir));
		
		indexer = new Indexer(indexDir);
		indexer.setDataDir(dataDir);
		indexer.index();
		indexer.close();
	}
	
	//verify writer document count
	@Test
	public void test1IndexWriter() throws Exception {
		log.info("Inside testIndexWriter()");
		
		Collection<File> files = FileUtils.listFiles(new File(dataDir), null, true);
		IndexWriter writer = indexer.getWriter();
		log.info("Number of files: " + files.size());
		assertEquals("file size " + files.size() + " differs with writer size " + writer.numDocs() , files.size(), writer.numDocs());
		writer.close();
	}
	
	//verify reader document count
	@Test
	public void test2IndexReader() throws IOException {
		log.info("Inside testIndexReader()");
		
		Collection<File> files = FileUtils.listFiles(new File(dataDir), null, true);
		Directory directory = FSDirectory.open(new File(indexDir));
		IndexReader reader = DirectoryReader.open(directory);
		
		assertEquals(files.size() + " is different with " + reader.maxDoc(), files.size(), reader.maxDoc());
		assertEquals("reader document count is not the same", files.size(), reader.numDocs());
		
		reader.close();
	}
}
