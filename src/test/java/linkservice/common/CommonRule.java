package linkservice.common;

import linkservice.index.Indexer;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * Common facilities for testing
 * 
 * @author newbiettn
 *
 */
public class CommonRule implements TestRule{
	//directory that contains index files
	private String index_dir;
	
	//directory that contains documents needed to be indexed
	private String data_dir;
	
	//directory that contains generated sequence file after converting from index files
	private String sequence_file_dir;
	
	//create a new Indexer object
	private Indexer indexer;
	
	public Statement apply(final Statement base, Description description) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				String configFilePath = "src/test/resources/config.properties";
				LinkServiceGetPropertyValues myDocumentIndexedProp = new LinkServiceGetPropertyValues(configFilePath);
				index_dir = myDocumentIndexedProp.getProperty("linkservice.test.index_dir");
				data_dir = myDocumentIndexedProp.getProperty("linkservice.test.data_dir");
				sequence_file_dir = myDocumentIndexedProp.getProperty("linkesrvice.test.sequence_dir");
				indexer = new Indexer(index_dir, data_dir);
				try {
					base.evaluate();
				} finally {
					//after test
				}
			}
		};
	}
	
	public String getIndexDir() {
		return index_dir;
	}

	public String getDataDir() {
		return data_dir;
	}

	public Indexer getIndexer() {
		return indexer;
	}
	
	public String getSequence_file_dir() {
		return sequence_file_dir;
	}

}
