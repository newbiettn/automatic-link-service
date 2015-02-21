package linkservice.indexing;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import linkservice.common.AnalyzerUtils;
import linkservice.document.MyDocumentIndexedProperties;
import linkservice.hadoop.HadoopConfig;

import org.apache.commons.io.FileUtils;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
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
import org.apache.lucene.index.FieldInfo.IndexOptions;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.Property;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;

/**
 * Index all documents 
 *
 * @author newbiettn
 * @version 1.0
 * 
 */
public class Indexer {
	// log4j
	Logger logger = LoggerFactory.getLogger(Indexer.class);
	
	//
	static Set<Property> textualMetadataFields = new HashSet<Property>();
	
	static {
		textualMetadataFields.add(TikaCoreProperties.TITLE);
	    textualMetadataFields.add(TikaCoreProperties.KEYWORDS);
	    textualMetadataFields.add(TikaCoreProperties.DESCRIPTION);
	}
	// Indexed, tokenized, stored, and create term vector
	public static final FieldType TYPE_STORED = new FieldType();
	
	static {
		TYPE_STORED.setIndexed(true);
		TYPE_STORED.setTokenized(true);
		TYPE_STORED.setStored(true);
		TYPE_STORED.setStoreTermVectors(true);
		TYPE_STORED.setStoreTermVectorOffsets(true);
		TYPE_STORED.setStoreTermVectorPositions(true);
		TYPE_STORED.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
		TYPE_STORED.freeze();
	}

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
	
	/** 
	 * Create an Indexer using the given directory to store index files, 
	 * and the directory need to be indexed.
	 *  
	 * */
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
			//analyzer = new MyCustomAnalyzer(new StandardAnalyzer(Version.LUCENE_46, MyStopWords.MY_ENGLISH_STOP_WORDS_SET));
			//analyzer = new StandardAnalyzer(Version.LUCENE_46, MyStopWords.MY_ENGLISH_STOP_WORDS_SET);
			analyzer = new MyCustomAnalyzer();
			
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

	/** 
	 * Index files in directory by looping through documents in directories 
	 * and sub-directories 
	 * 
	 * @return Number of documents is indexed
	 * @throws Exception
	 */
	// 
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
	
	/**
	 * Add Document to the index
	 * 
	 * @param f
	 * @param docId
	 * @throws Exception
	 */
	// 
	public void indexFile(File f, int docId) throws Exception {
		// logger.info("Indexing " + f.getCanonicalPath());
		Document doc = getDocument(f, docId);
		writer.addDocument(doc);
	}

	/**
	 * Parse document and index contents including metadata, body content
	 * 
	 * @param f
	 * @param docId
	 * @return
	 * @throws Exception
	 */
	private Document getDocument(File f, int docId) throws Exception {
		//create metadata
		Metadata metadata = new Metadata();
		metadata.set(Metadata.RESOURCE_NAME_KEY, f.getName());
		//open file
		InputStream is = new FileInputStream(f);
		//use AutoDetectParse to automatically decide parser type
		Parser parser = new AutoDetectParser();
		//exact metadata and bodytext
		ContentHandler handler = new BodyContentHandler(10*1024*1024);
		//setup parse context
		ParseContext context = new ParseContext();
		context.set(Parser.class, parser);
		
		try {
			//parse the document
			parser.parse(is, handler, metadata, new ParseContext());
		} finally {
			//close inputstream after parsing
			is.close();
		}
		
		Document doc = new Document();
		
		//docId
		doc.add(new StringField(MyDocumentIndexedProperties.ID_FIELD, Integer.toString(docId), Field.Store.YES));
		
		//content field
		TokenStream ts = analyzer.tokenStream("contents", new StringReader(handler.toString())); 
		Field contentField = new Field(MyDocumentIndexedProperties.CONTENT_FIELD, AnalyzerUtils.tokenStreamToString(ts), TYPE_STORED);
		ts.close();
		doc.add(contentField);
		
		//metadata fields
		for(String metadataName : metadata.names()) {
			String metadataValue = metadata.get(metadataName);
			for (Property acceptField: textualMetadataFields) {
				if (metadata.get(acceptField) == metadataName) {
					doc.add(new StringField(MyDocumentIndexedProperties.CONTENT_FIELD, metadataValue, Field.Store.YES));
				}
			}
		}
		
		return doc;
	}
	
	/**
	 * Retrieve paths of index files from Hdfs
	 * 
	 * @return
	 * @throws IOException
	 */
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

	/**
	 * Close the IndexWriter, which means committing the indexes
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException {
		this.writer.close();
	}

	/**
	 * Get IndexWriter
	 * 
	 * @return
	 * @throws IOException
	 */
	public IndexWriter getWriter() throws IOException {
		return new IndexWriter(FSDirectory.open(new File(indexDir)),
				new IndexWriterConfig(Version.LUCENE_46, new StandardAnalyzer(
						Version.LUCENE_46)));
	}
	
	/**
	 * Set the IndexWriter
	 * 
	 * @param writer
	 */
	public void setWriter(IndexWriter writer) {
		this.writer = writer;
	}
	
	/**
	 * Get IndexedPath
	 * 
	 * @return
	 */
	public Path[] getIndexedPath() {
		return indexedPath;
	}
	
	/**
	 * get path of directory containing the index
	 * 
	 * @return
	 */
	public String getIndexDir() {
		return this.indexDir;
	}
}
