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
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
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

import com.google.common.base.Charsets;
import com.google.common.io.BaseEncoding;

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
		TYPE_STORED
				.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
		TYPE_STORED.freeze();
	}
	
	public static final FieldType TYPE_SEARCH_BUT_NOT_CLUSTER = new FieldType();
	
	static {
		TYPE_SEARCH_BUT_NOT_CLUSTER.setIndexed(true);
		TYPE_SEARCH_BUT_NOT_CLUSTER.setTokenized(true);
		TYPE_SEARCH_BUT_NOT_CLUSTER.setStored(true);
		TYPE_SEARCH_BUT_NOT_CLUSTER.setStoreTermVectors(true);
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

	// for verify if new documents added to the index
	private boolean isIndexChanged;

	/**
	 * Create an Indexer using the given directory to store index files, and the
	 * directory need to be indexed.
	 * 
	 * */
	public Indexer(String anIndexDir, String aDataDir) {
		// get config singleton
		hadoopConf = new HadoopConfig();

		// set place to store indexes
		this.indexDir = anIndexDir;

		// set directory has to be indexed
		this.dataDir = aDataDir;
		
		this.isIndexChanged = false;

		try {
			// create Directory to store indexes, use FSDirectory.open
			// to automatically pick the most suitable directory implementation
			Directory dir = FSDirectory.open(new File(indexDir));

			// create StandardAnalyzer to tokenize which uses default stop words
			// analyzer = new MyCustomAnalyzer(new
			// StandardAnalyzer(Version.LUCENE_46,
			// MyStopWords.MY_ENGLISH_STOP_WORDS_SET));
			// analyzer = new StandardAnalyzer(Version.LUCENE_46,
			// MyStopWords.MY_ENGLISH_STOP_WORDS_SET);
			analyzer = new MyCustomAnalyzer();

			// create configuration for new IndexWriter
			IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_46,
					analyzer);

			// create IndexWriter, which can create new index, or
			// adds, removes, or updates documents in the index
			// however, it can not read or search
			this.writer = new IndexWriter(dir, conf);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Index files in directory by looping through documents in directories and
	 * sub-directories
	 * 
	 * @return Number of documents is indexed
	 * @throws Exception
	 */
	//
	public int runIndex() throws Exception {
		long start = System.currentTimeMillis();
		Collection<File> files = FileUtils.listFiles(new File(this.dataDir),
				null, true);
		IndexReader indexReader = DirectoryReader.open(writer, false);
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);

		for (File f : files) {
			Term filePathTerm = new Term(
					MyDocumentIndexedProperties.FILE_PATH_FIELD,
					f.getCanonicalPath());
			TermQuery termQuery = new TermQuery(filePathTerm);
			TopDocs topDocs = indexSearcher.search(termQuery, 1);
			if (topDocs.totalHits > 0) {
				this.isIndexChanged = true;
				continue;
			}
			indexFile(f);
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
	public void indexFile(File f) throws Exception {
		// logger.info("Indexing " + f.getCanonicalPath());
		Document doc = getDocument(f);
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
	private Document getDocument(File f) throws Exception {
		// create metadata
		Metadata metadata = new Metadata();
		metadata.set(Metadata.RESOURCE_NAME_KEY, f.getName());
		// open file
		InputStream is = new FileInputStream(f);
		System.out.println(f.getAbsoluteFile());
		// use AutoDetectParse to automatically decide parser type
		Parser parser = new AutoDetectParser();
		// exact metadata and bodytext
		ContentHandler handler = new BodyContentHandler(10 * 1024 * 1024);
		// setup parse context
		ParseContext context = new ParseContext();
		context.set(Parser.class, parser);

		try {
			// parse the document
			parser.parse(is, handler, metadata, new ParseContext());
		}  catch (Throwable e) {
			System.out.println(e);
	    } finally {
			// close inputstream after parsing
			is.close();
		}

		Document doc = new Document();

		// docId
		// generated by base64 of filepath
		String id = BaseEncoding.base64().encode(
				f.getCanonicalPath().getBytes(Charsets.US_ASCII));
		doc.add(new StringField(MyDocumentIndexedProperties.ID_FIELD, id,
				Field.Store.YES));

		// mime type
		doc.add(new StringField(MyDocumentIndexedProperties.MIME_TYPE_FIELD,
				metadata.get(Metadata.CONTENT_TYPE), Field.Store.YES));

		// filename
//		doc.add(new StringField(MyDocumentIndexedProperties.FILE_NAME_FIELD, f
//				.getName(), Field.Store.YES));
		doc.add(new Field(MyDocumentIndexedProperties.FILE_NAME_FIELD, f.getName(), TYPE_SEARCH_BUT_NOT_CLUSTER));
		
		// title
		if (metadata.get(Metadata.TITLE) != null ) {
			doc.add(new Field(MyDocumentIndexedProperties.TITLE_FIELD, metadata.get(Metadata.TITLE), TYPE_SEARCH_BUT_NOT_CLUSTER));
		}
		
		// filepath
		doc.add(new StringField(MyDocumentIndexedProperties.FILE_PATH_FIELD, f
				.getCanonicalPath(), Field.Store.YES));
		
		//content field without filtering for display and search
		Analyzer standardAnalyzer = new HighlighterPurposeAnalyzer();
		TokenStream standardTs = standardAnalyzer.tokenStream(
				MyDocumentIndexedProperties.CONTENT_FIELD_NO_FILTERING_TYPE, new StringReader(
						handler.toString()));
		Field contentNoFilteredField = new Field(
				MyDocumentIndexedProperties.CONTENT_FIELD_NO_FILTERING_TYPE,
				AnalyzerUtils.tokenStreamToString(standardTs), TYPE_STORED);
		standardAnalyzer.close();
		doc.add(contentNoFilteredField);
		
		// content field
		TokenStream ts = analyzer.tokenStream(
				MyDocumentIndexedProperties.CONTENT_FIELD, new StringReader(
						handler.toString()));
		Field contentField = new Field(
				MyDocumentIndexedProperties.CONTENT_FIELD,
				AnalyzerUtils.tokenStreamToString(ts), TYPE_STORED);
		ts.close();
		doc.add(contentField);

		// metadata fields
		for (String metadataName : metadata.names()) {
			String metadataValue = metadata.get(metadataName);
			for (Property acceptField : textualMetadataFields) {
				if (metadata.get(acceptField) == metadataName) {
					doc.add(new StringField(
							acceptField.getName(),
							metadataValue, Field.Store.YES));
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
	/**
	 * 
	 * @return
	 */
	public boolean isIndexChanged() {
		return isIndexChanged;
	}
	
	/**
	 * 
	 * @param isIndexChanged
	 */
	public void setIndexChanged(boolean isIndexChanged) {
		this.isIndexChanged = isIndexChanged;
	}
}
