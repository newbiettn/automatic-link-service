package linkservice.clustering;

import java.io.IOException;

import org.apache.hadoop.fs.Path;
import org.apache.mahout.clustering.kmeans.KMeansDriver;
import org.apache.mahout.clustering.kmeans.RandomSeedGenerator;
import org.apache.mahout.common.distance.CosineDistanceMeasure;

public class CluteringByKMeans extends AbstractClustering {
	protected CluteringByKMeans() throws IOException {
		super();
	}

	public void run() throws Exception {
		int k = 10;
		double convergenceDelta = 0.5;
		int maxIterations = 9999;
		
		// Generate Sparse vectors from sequence files
		generateSparseVectors(sequenceFileDir, sparseVectorsDir);

		RandomSeedGenerator.buildRandom(conf, 
				new Path(sparseVectorsDir, "tfidf-vectors"), 
				clusterInputPath, k,
				new CosineDistanceMeasure());

		//kmeans
		KMeansDriver.run(conf,
				new Path(sparseVectorsDir, "tfidf-vectors"),
				clusterInputPath, 
				finalClustersPath, 
				convergenceDelta,
		        maxIterations, true, 0.1, false);
	}
}
