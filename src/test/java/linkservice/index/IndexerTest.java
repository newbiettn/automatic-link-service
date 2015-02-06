package linkservice.index;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import static org.junit.Assert.assertEquals;

import org.apache.commons.io.FileUtils;
import org.apache.hadoop.fs.Path;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import linkservice.common.AbstractLinkServiceTest;
import linkservice.index.Indexer;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class IndexerTest extends AbstractLinkServiceTest {
	Logger logger = LoggerFactory.getLogger(IndexerTest.class);

	private static Indexer indexer;

	@BeforeClass
	public static void setUp() throws Exception {
		// empty directory
		FileUtils.cleanDirectory(new File(IndexerTest.INDEX_DIR));

		// index and store in both local and hdfs
		indexer = getIndexer();
		indexer.runIndex();
		indexer.close();
		indexer.storeIndexToHDFS();
	}

	// verify writer document count
	@Test
	public void test1IndexWriter() throws Exception {
		logger.info("Inside testIndexWriter()");

		Collection<File> files = FileUtils.listFiles(new File(
				IndexerTest.DATA_DIR), null, true);
		IndexWriter writer = indexer.getWriter();
		logger.info("Number of files: " + files.size());
		assertEquals("file size " + files.size() + " differs with writer size "
				+ writer.numDocs(), files.size(), writer.numDocs());
		writer.close();
	}

	// verify reader document count
	@Test
	public void test2IndexReader() throws IOException {
		logger.info("Inside testIndexReader()");

		Collection<File> files = FileUtils.listFiles(new File(
				IndexerTest.DATA_DIR), null, true);
		Directory directory = FSDirectory.open(new File(IndexerTest.INDEX_DIR));
		IndexReader reader = DirectoryReader.open(directory);

		assertEquals(files.size() + " is different with " + reader.maxDoc(),
				files.size(), reader.maxDoc());
		assertEquals("reader document count is not the same", files.size(),
				reader.numDocs());

		reader.close();
	}

	@Test
	public void test3IndexFilesInHdfs() {
		logger.info("Indside test3IndexFilesInHdfs()");

		Collection<File> files = FileUtils.listFiles(new File(
				IndexerTest.INDEX_DIR), null, true);
		Path[] indexPaths = indexer.getIndexedPath();

		logger.info("HDFS has " + indexPaths.length + " paths");
		assertEquals(files.size(), indexPaths.length);
	}

//	@Test
//	public void test4Term() throws Exception {
//		Directory directory = FSDirectory.open(new File(IndexerTest.INDEX_DIR));
//		IndexReader reader = DirectoryReader.open(directory);
//		IndexSearcher searcher = new IndexSearcher(reader);
//		Term t = new Term("contents", "programming");
//		Query query = new TermQuery(t);
//		TopDocs docs = searcher.search(query, 1000);
//		assertEquals(1, docs.totalHits);
//		reader.close();
//	}
//	
	@Test
	public void test5GetAllTerm() throws IOException {
		logger.info("--------------------------------------------------");
		Directory directory = FSDirectory.open(new File(IndexerTest.INDEX_DIR));
		IndexReader reader = DirectoryReader.open(directory);
		Fields fields = MultiFields.getFields(reader);
		for (String field : fields) {
            Terms terms = fields.terms(field);
            TermsEnum termsEnum = terms.iterator(null);
            int count = 0;
            BytesRef text;
            
            while ((text = termsEnum.next()) != null) {
            	
            	logger.info(text.utf8ToString() + " has " + termsEnum.docFreq());
                count++;
                
            }
            //logger.info("The count is " + count);
        }
		logger.info("--------------------------------------------------");
	}
	
	
	// @Test
	// public void test3IndexReaderHdfs() throws IOException {
	// logger.info("Inside test3IndexReaderHdfs()");
	//
	// Collection<File> files = FileUtils.listFiles(new File(dataDir), null,
	// true);
	// Directory directory = FSDirectory.open(new File(indexDir));
	// HdfsDirectory hdfsDir = new HdfsDirectory(new Path("automatic"),
	// HadoopConfig.getInstance().getConf());
	//
	// }
}
