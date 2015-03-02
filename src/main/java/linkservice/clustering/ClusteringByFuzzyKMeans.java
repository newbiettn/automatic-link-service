package linkservice.clustering;

import java.io.IOException;
import java.util.Set;

import linkservice.clustering.vectors.GetTFIDFVectors;
import linkservice.searching.result.SearchResultObject;

import org.apache.hadoop.fs.Path;
import org.apache.mahout.clustering.fuzzykmeans.FuzzyKMeansDriver;
import org.apache.mahout.clustering.kmeans.RandomSeedGenerator;
import org.apache.mahout.common.distance.CosineDistanceMeasure;

public class ClusteringByFuzzyKMeans extends AbstractClustering {

	protected ClusteringByFuzzyKMeans() throws IOException {
		super();
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void run(Set<SearchResultObject> results) throws Exception {
		int k = Math.round((float)Math.sqrt(results.size()/2));
		double convergenceDelta = 0.1;
		int maxIterations = 100;
		
		// Generate Sparse vectors from sequence files
		generateSparseVectors(sequenceFileDir, sparseVectorsDir);
		
		//exact keys from search result
		Set<String> keys = getListOfKeysFromResults(results);
		
		//exact corrresponding vectors
		GetTFIDFVectors.copy(keys);
		
		RandomSeedGenerator.buildRandom(conf, new Path(sparseVectorsByQueryDir,
				"tfidf-vectors"), clusterInputPath, k,
				new CosineDistanceMeasure());

		FuzzyKMeansDriver.run(conf,
				new Path(sparseVectorsByQueryDir, "tfidf-vectors"), clusterInputPath,
				finalClustersPath, convergenceDelta, maxIterations, 3, true,
				true, 0.01, false);
	}
}
