package linkservice.clustering;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.mahout.common.HadoopUtil;
import org.junit.Test;

import linkservice.common.ClusterOutput;
import linkservice.common.LinkServiceGetPropertyValues;
import linkservice.common.SequenceFileFromLuceneIndex;
import linkservice.indexing.Indexer;

public class CluteringByKMeansTest {

	@Test
	public void test1() throws Exception {
		Configuration conf = new Configuration();
		String configFilePath = "src/main/resources/config.properties";
		
		//get properties helper
		LinkServiceGetPropertyValues myDocumentIndexedProp = new LinkServiceGetPropertyValues(
				configFilePath);
		
		CluteringByKMeans clusteringByKMeans = new CluteringByKMeans();
		
		//uri to files
		String indexDir = clusteringByKMeans.getIndexFileDir();
		String dataFileDir = clusteringByKMeans.getDataFileDir();
		String sequenceFileDir = clusteringByKMeans.getSequenceFileDir();
		String finalClusterOutputDir = clusteringByKMeans
				.getFinalClusterOutputDir();
		String outputRootDir = clusteringByKMeans.getOutputRootDir();
		String sparseVectorsDir = clusteringByKMeans.getSparseVectorsDir();
		
		//create new indexer
		Indexer indexer = new Indexer(indexDir, dataFileDir);
		
		SequenceFileFromLuceneIndex lucene2Seq = 
				new SequenceFileFromLuceneIndex(indexer, sequenceFileDir);
		
		//empty the folder before doing anything
		HadoopUtil.delete(conf,
				new Path(myDocumentIndexedProp.getProperty("linkservice.output_root")));
		
		//create index files
		indexer.runIndex();
		indexer.close();
		
		//convert index files to sequence files
		lucene2Seq.run();
		
		//run clustering using sequence files
		clusteringByKMeans.run();

		// dump results
		String clusterFinalFile = finalClusterOutputDir + "/clusters-2-final";
		String clusteredPoints = finalClusterOutputDir + "/clusteredPoints";
		String outputFile = outputRootDir + "/output.txt";
		String dictionaryFile = sparseVectorsDir + "/dictionary.file-0";

		ClusterOutput clusterOutput = new ClusterOutput(clusterFinalFile,
				clusteredPoints, outputFile, dictionaryFile);
		clusterOutput.emptyOuputFile();
		clusterOutput.run();
	}
}
