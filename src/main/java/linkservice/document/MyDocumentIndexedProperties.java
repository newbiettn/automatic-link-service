package linkservice.document;

/** To contains properties should be indexed.
 * 
 * The {@link linkservice.indexing.Indexer} will index the content of documents
 * and distinguish them by their unique ID
 * 
 */

public class MyDocumentIndexedProperties {
	//default indexed field name for document content
	public static final String CONTENT_FIELD = "contents";
	
	//default indexed Id for document
	public static final String ID_FIELD = "id";
	
	//default index field name for name of document
	public static final String FILE_NAME_FIELD = "filename";
	
	//default index field name for path of document
	public static final String FILE_PATH_FIELD = "filepath";
	
	//default index field name for path of document
	public static final String MIME_TYPE_FIELD = "mimetype";
}
