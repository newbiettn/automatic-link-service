package linkservice.common;

import java.io.IOException;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.util.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Output the content of sequence file for further examination
 * 
 * @author newbiettn
 *
 */
public class SequenceFileReader {
	static Logger logger = LoggerFactory.getLogger(SequenceFileReader.class);

	public static void print(String uri) throws IOException {
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
				// String syncSeen = reader.syncSeen() ? "*" : "";
				logger.info("Key: " + key + " value:" + value);
				position = reader.getPosition(); // beginning of next record
			}
		} finally {
			IOUtils.closeStream(reader);

		}
	}
}