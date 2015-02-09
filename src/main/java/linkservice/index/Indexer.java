package linkservice.index;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import linkservice.common.hadoop.HadoopConfig;

import org.apache.commons.io.FileUtils;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.LongField;
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
	private Path[] indexedPath;

	// path to index files
	private String indexDir;

	// path to files folder
	private String dataDir;

	// IndexWriter to create and maintain index
	private IndexWriter writer;

	private Analyzer analyzer;

	// Hadoop configuration
	private HadoopConfig hadoopConf;

	public Indexer(String anIndexDir, String aDataDir) {
		// get config singleton
		hadoopConf = new HadoopConfig();

		// set place to store indexes
		this.indexDir = anIndexDir;

		// set directory has to be indexed
		this.dataDir = aDataDir;

		try {
			// create Directory to store indexes, use FSDirectory.open
			// to automatically pick the most suitable directory implementation
			Directory dir = FSDirectory.open(new File(indexDir));

			// create StandardAnalyzer to tokenize which uses default stop words
			analyzer = new MyCustomAnalyzer(new StandardAnalyzer(Version.LUCENE_46));

			// create configuration for new IndexWriter
			IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_46, analyzer);

			// create IndexWriter, which can create new index, or
			// adds, removes, or updates documents in the index
			// however, it can not read or search
			this.writer = new IndexWriter(dir, conf);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// index files in directory
	// start with *.txt files, will change later after having crawler
	public int runIndex() throws Exception {
		long start = System.currentTimeMillis();
		Collection<File> files = FileUtils.listFiles(new File(this.dataDir),
				null, true);
		
		//documentID is to provide unique ID to each document, start with 1
		int docId = 1; 
		for (File f : files) {
			indexFile(f, docId);
			docId +=1;
		}

		// logging
		long end = System.currentTimeMillis();
		logger.info("Indexing " + this.writer.numDocs() + " files took "
				+ (end - start) + " milliseconds");

		return this.writer.numDocs();
	}

	// add Document to the index
	public void indexFile(File f, int docId) throws Exception {
		// logger.info("Indexing " + f.getCanonicalPath());
		Document doc = getDocument(f, docId);
		writer.addDocument(doc);
	}

	// Indexed, tokenized, stored, and create term vector
	public static final FieldType TYPE_STORED = new FieldType();

	static {
		TYPE_STORED.setIndexed(true);
		TYPE_STORED.setTokenized(true);
		TYPE_STORED.setStored(true);
		TYPE_STORED.setStoreTermVectors(true);
		TYPE_STORED.setStoreTermVectorOffsets(false);
		TYPE_STORED.setStoreTermVectorPositions(false);
		// TYPE_STORED.freeze();
	}

	// create Document and Fields
	private Document getDocument(File f, int docId) throws Exception {
		Document doc = new Document();
		
		//docId
		doc.add(new StringField("id", Integer.toString(docId), Field.Store.YES));
		
		//content field
//		Field contentField = new Field("contents", new FileReader(f), TYPE_STORED);
//		doc.add(contentField);
		Field contentField = new Field("contents", FileUtils.readFileToString(f), TYPE_STORED);		
		//contentField.setTokenStream(analyzer.tokenStream("content",	new FileReader(f)));
		doc.add(contentField);
		
		//filename + path + modified 
		doc.add(new StringField("filename", f.getName(), Field.Store.YES));
		doc.add(new StringField("path", f.getPath(), Field.Store.YES));
		doc.add(new LongField("modified", f.lastModified(), Field.Store.YES));
		
		return doc;
	}

	// copy indexes to hdfs for further text mining by mahout
	public void storeIndexToHDFS() throws IOException {
		Collection<File> files = FileUtils.listFiles(new File(indexDir), null,
				true);
		FileSystem hdfsFileSystem = FileSystem.get(hadoopConf.getConf());

		indexedPath = new Path[files.size()];

		int count = 0;
		for (File f : files) {
			// put indexes file path to hdfs
			logger.info("Add " + f.getName() + " to indexedPath");
			indexedPath[count] = new Path(f.getName());

			Path localPath = new Path(f.getPath());
			Path hdfsPath = new Path("automatic/" + f.getName());

			// copy file to hdfs, overwrite if duplicated
			hdfsFileSystem.copyFromLocalFile(false, true, localPath, hdfsPath);
		}
	}

	public List<Path> getIndexFileFromHdfs() throws IOException {
		List<Path> indexPaths = new ArrayList<Path>();
		FileSystem fs = FileSystem.get(hadoopConf.getConf());
		FileStatus[] status = fs.listStatus(new Path(
				HadoopConfig.AUTOMATIC_INDEX_HDFS_DIR));
		for (int i = 0; i < status.length; i++) {
			indexPaths.add(status[i].getPath());
		}
		return indexPaths;
	}

	// close the IndexWriter, which means committing the indexes
	public void close() throws IOException {
		this.writer.close();
	}

	// get IndexWriter
	public IndexWriter getWriter() throws IOException {
		return new IndexWriter(FSDirectory.open(new File(indexDir)),
				new IndexWriterConfig(Version.LUCENE_46, new StandardAnalyzer(
						Version.LUCENE_46)));
	}

	public void setWriter(IndexWriter writer) {
		this.writer = writer;
	}

	public Path[] getIndexedPath() {
		return indexedPath;
	}
	
	public String getIndexDir() {
		return this.indexDir;
	}
}
