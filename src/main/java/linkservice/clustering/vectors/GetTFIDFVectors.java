package linkservice.clustering.vectors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.mahout.math.NamedVector;
import org.apache.mahout.math.VectorWritable;

public class GetTFIDFVectors {
	public static int read() throws IOException {
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(conf);
		Path outputPath = new Path(
				"output/sparse_vectors_by_query/tfidf-vectors/part-r-00000");
		SequenceFile.Reader reader = new SequenceFile.Reader(fs, outputPath,
				conf);
		Text key = new Text();
		VectorWritable val = new VectorWritable();
		int size = 0;
		while (reader.next(key, val)) {
			size++;
		}
		return size;
	}

	public static void copy(List<String> sample) throws IOException {
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(conf);
		Path p = new Path("/Users/newbiettn/Dropbox/Git/automatic-link-serivce/output/sparse_vectors/tfidf-vectors/part-r-00000");
		Path outputPath = new Path(
				"/Users/newbiettn/Dropbox/Git/automatic-link-serivce/output/clustering/sparse_vectors_by_query/tfidf-vectors/part-r-00000");
		SequenceFile.Reader reader = new SequenceFile.Reader(fs, p, conf);
		SequenceFile.Writer writer = new SequenceFile.Writer(fs, conf,
				outputPath, Text.class, VectorWritable.class);
		Text key = new Text();
		VectorWritable val = new VectorWritable();
		List<String> copiedList = new ArrayList<String>(sample);
		while (reader.next(key, val)) {
			String k = key.toString();
			if (copiedList.contains(k)) {
				writer.append(key, val);
				copiedList.remove(k);
			}
			
			if (copiedList.size() == 0) {
				break;
			}
		}
		writer.close();
		reader.close();
	}
}
