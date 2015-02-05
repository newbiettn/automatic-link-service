package linkservice.clustering;

import static java.util.Arrays.asList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import linkservice.common.hadoop.HadoopConfig;
import linkservice.index.Indexer;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.mahout.common.Pair;
import org.apache.mahout.text.SequenceFilesFromLuceneStorage;
import org.apache.mahout.text.LuceneStorageConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

public class SequenceFileFromLuceneIndex {
	Logger logger = LoggerFactory.getLogger(SequenceFileFromLuceneIndex.class);
	
	private SequenceFilesFromLuceneStorage lucene2Seq;
	private LuceneStorageConfiguration lucene2SeqConf;
	private Path seqFilesOutputPath;
	private HadoopConfig hadoopConf;
	private Indexer indexer;
	
	public SequenceFileFromLuceneIndex(Indexer anIndexer) throws IOException {
		this.indexer = anIndexer;
		
		hadoopConf = new HadoopConfig();
				
		seqFilesOutputPath = new Path("sequenceOutput");
		
		List<Path> indexPaths = new ArrayList<Path>();
		indexPaths.add(new Path("src/test/resources/index"));
		
		lucene2Seq = new SequenceFilesFromLuceneStorage();
		
		lucene2SeqConf = new LuceneStorageConfiguration(hadoopConf.getConf(),
				indexPaths, seqFilesOutputPath, "id", asList("content"));
	}
	
	public void run() throws Exception {
		lucene2Seq.run(lucene2SeqConf);
	}

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
