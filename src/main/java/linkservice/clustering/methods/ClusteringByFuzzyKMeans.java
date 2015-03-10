package linkservice.clustering.methods;

import java.io.IOException;
import java.util.List;

import linkservice.clustering.vectors.GetTFIDFVectors;
import linkservice.common.ClusterOutput;
import linkservice.searching.result.SearchResultObject;

import org.apache.hadoop.fs.Path;
import org.apache.mahout.clustering.fuzzykmeans.FuzzyKMeansDriver;
import org.apache.mahout.clustering.kmeans.RandomSeedGenerator;
import org.apache.mahout.common.distance.CosineDistanceMeasure;

public class ClusteringByFuzzyKMeans extends AbstractClustering {

	public ClusteringByFuzzyKMeans() throws IOException {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run(List<SearchResultObject> results) throws Exception {
		int k = Math.round((float) Math.sqrt(results.size() / 2));
		double convergenceDelta = 0.5;
		int maxIterations = 100;

		// Generate Sparse vectors from sequence files
		//generateSparseVectors(sequenceFileDir, sparseVectorsDir);

		// exact keys from search result
		List<String> keys = getListOfKeysFromResults(results);

		// exact corresponding vectors
		GetTFIDFVectors.copy(keys);

		RandomSeedGenerator.buildRandom(conf, new Path(sparseVectorsByQueryDir,
				"tfidf-vectors"), clusterInputPath, k,
				new CosineDistanceMeasure());

		FuzzyKMeansDriver.run(conf, new Path(sparseVectorsByQueryDir,
				"tfidf-vectors"), clusterInputPath, finalClustersPath,
				convergenceDelta, maxIterations, 3, true, true, 0.01, false);
		
		// dump results
		String clusterFinalFile = finalClusterOutputDir + "/clusters-0";
		String clusteredPoints = finalClusterOutputDir + "/clusteredPoints";
		String outputFile = outputRootDir + "/output.txt";
		String dictionaryFile = sparseVectorsDir + "/dictionary.file-0";

		ClusterOutput clusterOutput = new ClusterOutput(clusterFinalFile,
				clusteredPoints, outputFile, dictionaryFile);
		clusterOutput.emptyOuputFile();
		clusterOutput.run();
	}
}
