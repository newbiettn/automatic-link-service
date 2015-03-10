package linkservice.clustering.methods;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import linkservice.common.GeneralConfigPath;
import linkservice.common.LinkServiceGetPropertyValues;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.mahout.clustering.AbstractCluster;
import org.apache.mahout.clustering.classify.WeightedPropertyVectorWritable;
import org.apache.mahout.clustering.classify.WeightedVectorWritable;
import org.apache.mahout.clustering.iterator.ClusterWritable;
import org.apache.mahout.common.iterator.sequencefile.PathType;
import org.apache.mahout.common.iterator.sequencefile.SequenceFileDirValueIterable;
import org.apache.mahout.math.NamedVector;
import org.apache.mahout.math.Vector;
import org.apache.mahout.utils.clustering.AbstractClusterWriter;
import org.apache.mahout.utils.clustering.ClusterDumper;
import org.apache.mahout.utils.vectors.VectorHelper;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClusteringExp {
	Logger logger = LoggerFactory.getLogger(ClusteringExp.class);
	String[] dictionary;
	int numTopFeatures = 10;
	Configuration conf = new Configuration();

	@Before
	public void setUp() throws IOException {
		dictionary = VectorHelper.loadTermDictionary(conf,
				"output/sparse_vectors/dictionary.file-0");
	}

	@Test
	public void test1() {
		Iterable<ClusterWritable> iterable = new SequenceFileDirValueIterable<ClusterWritable>(
				new Path("output/final_clusters/clusters-2-final", "part-*"),
				PathType.GLOB, conf);
		Iterator<ClusterWritable> iterator = iterable.iterator();
		//iterator of clusters
		while (iterator.hasNext()) {
			//handle each clusters
			write(iterator.next());
		}
	}
	
//	@Test 
//	public void test2(ClusterWritable clusterWritable) {
//		Map<Integer, List<WeightedPropertyVectorWritable>> clusterIdToPoints = ClusterDumper
//				.readPoints(new Path("output/final_clusters/clusteredPoints"),
//						Long.MAX_VALUE, conf);
//		List<WeightedPropertyVectorWritable> points = clusterIdToPoints.get(clusterWritable.getValue().getId());
//		
//	}
	
	public void write(ClusterWritable clusterWritable) {
		//get top terms for the clusters
		String topTerms = AbstractClusterWriter.getTopFeatures(clusterWritable
				.getValue().getCenter(), dictionary, numTopFeatures);
		logger.info(clusterWritable.getValue().getId() + "");
		logger.info(topTerms);
		
		//
		Map<Integer, List<WeightedPropertyVectorWritable>> clusterIdToPoints = ClusterDumper
				.readPoints(new Path("output/final_clusters/clusteredPoints"),
						Long.MAX_VALUE, conf);
		//get list of points for the cluster
		List<WeightedPropertyVectorWritable> points = clusterIdToPoints.get(clusterWritable.getValue().getId());
		
		//loop the list
		for (Iterator<WeightedPropertyVectorWritable> iterator = points.iterator(); iterator.hasNext();) {
			//the point
			WeightedVectorWritable point = iterator.next();
			// writer.write(String.valueOf(point.getWeight()));
			
			//get distance for points
//			if (point instanceof WeightedPropertyVectorWritable) {
//				WeightedPropertyVectorWritable tmp = (WeightedPropertyVectorWritable) point;
//				Map<Text, Text> map = tmp.getProperties();
//				// map can be null since empty maps when written are returned as
//				// null
//				System.out.print(" : [");
//				if (map != null) {
//					for (Map.Entry<Text, Text> entry : map.entrySet()) {
//						System.out.print(entry.getKey().toString());
//						System.out.print("=");
//						System.out.print(entry.getValue().toString());
//					}
//				}
//				System.out.print("]");
//			}
			System.out.println(point.getVector().asFormatString());
		}
	}
	
	public String formatVector(Vector v, String[] bindings){
		String name = ((NamedVector) v).getName();
		return name;
		
	}
	
	public void readDictionary() throws IOException {
		FileSystem fs = FileSystem.get(conf);
		SequenceFile.Reader read = new SequenceFile.Reader(fs, new Path(
				"output/sparse_vectors/dictionary.file-0"), conf);
		IntWritable dicKey = new IntWritable();
		Text text = new Text();
		HashMap<Integer, String> dictionaryMap = new HashMap<Integer, String>();
		while (read.next(text, dicKey)) {
			dictionaryMap.put(Integer.parseInt(dicKey.toString()),
					text.toString());
			logger.info(dicKey + "\t" + text);
		}
		read.close();
	}
}
