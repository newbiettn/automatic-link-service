package linkservice.document;

import javax.swing.ImageIcon;

/**
 * Document entity, used to represent document item
 * in search result.
 * 
 * @author newbiettn
 *
 */
public class MyDocument {
	public enum FileTypes {
		PDF, DOC, DOCX, TXT, HTML
	}
	
	//document id
	private String id;
	
	//image icon
	private String mimeType;

	//for display highlight in the search result, depends on search keyword
	private String fragment;
	
	//path to document
	private String uri;
	
	//file name of the document
	private String fileName;
	
	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
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
