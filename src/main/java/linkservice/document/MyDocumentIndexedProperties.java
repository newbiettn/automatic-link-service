package linkservice.document;

import org.apache.tika.metadata.TikaCoreProperties;

/**
 * To contains properties should be indexed.
 * 
 * The {@link linkservice.indexing.Indexer} will index the content of documents
 * and distinguish them by their unique ID
 * 
 */

public class MyDocumentIndexedProperties {
	// default indexed field name for document content
	public static final String CONTENT_FIELD = "contents";
	
	// default indexed field name for document content without filtering
	public static final String CONTENT_FIELD_NO_FILTERING_TYPE = "contents_no_filtering";

	// default indexed Id for document
	public static final String ID_FIELD = "id";

	// default index field name for name of document
	public static final String FILE_NAME_FIELD = "filename";

	// default index field name for filepath of document
	public static final String FILE_PATH_FIELD = "filepath";

	// default index field name for mime type of document
	public static final String MIME_TYPE_FIELD = "mimetype";
	
	// default index field name for title of document
	public static final String TITLE_FIELD = TikaCoreProperties.TITLE.getName();
	
	// default index field name for author of document
	public static final String AUTHOR_FIELD = TikaCoreProperties.CREATOR.getName();
	
	// default index field name for keywords of document
	public static final String KEYWORD_FIELD = TikaCoreProperties.KEYWORDS.getName();
	
	// default index field name for description of document
	public static final String DESCRIPTION_FIELD = TikaCoreProperties.DESCRIPTION.getName();
	
}
