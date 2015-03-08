package linkservice.clustering;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import linkservice.common.LinkServiceGetPropertyValues;
import linkservice.document.MyDocument;
import linkservice.searching.result.SearchResultObject;

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

	protected String outputRootDir;

	protected String dataFileDir;
	protected String indexFileDir;
	protected String sequenceFileDir;
	protected String sparseVectorsDir;
	protected String sparseVectorsByQueryDir;
	protected String clusterInputDir;
	protected String finalClusterOutputDir;

	protected Path indexFilesPath;
	protected Path sequenceFilesPath;
	protected Path clusterInputPath;
	protected Path finalClustersPath;

	protected Configuration conf;

	/**
	 * Required method for algorithm running
	 * 
	 * @throws Exception
	 * 
	 */
	public void run(List<SearchResultObject> results) throws Exception {};

	protected AbstractClustering() throws IOException {
		conf = new Configuration();

		myDocumentIndexedProp = new LinkServiceGetPropertyValues(
				"src/main/resources/config.properties");

		dataFileDir = myDocumentIndexedProp.getProperty("linkservice.data_dir");
		outputRootDir = myDocumentIndexedProp
				.getProperty("linkservice.output_root");
		indexFileDir = myDocumentIndexedProp
				.getProperty("linkservice.index_dir");
		sequenceFileDir = myDocumentIndexedProp
				.getProperty("linkesrvice.sequence_dir");
		sparseVectorsDir = myDocumentIndexedProp
				.getProperty("linkservice.mahout.sparse_vector_dir");
		sparseVectorsByQueryDir = myDocumentIndexedProp
				.getProperty("linkservice.mahout.sparse_vector_by_query_dir");
		clusterInputDir = myDocumentIndexedProp
				.getProperty("linkservice.mahout.seeding_cluster_dir");
		finalClusterOutputDir = myDocumentIndexedProp
				.getProperty("linkservice.mahout.final_cluster_dir");

		indexFilesPath = new Path(indexFileDir);
		sequenceFilesPath = new Path(sequenceFileDir);
		clusterInputPath = new Path(clusterInputDir);
		finalClustersPath = new Path(finalClusterOutputDir);
	}

	protected void readDictionaryAndFrequency(String path1, String path2)
			throws IOException {
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

	/**
	 * Get a list of document ids of the search result returned by the
	 * searcher. The list will be used to exact corresponding vectors from
	 * TF-IDF vectors to build query-specific set of TF-IDF vectors.
	 * 
	 * The exacted set of vectors will be the input of clustering process
	 * 
	 * @param results
	 * @return
	 */
	public List<String> getListOfKeysFromResults(List<SearchResultObject> results) {
		List<String> result = new ArrayList<String>();
		for (SearchResultObject ele : results) {
			MyDocument doc = ele.getMyDoc();
			result.add(doc.getId());
		}
		return result;
	}

	public static int generateSparseVectors(String input, String output)
			throws Exception {

		/*
		 * -wt tfidf --> Use the tfidf weighting method. -ng 2 --> Use an
		 * n-gram, size of 2 to generate both unigrams and bigrams. -ml 50 -->
		 * Use a log-likelihood ratio (LLR) value of 50 to keep only very
		 * significant bigrams.
		 */

		String[] para = {
				// output
				"-o", output,
				// input
				"-i", input,
				// use TFIDF
				"-wt", "tfidf",
				// overwrite the file
				"-ow",
				// use SequentialAccessVectors
				"-seq",
				// use 2-gram
				"-ng", "2",
				// log-normalize the vector
				"-lnorm", "-nv" };
		int x = new SparseVectorsFromSequenceFiles().run(para);
		return x;
	}

	public LinkServiceGetPropertyValues getMyDocumentIndexedProp() {
		return myDocumentIndexedProp;
	}

	public String getOutputRootDir() {
		return outputRootDir;
	}

	public String getDataFileDir() {
		return dataFileDir;
	}

	public String getIndexFileDir() {
		return indexFileDir;
	}

	public String getSequenceFileDir() {
		return sequenceFileDir;
	}

	public String getSparseVectorsDir() {
		return sparseVectorsDir;
	}

	public String getClusterInputDir() {
		return clusterInputDir;
	}

	public String getFinalClusterOutputDir() {
		return finalClusterOutputDir;
	}

	public Path getIndexFilesPath() {
		return indexFilesPath;
	}

	public Path getSequenceFilesPath() {
		return sequenceFilesPath;
	}

	public Path getClusterInputPath() {
		return clusterInputPath;
	}

	public Path getFinalClustersPath() {
		return finalClustersPath;
	}

	public Configuration getConf() {
		return conf;
	}

	public String getSparseVectorsByQueryDir() {
		return sparseVectorsByQueryDir;
	}

	public void setSparseVectorsByQueryDir(String sparseVectorsByQueryDir) {
		this.sparseVectorsByQueryDir = sparseVectorsByQueryDir;
	}
}
