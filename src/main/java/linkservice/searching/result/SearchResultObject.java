package linkservice.searching.result;

import java.util.List;

import linkservice.document.MyDocument;

/**
 * A single search result that will be returned by the searcher.
 * 
 * @author newbiettn
 *
 */
public class SearchResultObject {
	private List<MyDocument> linkedDocuments;
	
	private MyDocument myDoc;
	
	public SearchResultObject(MyDocument aDoc) {
		this.myDoc = aDoc;
	}

	public MyDocument getMyDoc() {
		return myDoc;
	}

	public void setMyDoc(MyDocument myDoc) {
		this.myDoc = myDoc;
	}

	public List<MyDocument> getLinkedDocuments() {
		return linkedDocuments;
	}

	public void setLinkedDocuments(List<MyDocument> linkedDocuments) {
		this.linkedDocuments = linkedDocuments;
	}
	
}
