package linkservice.hadoop;

import org.apache.commons.io.FilenameUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;

public class HadoopConfig {
	private Configuration conf;
	public static String HADOOP_DIR = "/usr/local/Cellar/hadoop121/1.2.1/";
	public static String CORE_SITE_XML_DIR = "libexec/conf/core-site.xml";
	public static String HDFS_SITE_XML_DIR = "libexec/conf/hdfs-site.xml";
	
	private HadoopConfig() {
		conf = new Configuration();
		conf.addResource(new Path(FilenameUtils.concat(HADOOP_DIR, CORE_SITE_XML_DIR)));
		conf.addResource(new Path(FilenameUtils.concat(HADOOP_DIR, HDFS_SITE_XML_DIR)));
	}
	
	private static class HadoopConfigHolder {
		private static final HadoopConfig INSTANCE = new HadoopConfig();
	}
	
	public static HadoopConfig getInstance() {
		return HadoopConfigHolder.INSTANCE;
	}
	
	public Configuration getConf() {
		return conf;
	}
}
