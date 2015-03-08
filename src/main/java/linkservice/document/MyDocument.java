package linkservice.document;

/**
 * Document entity, used to represent document item
 * in search result.
 * 
 * @author newbiettn
 *
 */
public class MyDocument {
	public enum MyDocumentType {
		PDF, DOC, DOCX, TXT, HTML, RFC;
	}
	
	//document id
	private String id;
	
	private MyDocumentType mimeType;

	//for display highlight in the search result, depends on search keyword
	private String fragment;
	
	//path to document
	private String uri;
	
	//file name of the document
	private String fileName;
	
	public MyDocumentType getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeTypeStr) {
		System.out.println(mimeTypeStr);
		switch(mimeTypeStr) {
			case "application/pdf": 
				mimeType = MyDocumentType.PDF;
				break;
			default:
				mimeType = MyDocumentType.TXT;
				break;
		}
	}
	
	public String getId() {
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

	public void setId(String id) {
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
