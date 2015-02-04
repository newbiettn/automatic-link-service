package linkservice.index;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Indexer {
	//log4j
	static Logger log = Logger.getLogger(Indexer.class.getName());
	
	//path to index files
	private String indexDir;
	
	//path to files folder
	private String dataDir;
	
	//IndexWriter to create and maintain index
	private IndexWriter writer;
	
	Analyzer analyzer;
	
	public Indexer (String indexDir) throws IOException {
		this.indexDir = indexDir;
		
		//create Directory to store indexes, use FSDirectory.open
		//to automatically pick the most suitable directory implementation
		Directory dir = FSDirectory.open(new File(indexDir));
		
		//create StandardAnalyzer to tokenize which uses default stop words 
		analyzer = new StandardAnalyzer(Version.LUCENE_46);
		
		//create configuration for new IndexWriter 
		IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_46, analyzer);
		
		//create IndexWriter, which can create new index, or
		//adds, removes, or updates documents in the index
		//however, it can not read or search
		this.writer = new IndexWriter(dir, conf);
	}
	
	//index files in directory 
	//start with *.txt files, will change later after having crawler
	public int index () throws Exception {
		long start = System.currentTimeMillis();
		Collection<File> files = FileUtils.listFiles(new File(this.dataDir), null, true);
		
		for (File f: files) {
			indexFile(f);
		}
		long end = System.currentTimeMillis();
		log.info("Indexing " + this.writer.numDocs() + " files took "
			      + (end - start) + " milliseconds");
		return this.writer.numDocs();
	}
	
	//add Document to the index
	public void indexFile(File f) throws Exception {
		//log.info("Indexing " + f.getCanonicalPath());
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
	
	//create Document and Fields
	private Document getDocument(File f) throws Exception {
		Document doc = new Document();
		Field contentField = new Field("content", "", TYPE_STORED);
		contentField.setTokenStream(analyzer.tokenStream("content", new FileReader(f)));

		doc.add(contentField);
		doc.add(new StringField("filename", f.getName(), Field.Store.YES));
		doc.add(new StringField("fullpath", f.getCanonicalPath(), Field.Store.YES));
		
		return doc;
	}
	
	//close the IndexWriter, which means committing the indexes
	public void close() throws IOException {
		this.writer.close();
	}
	
	public String getIndexDir() {
		return indexDir;
	}

	public void setIndexDir(String indexDir) {
		this.indexDir = indexDir;
	}

	public String getDataDir() {
		return dataDir;
	}

	public void setDataDir(String dataDir) {
		this.dataDir = dataDir;
	}

	public IndexWriter getWriter() throws IOException {
		return new IndexWriter(FSDirectory.open(new File(indexDir)), 
				new IndexWriterConfig(Version.LUCENE_46, new StandardAnalyzer(Version.LUCENE_46)));
	}

	public void setWriter(IndexWriter writer) {
		this.writer = writer;
	}
}
