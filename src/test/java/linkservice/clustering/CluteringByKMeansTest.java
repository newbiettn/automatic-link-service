package linkservice.clustering;

import org.junit.Test;

import linkservice.common.ClusterOutput;
import linkservice.common.LinkServiceGetPropertyValues;

public class CluteringByKMeansTest {
	
	@Test
	public void test1() throws Exception {
		CluteringByKMeans clusteringByKMeans = new CluteringByKMeans();
		clusteringByKMeans.run();
		
		String configFilePath = "src/main/resources/config.properties";
		LinkServiceGetPropertyValues myDocumentIndexedProp = new LinkServiceGetPropertyValues(configFilePath);
		String clusterFinalFile = 
				myDocumentIndexedProp.getProperty("linkservice.mahout.final_cluster_dir") 
				+ "/clusters-2-final";
		String clusteredPoints = 
				myDocumentIndexedProp.getProperty("linkservice.mahout.final_cluster_dir")
				+ "/clusteredPoints";
		String outputFile = 
				myDocumentIndexedProp.getProperty("linkservice.output_root")
				+ "/output.txt";
		String dictionaryFile = 
				myDocumentIndexedProp.getProperty("linkservice.data_dir")
				+ "/dictionary.file-0";
		
		ClusterOutput clusterOutput = new ClusterOutput(
				clusterFinalFile,
				clusteredPoints,
				outputFile,
				dictionaryFile);
		clusterOutput.emptyOuputFile();
		clusterOutput.run();
	}
}
