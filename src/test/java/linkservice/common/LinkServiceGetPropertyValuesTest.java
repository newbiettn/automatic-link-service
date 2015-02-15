package linkservice.common;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

public class LinkServiceGetPropertyValuesTest {
	
	private LinkServiceGetPropertyValues myDocumentIndexedProp;
	
	@Before
	public void setUp() throws IOException {
		String configFilePath = "src/test/resources/config.properties";
		 myDocumentIndexedProp = new LinkServiceGetPropertyValues(configFilePath);
	}
	
	@Test
	public void testDisplayConfigurationProperties() {
		String index_dir = myDocumentIndexedProp.getProperty("linkservice.test.index_dir");
		assertEquals("test_output/index_files", index_dir);
	}
}
