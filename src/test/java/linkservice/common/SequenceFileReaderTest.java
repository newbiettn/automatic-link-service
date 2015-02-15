package linkservice.common;

import java.io.IOException;

import org.junit.Test;

/**
 * Test SequenceFileReader class
 * 
 * @author newbiettn
 *
 */
public class SequenceFileReaderTest {
	
	@Test
	public void testDisplaySequnceFile() throws IOException {
		String uri = "output/final_clusters/clusteredPoints/part-m-00000";
		SequenceFileReader.print(uri);
	}
}
