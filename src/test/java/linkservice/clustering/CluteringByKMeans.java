package linkservice.clustering;

import java.io.IOException;

import linkservice.common.hadoop.HadoopConfig;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.mahout.clustering.canopy.CanopyDriver;
import org.apache.mahout.clustering.kmeans.KMeansDriver;
import org.apache.mahout.common.distance.EuclideanDistanceMeasure;

public class CluteringByKMeans {
	public static void main(String[] args) throws ClassNotFoundException,
			IOException, InterruptedException {
		HadoopConfig hadoopConf = new HadoopConfig();
		FileSystem fs = FileSystem.get(hadoopConf.getConf());
		Path indexPath = new Path("sequenceOutput");
		Path canopyCentroids = new Path("sequenceOutput", "canopy-centroids");
		Path clusterOutput = new Path("sequenceOutput", "clusters");

		if (fs.exists(clusterOutput)) {
			fs.delete(clusterOutput, true);
		}
		CanopyDriver.run(hadoopConf.getConf(), indexPath, canopyCentroids,
				new EuclideanDistanceMeasure(), 250, 120, false, 0, false);
		KMeansDriver.run(hadoopConf.getConf(), indexPath, new Path(
				canopyCentroids, "clusters-0"), clusterOutput, 0.01, 20, true,
				0.01, true);
	}
}
