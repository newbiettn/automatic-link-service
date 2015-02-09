package linkservice.common.clustering;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import linkservice.common.hadoop.HadoopConfig;
import linkservice.index.IndexerTest;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.util.ToolRunner;
import org.apache.mahout.clustering.Cluster;
import org.apache.mahout.clustering.canopy.CanopyDriver;
import org.apache.mahout.clustering.classify.WeightedPropertyVectorWritable;
import org.apache.mahout.clustering.kmeans.KMeansDriver;
import org.apache.mahout.clustering.kmeans.RandomSeedGenerator;
import org.apache.mahout.common.HadoopUtil;
import org.apache.mahout.common.distance.CosineDistanceMeasure;
import org.apache.mahout.common.distance.DistanceMeasure;
import org.apache.mahout.common.distance.EuclideanDistanceMeasure;
import org.apache.mahout.common.distance.TanimotoDistanceMeasure;
import org.apache.mahout.text.LuceneStorageConfiguration;
import org.apache.mahout.text.SequenceFilesFromLuceneStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import linkservice.common.hadoop.*;
public class CluteringByKMeans1 {
	static Logger logger = LoggerFactory.getLogger(CluteringByKMeans.class);
	private static final String DIRECTORY_CONTAINING_CONVERTED_INPUT = "data";
	public static void main(String args[]) throws Exception {
		HadoopConfig hadoopConf = new HadoopConfig();
		Path indexFilesPath = new Path("src/test/resources/index");
		
		Path sequenceFilesPath = new Path("testdata");
		
		Configuration conf = hadoopConf.getConf();
		Path input = new Path("testdata");
		Path output = new Path("output");
		DistanceMeasure measure = new EuclideanDistanceMeasure();
		int k = 6;
		double convergenceDelta = 0.5;
		int maxIterations = 10;
		
		LuceneStorageConfiguration luceneStorageConf = new LuceneStorageConfiguration(
				conf, Arrays.asList(indexFilesPath), sequenceFilesPath, "id",
				Arrays.asList("contents"));

		SequenceFilesFromLuceneStorage sequenceFilefromLuceneStorage = new SequenceFilesFromLuceneStorage();
		sequenceFilefromLuceneStorage.run(luceneStorageConf);
		
		Path directoryContainingConvertedInput = new Path(output, DIRECTORY_CONTAINING_CONVERTED_INPUT);
	    //InputDriver.runJob(input, directoryContainingConvertedInput, "org.apache.mahout.math.RandomAccessSparseVector");
	    generateSparseVectors("testdata", directoryContainingConvertedInput.getName());
	    
	    Path clusters = new Path(output, "random-seeds");
	    clusters = RandomSeedGenerator.buildRandom(conf, directoryContainingConvertedInput, clusters, k, measure);

	    KMeansDriver.run(conf, directoryContainingConvertedInput, clusters, output, convergenceDelta,
	        maxIterations, true, 0.0, false);
	}
	public static int generateSparseVectors(String input, String output)
			throws Exception {

		/*
		 * -wt tfidf --> Use the tfidf weighting method. 
		 * -ng 2 --> Use an n-gram, size of 2 to generate both unigrams and bigrams. 
		 * -ml 50 --> Use a log-likelihood ratio (LLR) value of 50 to keep only very significant bigrams.
		 */

		String[] para = {
				//output
				"-o", output,
				//input
				"-i", input,
				//use TFIDF
				"-wt", "tfidf",
				//overwrite the file
				"-ow",
				//use SequentialAccessVectors
				"-seq",
				//use 2-gram
				"-ng", "2",
				//log-normalize the vector
				"-lnorm"};
		int x = new SparseVectorsFromSequenceFiles().run(para);
		return x;
	}
}
