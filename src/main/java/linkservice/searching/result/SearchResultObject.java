package linkservice.searching.result;

import linkservice.document.MyDocument;

/**
 * A single search result that will be returned by the searcher.
 * 
 * @author newbiettn
 *
 */
public class SearchResultObject {
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
	
}
