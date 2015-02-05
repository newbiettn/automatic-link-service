package linkservice.index;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;

import linkservice.hadoop.HadoopConfig;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Indexer {
	// log4j
	Logger logger = LoggerFactory.getLogger(Indexer.class);

	// array of index paths
	Path[] indexedPath;

	// path to index files
	private String indexDir;

	// path to files folder
	private String dataDir;

	// IndexWriter to create and maintain index
	private IndexWriter writer;

	private Analyzer analyzer;

	// Hadoop configuration
	private HadoopConfig hadoopConf;

	public void init(String indexDir, String dataDir) throws IOException {
		// get config singleton
		hadoopConf = HadoopConfig.getInstance();
		
		// set place to store indexes
		this.indexDir = indexDir;
		
		//set directory has to be indexed
		this.dataDir = dataDir;

		// create Directory to store indexes, use FSDirectory.open
		// to automatically pick the most suitable directory implementation
		Directory dir = FSDirectory.open(new File(indexDir));

		// create StandardAnalyzer to tokenize which uses default stop words
		analyzer = new StandardAnalyzer(Version.LUCENE_46);

		// create configuration for new IndexWriter
		IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_46,
				analyzer);

		// create IndexWriter, which can create new index, or
		// adds, removes, or updates documents in the index
		// however, it can not read or search
		this.writer = new IndexWriter(dir, conf);
	}

	public static Indexer getInstance() {
		return IndexerHolder.INSTANCE;
	}
	
	private static class IndexerHolder {
		private static final Indexer INSTANCE = new Indexer();
	}
	
	// index files in directory
	// start with *.txt files, will change later after having crawler
	public int runIndex() throws Exception {
		long start = System.currentTimeMillis();
		Collection<File> files = FileUtils.listFiles(new File(this.dataDir), null, true);

		for (File f : files) {
			indexFile(f);
		}

		//logging
		long end = System.currentTimeMillis();
		logger.info("Indexing " + this.writer.numDocs() + " files took "
				+ (end - start) + " milliseconds");
		
		return this.writer.numDocs();
	}

	// copy indexes to hdfs for further text mining by mahout
	public void storeIndexToHDFS() throws IOException {
		Collection<File> files = FileUtils.listFiles(new File(indexDir), null, true);
		FileSystem hdfsFileSystem = FileSystem.get(hadoopConf.getConf());
		
		indexedPath = new Path[files.size()];
		for (File f : files) {
			//put indexes file path to hdfs
			ArrayUtils.add(indexedPath, new Path(f.getName()));

			Path localPath = new Path(f.getPath());
			Path hdfsPath = new Path("automatic/" + f.getName());
			
			//copy file to hdfs, overwrite if duplicated
			hdfsFileSystem.copyFromLocalFile(false, true, localPath, hdfsPath);
		}
	}

	// add Document to the index
	public void indexFile(File f) throws Exception {
		// log.info("Indexing " + f.getCanonicalPath());
		Document doc = getDocument(f);
		writer.addDocument(doc);
	}

	/* Indexed, tokenized, stored. */
	public static final FieldType TYPE_STORED = new FieldType();

	static {
		TYPE_STORED.setIndexed(true);
		TYPE_STORED.setTokenized(true);
		TYPE_STORED.setStored(true);
		TYPE_STORED.setStoreTermVectors(true);
		TYPE_STORED.setStoreTermVectorPositions(true);
		TYPE_STORED.freeze();
	}

	// create Document and Fields
	private Document getDocument(File f) throws Exception {
		Document doc = new Document();

		Field contentField = new Field("content", "", TYPE_STORED);
		contentField.setTokenStream(analyzer.tokenStream("content",
				new FileReader(f)));
		doc.add(contentField);
		doc.add(new StringField("filepath", f.getCanonicalPath(),
				Field.Store.YES));
		// doc.add(new StringField("fullpath", f.getCanonicalPath(),
		// Field.Store.YES));

		return doc;
	}

	// close the IndexWriter, which means committing the indexes
	public void close() throws IOException {
		this.writer.close();
	}
	
	public IndexWriter getWriter() throws IOException {
		return new IndexWriter(FSDirectory.open(new File(indexDir)),
				new IndexWriterConfig(Version.LUCENE_46, new StandardAnalyzer(
						Version.LUCENE_46)));
	}

	public void setWriter(IndexWriter writer) {
		this.writer = writer;
	}
}
