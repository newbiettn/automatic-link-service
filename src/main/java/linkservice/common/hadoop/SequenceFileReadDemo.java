package linkservice.common.hadoop;

import java.io.IOException;
import java.net.URI;

import linkservice.index.Indexer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.util.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SequenceFileReadDemo {
	static Logger logger = LoggerFactory.getLogger(SequenceFileReadDemo.class);

	public static void main(String[] args) throws IOException {
		String uri = "mahout-work/clusteroutput/clusteredPoints/part-m-0";
		//HadoopConfig hadoopConf = new HadoopConfig();
		//Configuration conf = hadoopConf.getConf();
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(URI.create(uri), conf);
		Path path = new Path(uri);

		SequenceFile.Reader reader = null;
		try {
			reader = new SequenceFile.Reader(fs, path, conf);
			Writable key = (Writable) ReflectionUtils.newInstance(
					reader.getKeyClass(), conf);
			Writable value = (Writable) ReflectionUtils.newInstance(
					reader.getValueClass(), conf);
			long position = reader.getPosition();
			while (reader.next(key, value)) {
				String syncSeen = reader.syncSeen() ? "*" : "";
				logger.info("Key: " + key + " value:" + value);
				position = reader.getPosition(); // beginning of next record
			}
		} finally {
			IOUtils.closeStream(reader);
		}
	}
}