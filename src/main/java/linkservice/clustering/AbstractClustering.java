package linkservice.clustering;

import java.io.IOException;
import java.util.HashMap;

import linkservice.common.LinkServiceGetPropertyValues;
import linkservice.common.SequenceFileFromLuceneIndex;
import linkservice.index.Indexer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.mahout.vectorizer.SparseVectorsFromSequenceFiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Super class of clustering algorithm implementations
 * 
 * @author newbiettn
 *
 */
public abstract class AbstractClustering {
	private Logger logger = LoggerFactory.getLogger(CluteringByKMeans.class);
	
	protected LinkServiceGetPropertyValues myDocumentIndexedProp;
		
	protected SequenceFileFromLuceneIndex lucene2Seq;
	
	protected String outputRootDir;
	protected String dataFileDir;
	protected String indexFileDir;
	protected String sequenceFileDir;
	protected String sparseVectorsDir;
	protected String clusterInputDir;
	protected String finalClusterOutputDir;
	
	protected Path indexFilesPath;
	protected Path sequenceFilesPath;
	protected Path clusterInputPath;
	protected Path finalClustersPath;
	
	protected Configuration conf;
	protected Indexer indexer;
	
	protected AbstractClustering() throws IOException {
		conf = new Configuration();
		
		myDocumentIndexedProp = new LinkServiceGetPropertyValues("src/main/resources/config.properties");
		
		outputRootDir = myDocumentIndexedProp.getProperty("linkservice.output_root");
		dataFileDir = myDocumentIndexedProp.getProperty("linkservice.data_dir");
		indexFileDir = myDocumentIndexedProp.getProperty("linkservice.index_dir");
		sequenceFileDir = myDocumentIndexedProp.getProperty("linkesrvice.sequence_dir");
		sparseVectorsDir = myDocumentIndexedProp.getProperty("linkservice.mahout.sparse_vector_dir");
		clusterInputDir = myDocumentIndexedProp.getProperty("linkservice.mahout.seeding_cluster_dir");
		finalClusterOutputDir = myDocumentIndexedProp.getProperty("linkservice.mahout.final_cluster_dir");
		
		indexFilesPath = new Path(indexFileDir);
		sequenceFilesPath = new Path(sequenceFileDir);
		clusterInputPath = new Path(clusterInputDir);
		finalClustersPath = new Path(finalClusterOutputDir);
		
		indexer = new Indexer(indexFileDir, dataFileDir);
	}
	
	protected void readDictionaryAndFrequency(String path1, String path2)throws IOException {
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
	
	protected static int generateSparseVectors(String input, String output)
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
				"-lnorm",
				"-nv"
				};
		int x = new SparseVectorsFromSequenceFiles().run(para);
		return x;
	}
}
