package linkservice.rs;

import java.io.File;
import java.io.IOException;

public class Rename {
	public static void main(String[] args) throws IOException {
		String root = "/Users/newbiettn/IDE/Test Data/Test_data/";
		File dir = new File(root);
		File[] directoryListing = dir.listFiles();
		for (File child: directoryListing) {
			String name = child.getName();
			int pos = name.indexOf("for");
			if (pos > 0) {
				String keep = name.substring(0, name.indexOf("for")-1);
				File file2 = new File(root + keep + ".pdf");
				if (file2.exists()) {
					System.out.println(file2.getName());
					throw new java.io.IOException("file exists");
				}
					

				boolean success = child.renameTo(file2);
				if (!success) {
					System.out.println("Done!!");
				}
			}
			
		}
		
	}
}
