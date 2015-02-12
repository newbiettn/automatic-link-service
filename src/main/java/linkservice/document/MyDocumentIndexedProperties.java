package linkservice.document;

/** To contains properties should be indexed.
 * 
 * The {@link linkservice.index.Indexer} will index the content of documents
 * and distinguish them by their unique ID
 * 
 */

public class MyDocumentIndexedProperties {
	//default indexed field name for document content
	public static final String CONTENT_FIELD = "contents";
	
	//default indexed Id for document
	public static final String ID_FIELD = "id";
}
