package linkservice.clustering.vectors;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import linkservice.clustering.methods.AbstractClustering;
import linkservice.clustering.methods.CluteringByKMeans;
import linkservice.common.GeneralConfigPath;
import linkservice.common.LinkServiceGetPropertyValues;
import linkservice.common.SequenceFileFromLuceneIndex;
import linkservice.indexing.Indexer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.mahout.common.HadoopUtil;
import org.apache.mahout.math.NamedVector;
import org.apache.mahout.math.VectorWritable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test if all corresponding TF-IDF vectors are copied
 * 
 * 
 * @author newbiettn
 *
 */
public class GetTFIDFVectorsTest {
	private List<String> sample;
	Configuration conf;
	LinkServiceGetPropertyValues myDocumentIndexedProp;

	@Before
	public void setUp() throws Exception {
		conf = new Configuration();
		sample = new ArrayList<String>();
		String configFilePath = GeneralConfigPath.PROPERTIES_PATH;

		// get properties helper
		myDocumentIndexedProp = new LinkServiceGetPropertyValues(configFilePath);

		// uri to files
		String dataFileDir = myDocumentIndexedProp
				.getProperty("linkservice.data_dir");
		String indexDir = myDocumentIndexedProp
				.getProperty("linkservice.index_dir");
		String sequenceFileDir = myDocumentIndexedProp
				.getProperty("linkesrvice.sequence_dir");
		String sparseVectorsDir = myDocumentIndexedProp
				.getProperty("linkservice.mahout.sparse_vector_dir");

		// create new indexer
		Indexer indexer = new Indexer(indexDir, dataFileDir);

		SequenceFileFromLuceneIndex lucene2Seq = new SequenceFileFromLuceneIndex(
				indexer, sequenceFileDir);

		// create index files
		indexer.runIndex();
		indexer.close();

		// convert index files to sequence files
		lucene2Seq.run();

		// Generate Sparse vectors from sequence files
		AbstractClustering.generateSparseVectors(sequenceFileDir,
				sparseVectorsDir);

		createSample(sparseVectorsDir);
	}

	public void createSample(String sparseVectorsDir) throws IOException {
		// get top 10 as an sample
		FileSystem fs = FileSystem.get(conf);
		Path p = new Path(sparseVectorsDir, "tfidf-vectors/part-r-00000");
		SequenceFile.Reader reader = new SequenceFile.Reader(fs, p, conf);
		Text key = new Text();
		VectorWritable val = new VectorWritable();
		int i = 0;
		while (reader.next(key, val)) {
			if (i < 10) {
				break;
			}
			sample.add(key.toString());
			i++;
		}
	}

	@Test
	public void test() throws IOException {
		GetTFIDFVectors.copy(sample);
		int outputSize = GetTFIDFVectors.read();
		assertEquals(sample.size(), outputSize);
	}

	@After
	public void tearDown() throws IOException {
		HadoopUtil.delete(conf,
				new Path(myDocumentIndexedProp.getProperty("linkservice.output_root")));

	}
}
