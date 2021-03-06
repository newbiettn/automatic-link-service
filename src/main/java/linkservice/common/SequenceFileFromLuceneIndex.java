package linkservice.common;

import static java.util.Arrays.asList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import linkservice.document.MyDocumentIndexedProperties;
import linkservice.hadoop.HadoopConfig;
import linkservice.indexing.Indexer;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.mahout.common.Pair;
import org.apache.mahout.text.SequenceFilesFromLuceneStorage;
import org.apache.mahout.text.LuceneStorageConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

/**
 * Convert index files generated by Apache Lucene to Sequence File.
 * 
 * @author newbiettn
 *
 */
public class SequenceFileFromLuceneIndex {
	//logging
	Logger logger = LoggerFactory.getLogger(SequenceFileFromLuceneIndex.class);
	
	private SequenceFilesFromLuceneStorage lucene2Seq;

	//config file for processing from lucene to sequence
	private LuceneStorageConfiguration lucene2SeqConf;
	private Path seqFilesOutputPath;
	private HadoopConfig hadoopConf;
	private Indexer indexer;
	
	/**
	 * Create a new SequenceFileFromLuceneIndex object using the given Indexer.
	 * 
	 * @param anIndexer
	 * @throws IOException
	 */
	public SequenceFileFromLuceneIndex(Indexer anIndexer, String sequence_output_dir) throws IOException {
		indexer = anIndexer;
		hadoopConf = new HadoopConfig();
		seqFilesOutputPath = new Path(sequence_output_dir);
		
		List<Path> indexPaths = new ArrayList<Path>();
		indexPaths.add(new Path(indexer.getIndexDir()));
		
		lucene2Seq = new SequenceFilesFromLuceneStorage();
		lucene2SeqConf = new LuceneStorageConfiguration(hadoopConf.getConf(),
				indexPaths, seqFilesOutputPath, MyDocumentIndexedProperties.ID_FIELD, 
				asList(MyDocumentIndexedProperties.CONTENT_FIELD));
	}
	
	/**
	 * Run the converting
	 * 
	 * @throws Exception
	 */
	public void run() throws Exception {
		lucene2Seq.run(lucene2SeqConf);
	}
	
	/**
	 * Get size of the Sequence File after converting
	 * 
	 * @return
	 */
	public int getSequenceSize() {
		Iterator<Pair<Text, Text>> iterator = lucene2SeqConf
				.getSequenceFileIterator();
		Map<String, Text> map = Maps.newHashMap();
		while (iterator.hasNext()) {
			Pair<Text, Text> next = iterator.next();
			map.put(next.getFirst().toString(), next.getSecond());
		}
		return map.size();
	}
}
