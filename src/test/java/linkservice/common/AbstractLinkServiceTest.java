package linkservice.common;

import linkservice.index.Indexer;

public abstract class AbstractLinkServiceTest {
	//path to index files
	protected final static String INDEX_DIR = "src/test/resources/index";
	
	//path to files folder that contains documents needed to be indexed
	protected final static String DATA_DIR = "src/test/resources/samples/data/test/alt.atheism";
	
	protected static Indexer indexer;
	
	protected final static Indexer getIndexer() {
		indexer = new Indexer(INDEX_DIR, DATA_DIR);
		return indexer;
	}
	
}
