package linkservice.common;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import org.apache.hadoop.fs.Path;
import org.apache.mahout.utils.clustering.ClusterDumper;

/**
 * Dump the content of the cluster output
 * 
 * @author newbiettn
 *
 */
public class ClusterOutput {

	// directory containing sequence files
	private String seq_file_dir;

	// directory containing clustered points
	private String clustered_points_dir;

	// output file
	private String output_file;

	// dictionary file
	private String dict_file;

	// clusterdumper
	private ClusterDumper clusterDumper;
	
	/**
	 * Create a ClusterOuput.
	 * 
	 * @param aSequenceFileDir
	 * @param aClusteredPointsDir
	 * @param anOutputFile
	 * @param aDictFile
	 */
	public ClusterOutput(String aSequenceFileDir, String aClusteredPointsDir,
			String anOutputFile, String aDictFile) {
		this.seq_file_dir = aSequenceFileDir;
		this.clustered_points_dir = aClusteredPointsDir;
		this.output_file = anOutputFile;
		this.dict_file = aDictFile;
		this.clusterDumper = new ClusterDumper(new Path(this.seq_file_dir),
				new Path(this.clustered_points_dir));
	}

	public String getTermDictionary() {
		return clusterDumper.getTermDictionary();
	}
	
	/**
	 * Print cluster dumper to screen.
	 * 
	 * @throws Exception
	 */
	public void printClusters() throws Exception {
		clusterDumper.printClusters(null);
	}
	
	/**
	 * Empty output file.
	 * 
	 * @throws FileNotFoundException
	 */
	public void emptyOuputFile() throws FileNotFoundException {
		PrintWriter pw = new PrintWriter(this.output_file);
		pw.close();
	}
	
	/**
	 * Run cluster dumper with provided arguments.
	 * 
	 * @throws Exception
	 */
	public void run() throws Exception {
		String[] para = {
				// dictionary type: sequencefile
				"-dt", "sequencefile",
				// input: directory of sequence file
				"-i", this.seq_file_dir,
				// dictionary file
				"-d", this.dict_file,
				// clustered point directory
				"-p", this.clustered_points_dir,
				// number of term to print
				"-n", "10",
				// output
				"-of", "TEXT", "-o", this.output_file,
				"-e"
				};
		clusterDumper.run(para);
	}
}
