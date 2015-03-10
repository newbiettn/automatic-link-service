package linkservice.common;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.mahout.common.HadoopUtil;

public class UpdatePath {
	public static boolean isOutputEmpty() throws IOException {
		LinkServiceGetPropertyValues myDocumentIndexedProp = new LinkServiceGetPropertyValues(GeneralConfigPath.PROPERTIES_PATH);
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(conf);
		
		Path outputPath = new Path(myDocumentIndexedProp.getProperty("linkservice.output_root"));
		
		if (fs.exists(outputPath)) {
			return false;
		}
		return true;
	}
	public static void cleanIfHaveNewIndex() throws IOException {
		LinkServiceGetPropertyValues myDocumentIndexedProp = new LinkServiceGetPropertyValues(GeneralConfigPath.PROPERTIES_PATH);
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(conf);
		
		Path sequenceFilePath = new Path(myDocumentIndexedProp.getProperty("linkservice.sequence_dir"));
		Path sparseVectorPath = new Path(myDocumentIndexedProp.getProperty("linkservice.mahout.sparse_vector_dir")); 
		Path clusteringPath = new Path(myDocumentIndexedProp.getProperty("linkservice.mahout.clustering_root"));
		
		if (fs.exists(sequenceFilePath)) {
			HadoopUtil.delete(conf, sequenceFilePath);
		}
		
		if (fs.exists(sparseVectorPath)) {
			HadoopUtil.delete(conf, sparseVectorPath);
		}
		
		if (fs.exists(clusteringPath)) {
			HadoopUtil.delete(conf, clusteringPath);
		}
	}
	
	public static void cleanForNewClustering() throws IOException {
		LinkServiceGetPropertyValues myDocumentIndexedProp = new LinkServiceGetPropertyValues(GeneralConfigPath.PROPERTIES_PATH);
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(conf);
		 
		Path clusteringPath = new Path(myDocumentIndexedProp.getProperty("linkservice.mahout.clustering_root"));
		
		if (fs.exists(clusteringPath)) {
			HadoopUtil.delete(conf, clusteringPath);
		}
	}
	
}
