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
import org.apache.mahout.common.distance.EuclideanDistanceMeasure;
import org.apache.mahout.common.distance.TanimotoDistanceMeasure;
import org.apache.mahout.text.LuceneStorageConfiguration;
import org.apache.mahout.text.SequenceFilesFromLuceneStorage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class CluteringByKMeans {
	static Logger logger = LoggerFactory.getLogger(CluteringByKMeans.class);

	public static void main(String args[]) throws Exception {
		//HadoopConfig hadoopConf = new HadoopConfig();
		//Configuration conf = hadoopConf.getConf();
		Configuration conf = new Configuration();
		
		FileSystem fs = FileSystem.get(conf);
		
		String sequenceFileDir = "mahout-work/sequence-file";
		String sparseVectorsDir = "mahout-work/sparse-vector";
		String clusterInputDir = "mahout-work/centroid";
		String finalClusterOutputDir = "mahout-work/clusteroutput";
		
		Path indexFilesPath = new Path("src/test/resources/index");
		Path sequenceFilesPath = new Path(sequenceFileDir);
		Path sparseVectorsPath = new Path(sparseVectorsDir);
		Path clusterInputPath = new Path(clusterInputDir);
		Path finalClustersPath = new Path(finalClusterOutputDir);
		
		//delete old files
		HadoopUtil.delete(conf, new Path("mahout-work"));

		// Create sequence files from Index
		LuceneStorageConfiguration luceneStorageConf = new LuceneStorageConfiguration(
				conf, Arrays.asList(indexFilesPath), sequenceFilesPath, "id",
				Arrays.asList("contents"));

		SequenceFilesFromLuceneStorage sequenceFilefromLuceneStorage = new SequenceFilesFromLuceneStorage();
		sequenceFilefromLuceneStorage.run(luceneStorageConf);

		// Generate Sparse vectors from sequence files
		generateSparseVectors(sequenceFileDir, sparseVectorsDir);
		
		//canopy
//		CanopyDriver.run(conf, 
//				new Path(sparseVectorsDir, "tfidf-vectors"), 
//                new Path(clusterInputDir),
//                new TanimotoDistanceMeasure(),
//				2.5, 1,
//				false, 
//				0, 
//				false);

		int k = 6;

		RandomSeedGenerator.buildRandom(conf, 
				new Path(sparseVectorsDir, "tfidf-vectors"), 
				clusterInputPath, k,
				new CosineDistanceMeasure());

		//kmeans
		double convergenceDelta = 0.5;
		int maxIterations = 100;

		KMeansDriver.run(conf,
				new Path(sparseVectorsDir, "tfidf-vectors"),
				new Path("mahout-work/centroid"), 
				finalClustersPath, 
				convergenceDelta,
		        maxIterations, true, 0.0, false);
		
		// Read and print out the clusters in the console
		SequenceFile.Reader reader = new SequenceFile.Reader(fs,
                new Path(finalClusterOutputDir, Cluster.CLUSTERED_POINTS_DIR + "/part-m-00000"),
                conf);

        IntWritable key = new IntWritable();
        WeightedPropertyVectorWritable value = new WeightedPropertyVectorWritable();
        while (reader.next(key, value)) {
            logger.info(value.toString() + " belongs to cluster " + key.toString());
        }
        reader.close();
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

	private static void readDictionaryAndFrequency(String path1, String path2)throws IOException {
		//HadoopConfig hadoopConf = new HadoopConfig();
		Configuration conf = new Configuration();
		SequenceFile.Reader read1 = new SequenceFile.Reader(
				FileSystem.get(conf), new Path(path1), conf);
		SequenceFile.Reader read2 = new SequenceFile.Reader(
				FileSystem.get(conf), new Path(path2), conf);
		IntWritable dictionaryKey = new IntWritable();
		Text text = new Text();
		LongWritable freq = new LongWritable();
		HashMap<Integer, Long> freqMap = new HashMap<Integer, Long>();
		HashMap<Integer, String> dictionaryMap = new HashMap<Integer, String>();

		/*
		 * Read the contents of dictionary.file-0 and frequency.file-0 and write
		 * them to appropriate HashMaps
		 */

		while (read1.next(text, dictionaryKey)) {
			dictionaryMap.put(Integer.parseInt(dictionaryKey.toString()),
					text.toString());
		}
		while (read2.next(dictionaryKey, freq)) {
			freqMap.put(Integer.parseInt(dictionaryKey.toString()),
					Long.parseLong(freq.toString()));
		}

		read1.close();
		read2.close();

		for (int i = 0; i < dictionaryMap.size(); i++) {
			logger.info("Key " + i + ": " + dictionaryMap.get(i));
		}

		for (int i = 0; i < freqMap.size(); i++) {
			System.out.println("Key " + i + ": " + freqMap.get(i));
		}
	}

}
