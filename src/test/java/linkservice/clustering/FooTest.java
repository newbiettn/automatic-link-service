package linkservice.clustering;

import static org.junit.Assert.assertEquals;
import linkservice.index.IndexerTest;

import org.apache.log4j.Logger;
import org.junit.Test;

public class FooTest {
	static Logger log = Logger.getLogger(IndexerTest.class.getName());
	
	@Test
	public void testRun2Directories() throws Exception {
		Foo foo = new Foo();
		foo.run();
		int i = 7000;
		assertEquals(i, foo.getSequenceSize());
	}
}
