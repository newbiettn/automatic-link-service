package linkservice.common.hadoop;

import org.apache.commons.io.FilenameUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;

public class HadoopConfig {
	private Configuration conf;
	public static String HADOOP_DIR = "/usr/local/hadoop-1.2.1/";
	public static String CORE_SITE_XML_DIR = "conf/core-site.xml";
	public static String HDFS_SITE_XML_DIR = "conf/hdfs-site.xml";
	public static String AUTOMATIC_INDEX_HDFS_DIR = "automatic";
	
	public HadoopConfig() {
		conf = new Configuration();
		//conf.addResource(new Path(FilenameUtils.concat(HADOOP_DIR, CORE_SITE_XML_DIR)));
		//conf.addResource(new Path(FilenameUtils.concat(HADOOP_DIR, HDFS_SITE_XML_DIR)));
	}
	
	public Configuration getConf() {
		return conf;
	}
}
