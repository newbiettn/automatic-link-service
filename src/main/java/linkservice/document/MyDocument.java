package linkservice.document;

/**
 * Document entity, used to represent document item
 * in search result.
 * 
 * @author newbiettn
 *
 */
public class MyDocument {
	//document id
	private int id;

	//for display highlight in the search result, depends on search keyword
	private String fragment;
	
	//path to document
	private String uri;
	
	//file name of the document
	private String fileName;
	
	public int getId() {
		return id;
	}

	public String getFragment() {
		return fragment;
	}

	public String getUri() {
		return uri;
	}

	public String getFileName() {
		return fileName;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setFragment(String fragment) {
		this.fragment = fragment;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
