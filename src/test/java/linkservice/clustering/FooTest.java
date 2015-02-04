package linkservice.clustering;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.LoggerFactory;

public class FooTest {
	org.slf4j.Logger logger = LoggerFactory.getLogger(FooTest.class);
	
	private static String dataDir;
	private static Foo foo;
	@BeforeClass
	public static void setUp() throws Exception {
		dataDir = "src/test/resources/samples/data/test";
		foo = new Foo();
	}
	
	@Test
	public void testRunDirectories() throws Exception {
		logger.info("Inside testRunDirectories()");
		foo.run();
		Collection<File> files = FileUtils.listFiles(new File(dataDir), null, true);
		assertEquals(files.size(), foo.getSequenceSize());
	}
}
