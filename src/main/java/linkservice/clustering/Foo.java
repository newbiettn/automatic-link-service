package linkservice.clustering;

import static java.util.Arrays.asList;

import java.util.Iterator;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.mahout.common.Pair;
import org.apache.mahout.text.SequenceFilesFromLuceneStorage;
import org.apache.mahout.text.LuceneStorageConfiguration;

import com.google.common.collect.Maps;

public class Foo {
	SequenceFilesFromLuceneStorage lucene2Seq;
	LuceneStorageConfiguration lucene2SeqConf;
	Path seqFilesOutputPath;
	Configuration configuration;

	public Foo() {
		configuration = new Configuration();
		configuration.addResource(new Path("/usr/local/Cellar/hadoop121/1.2.1/libexec/conf/core-site.xml"));
		configuration.addResource(new Path("/usr/local/Cellar/hadoop121/1.2.1/libexec/conf/hdfs-site.xml"));
		Path indexpath = new Path("src/test/resources/index");
		seqFilesOutputPath = new Path("src/test/resources", "temp");
		
		lucene2Seq = new SequenceFilesFromLuceneStorage();
		lucene2SeqConf = new LuceneStorageConfiguration(
				configuration, asList(indexpath), seqFilesOutputPath, "filename",
				asList("content"));
	}
	
	public void run() throws Exception {
		lucene2Seq.run(lucene2SeqConf);
	}
	
	public int getSequenceSize() {
		Iterator<Pair<Text, Text>> iterator = lucene2SeqConf.getSequenceFileIterator();
	    Map<String, Text> map = Maps.newHashMap();
	    while (iterator.hasNext()) {
	      Pair<Text, Text> next = iterator.next();
	      map.put(next.getFirst().toString(), next.getSecond());
	    }
	    return map.size();
	}
}
