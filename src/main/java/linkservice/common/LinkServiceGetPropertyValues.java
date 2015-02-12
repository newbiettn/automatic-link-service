package linkservice.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Read the Properties file and get corresponding value for each property.
 * 
 * @author newbiettn
 *
 */
public class LinkServiceGetPropertyValues {
	private Properties prop;
	private String propFileName;
	
	public LinkServiceGetPropertyValues(String propertyFileName) throws IOException {
		this.prop = new Properties();
		this.propFileName = propertyFileName;
		File propFile = new File(propFileName);
		InputStream is = new FileInputStream(propFile);
		prop.load(is);
 
	}
	public String getProperty(String aPropertyName) {
		return prop.getProperty(aPropertyName);
	}
}
