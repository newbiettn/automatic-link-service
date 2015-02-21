package linkservice.common;

import java.io.IOException;

import linkservice.indexing.MyCustomAnalyzer;

import org.apache.lucene.analysis.TokenStream;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.slf4j.Logger;

/**
 * Test AnalyzerUtils
 * 
 * @author newbiettn
 *
 */
public class AnylyzerUtilsTest {
	@ClassRule
	public static LoggerRule testLogger = new LoggerRule();
	
	private MyCustomAnalyzer myCustomAnalyzer;
	
	private TokenStream ts;
	
	private String sampleString;
	
	private static Logger logger;
	
	@Before
	public void setUp() throws IOException {
		logger = testLogger.getLogger();
		myCustomAnalyzer = new MyCustomAnalyzer();
		sampleString = "this is a sample string for stemming words, "
				+ "removing stop words by custom analyzer";
		ts = myCustomAnalyzer.tokenStream("contents", sampleString);
	}
	
	/**
	 * Test if the analyzer returns expecting string
	 * 
	 * @throws IOException
	 */
	@Test
	public void test1Displaytokens() throws IOException {
		logger.info("inside test1Displaytokens()");
		AnalyzerUtils.displayTokens(ts);
	}
	
	/**
	 * Test if the tokens are concatenated into 1 single string 
	 * 
	 * @throws IOException
	 */
	@Test
	public void test2TokenStreamToString() throws IOException {
		logger.info("inside test2TokenStreamToString()");
		String result = AnalyzerUtils.tokenStreamToString(ts);
		logger.info(result);
	}
}
