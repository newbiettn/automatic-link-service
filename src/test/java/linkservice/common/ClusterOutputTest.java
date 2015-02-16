package linkservice.common;

import java.io.FileNotFoundException;

import org.junit.Before;
import org.junit.Test;

public class ClusterOutputTest {
	private ClusterOutput clusterOutput;
	
	@Before
	public void setUp() throws FileNotFoundException {
		clusterOutput = new ClusterOutput(
				"output/final_clusters/clusters-2-final",
				"output/final_clusters/clusteredPoints",
				"output/output.txt",
				"output/sparse_vectors/dictionary.file-0");
		clusterOutput.emptyOuputFile();
	}
	
	@Test
	public void test1RunClusterOutput() throws Exception {
		clusterOutput.run();
	}
}
