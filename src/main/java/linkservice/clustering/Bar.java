package linkservice.clustering;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.mahout.clustering.Cluster;
import org.apache.mahout.clustering.kmeans.KMeansDriver;
import org.apache.mahout.common.distance.EuclideanDistanceMeasure;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.VectorWritable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bar {
	static Logger logger = LoggerFactory.getLogger(Bar.class);
	
	public static void main(String[] args) throws IOException {
		// int k = 2;
		Configuration configuration = new Configuration();
		configuration.addResource(new Path("/usr/local/Cellar/hadoop121/1.2.1/libexec/conf/core-site.xml"));
		//configuration.addResource(new Path("/usr/local/Cellar/hadoop121/1.2.1/libexec/conf/hdfs-site.xml"));
		FileSystem fs = FileSystem.get(configuration);
		// configuration
		// .addResource(new Path(
		// "/usr/local/Cellar/hadoop121/1.2.1/libexec/conf/core-site.xml"));
		// configuration
		// .addResource(new Path(
		// "/usr/local/Cellar/hadoop121/1.2.1/libexec/conf/hdfs-site.xml"));
		// Path indexpath = new Path("src/test/resources/index");
		// Path clustersIn = new Path("testdata/clusters/part-00000");
		// FileSystem fs = FileSystem.get(configuration);
		// SequenceFile.Writer writer = new SequenceFile.Writer(fs,
		// configuration,
		// clustersIn, Text.class, Cluster.class);
		//
		// for (int i = 0; i < k; i++) {
		// Vector vec = vectors.get(i);
		// Cluster cluster = new Cluster(vec, i,
		// new EuclideanDistanceMeasure());
		// writer.append(new Text(cluster.getIdentifier()), cluster);
		// }
		// writer.close();
		//
		// KMeansDriver.run(configuration, indexpath, clustersIn, new Path(
		// "output"), new EuclideanDistanceMeasure(), 100, 0.001, 0.001,
		// true);

		SequenceFile.Reader reader = new SequenceFile.Reader(fs, new Path("file1.seq"), configuration);
		Text key = new Text();
		VectorWritable value = new VectorWritable();

		while (reader.next(key, value)) {
			logger.info(key.toString() + " "
					+ value.get().asFormatString());
		}
		reader.close();
	}
}
