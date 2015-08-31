package linkservice.searching;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import linkservice.document.MyDocument;
import linkservice.document.MyDocumentIndexedProperties;
import linkservice.indexing.Indexer;
import linkservice.indexing.MyCustomAnalyzer;
import linkservice.searching.result.SearchResultObject;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.search.highlight.TokenSources;
import org.apache.lucene.search.vectorhighlight.BaseFragmentsBuilder;
import org.apache.lucene.search.vectorhighlight.FastVectorHighlighter;
import org.apache.lucene.search.vectorhighlight.FieldQuery;
import org.apache.lucene.search.vectorhighlight.FragListBuilder;
import org.apache.lucene.search.vectorhighlight.FragmentsBuilder;
import org.apache.lucene.search.vectorhighlight.ScoreOrderFragmentsBuilder;
import org.apache.lucene.search.vectorhighlight.SimpleFragListBuilder;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Perform the search
 * 
 * @author newbiettn
 *
 */
public class Searcher {
	// log4j
	Logger logger = LoggerFactory.getLogger(Indexer.class);
	
	// directory containing index files
	private String indexFileDir;

	private IndexReader indexReader;

	private IndexSearcher indexSearcher;

	private QueryScorer queryScorer;

	private TopDocs topDocs;

	private MyCustomAnalyzer myCustomAnalyzer;
	
	private MultiFieldQueryParser multiFieldQueryParser;
	
	private List<SearchResultObject> relatedResult;

	public Searcher(String anIndexFileDir) throws IOException {
		relatedResult = new ArrayList<SearchResultObject>();
		this.indexFileDir = anIndexFileDir;
		this.myCustomAnalyzer = new MyCustomAnalyzer();
		Directory directory = FSDirectory.open(new File(this.indexFileDir));
		this.indexReader = DirectoryReader.open(directory);
		this.indexSearcher = new IndexSearcher(this.indexReader);
		this.multiFieldQueryParser = new MultiFieldQueryParser(Version.LUCENE_46, new String[] {
				MyDocumentIndexedProperties.CONTENT_FIELD_NO_FILTERING_TYPE, 
				MyDocumentIndexedProperties.FILE_NAME_FIELD,
				MyDocumentIndexedProperties.MIME_TYPE_FIELD,
				MyDocumentIndexedProperties.AUTHOR_FIELD
				},
				myCustomAnalyzer);
	}

	/**
	 * 
	 * @return
	 * @throws IOException
	 * @throws InvalidTokenOffsetsException
	 * @throws ParseException 
	 */
	public List<SearchResultObject> search(String queryStr) throws IOException, InvalidTokenOffsetsException, ParseException {
		Query query = multiFieldQueryParser.parse(queryStr);
		//termQuery = new TermQuery(new Term(MyDocumentIndexedProperties.CONTENT_FIELD, keyword));
		topDocs = indexSearcher.search(query, 100);
		System.out.println(topDocs.totalHits);
//		this.queryScorer = new QueryScorer(query, MyDocumentIndexedProperties.CONTENT_FIELD_NO_FILTERING_TYPE);
		//Highlighter highlighter = new Highlighter(this.queryScorer);
		FastVectorHighlighter highlighter = getHighlighter();
		FieldQuery fieldQuery = highlighter.getFieldQuery(query);
		//highlighter.setTextFragmenter(new SimpleSpanFragmenter(this.queryScorer));
		
		List<SearchResultObject> searchResult = new ArrayList<SearchResultObject>();
		for (ScoreDoc sd : topDocs.scoreDocs) {
			MyDocument singleDoc = makeDocumentForResult(sd, highlighter, fieldQuery);
			
			SearchResultObject searchObj = buildSingleSearchResult(singleDoc);
			searchResult.add(searchObj);
		}
//		for (SearchResultObject r : relatedResult) {
//			for (SearchResultObject sr : searchResult) {
//				if (sr.getMyDoc().getFileName() != r.getMyDoc().getFileName()) {
//					relatedResult.add(r);
//				}
//			}
//			
//		}
		return searchResult;
	}
	FastVectorHighlighter getHighlighter() {
		FragListBuilder fragListBuilder = new SimpleFragListBuilder();
		FragmentsBuilder fragmentBuilder = new ScoreOrderFragmentsBuilder(
				BaseFragmentsBuilder.COLORED_PRE_TAGS,
				BaseFragmentsBuilder.COLORED_POST_TAGS);
		return new FastVectorHighlighter(true, true,
				fragListBuilder, fragmentBuilder);
	}
	
	/**
	 * Return single document entity for search result.
	 * The entity includes document filename, id, text fragment (for highlighting)
	 * 
	 * @param sd
	 * @param highlighter
	 * @return
	 * @throws IOException
	 * @throws InvalidTokenOffsetsException
	 */
	public MyDocument makeDocumentForResult(ScoreDoc sd, FastVectorHighlighter highlighter, FieldQuery fieldQuery) throws IOException, InvalidTokenOffsetsException {
		MyDocument myDoc = new MyDocument();
		Document doc = indexSearcher.doc(sd.doc);
		
		System.out.println(doc.get(MyDocumentIndexedProperties.FILE_PATH_FIELD));
		String mimeType = doc.get(MyDocumentIndexedProperties.MIME_TYPE_FIELD);
		String filename = doc.get(MyDocumentIndexedProperties.FILE_NAME_FIELD);
		String id = doc.get(MyDocumentIndexedProperties.ID_FIELD);
		String filepath = doc.get(MyDocumentIndexedProperties.FILE_PATH_FIELD);
		String author = doc.get(MyDocumentIndexedProperties.AUTHOR_FIELD);
		String title = doc.get(MyDocumentIndexedProperties.TITLE_FIELD);
		String keywords = doc.get(MyDocumentIndexedProperties.KEYWORD_FIELD);
		String description = doc.get(MyDocumentIndexedProperties.DESCRIPTION_FIELD);

		//String contents = doc.get(MyDocumentIndexedProperties.CONTENT_FIELD_NO_FILTERING_TYPE);
//		TokenStream stream = TokenSources.getAnyTokenStream(
//				indexSearcher.getIndexReader(), sd.doc, MyDocumentIndexedProperties.CONTENT_FIELD_NO_FILTERING_TYPE, doc, myCustomAnalyzer);
		//String fragment = highlighter.getBestFragment(stream, contents);
		//String snippet = highlighter.getBestFragment(fieldQuery, searcher.getIndexReader(), scoreDoc.doc, F, 100 );

		String fragment = highlighter.getBestFragment(
			      fieldQuery, indexSearcher.getIndexReader(),
			      sd.doc, MyDocumentIndexedProperties.CONTENT_FIELD_NO_FILTERING_TYPE, 500 );
		myDoc.setFragment(fragment);
		myDoc.setId(id);
		myDoc.setFileName(filename);;
		myDoc.setUri(filepath);
		myDoc.setMimeType(mimeType);
		myDoc.setAuthor(author);
		myDoc.setTitle(title);
		myDoc.setKeywords(keywords);
		myDoc.setDescription(description);
		
		return myDoc;
	}
	
	/**
	 * Make ResultObject
	 * 
	 * @param myDoc
	 * @return
	 * @throws ParseException 
	 * @throws InvalidTokenOffsetsException 
	 * @throws IOException 
	 */
	public SearchResultObject buildSingleSearchResult(MyDocument myDoc) throws IOException, InvalidTokenOffsetsException, ParseException {
		SearchResultObject searchResultObj = new SearchResultObject(myDoc);
		List<MyDocument> linkedDocs = getLinkedDocument(myDoc);
		searchResultObj.setLinkedDocuments(linkedDocs);
		return searchResultObj;
	}
	
	public List<MyDocument> getLinkedDocument(MyDocument myDoc) throws IOException, InvalidTokenOffsetsException, ParseException {
		List<MyDocument> linkedDocs = new ArrayList<MyDocument>();
		Document[] docsLikeThis = docsLike(myDoc.getId(), 1);
		for (Document likeThisDoc : docsLikeThis) {
			MyDocument tempDoc = new MyDocument();
			String mimeType = likeThisDoc.get(MyDocumentIndexedProperties.MIME_TYPE_FIELD);
			String filename = likeThisDoc.get(MyDocumentIndexedProperties.FILE_NAME_FIELD);
			String id = likeThisDoc.get(MyDocumentIndexedProperties.ID_FIELD);
			String filepath = likeThisDoc.get(MyDocumentIndexedProperties.FILE_PATH_FIELD);
			String author = likeThisDoc.get(MyDocumentIndexedProperties.AUTHOR_FIELD);
			String title = likeThisDoc.get(MyDocumentIndexedProperties.TITLE_FIELD);
			String keywords = likeThisDoc.get(MyDocumentIndexedProperties.KEYWORD_FIELD);
			String description = likeThisDoc.get(MyDocumentIndexedProperties.DESCRIPTION_FIELD);
			
			tempDoc.setId(id);
			tempDoc.setFileName(filename);;
			tempDoc.setUri(filepath);
			tempDoc.setMimeType(mimeType);
			tempDoc.setAuthor(author);
			tempDoc.setTitle(title);
			tempDoc.setKeywords(keywords);
			tempDoc.setDescription(description);
			
			linkedDocs.add(tempDoc);
//			for (SearchResultObject s : relatedResult) {
//				if (s.getMyDoc().getFileName() != filename) {
//					relatedResult.add(new SearchResultObject(tempDoc));
//				}
//			}
		}
		return linkedDocs;
	}
	
//	public List<MyDocument> generateRandomLinkedDocuments(MyDocument myDoc) throws IOException, InvalidTokenOffsetsException, ParseException {
//		List<MyDocument> linkedDocs = new ArrayList<MyDocument>();
//		String filename = myDoc.getFileName();
//		int fileInt = Integer.parseInt(filename);
//		List<SearchResultObject> searchResult = search("filename:" + fileInt/10 + "*");
//		for (SearchResultObject searchResultObject : searchResult) {
//			MyDocument doc = searchResultObject.getMyDoc();
//			linkedDocs.add(doc);
//		}
//		return linkedDocs;
//	}
	
	public Document[] docsLike(String id, int max) throws IOException, ParseException {
		Query idQuery = new TermQuery(new Term("id", id));
	    TopDocs topDocs = indexSearcher.search(idQuery, 1);
	    int docId = topDocs.scoreDocs[0].doc;
	    Document doc = indexSearcher.doc(docId);
	    
	    Terms terms = indexReader.getTermVector(docId, "filename");
	    BooleanQuery filenameQuery = new BooleanQuery();
	    TermsEnum termsEnum = terms.iterator(null);
	    BytesRef text = null;
	    while ((text = termsEnum.next()) != null) {
	    	String term = text.utf8ToString();
	    	TermQuery tq = new TermQuery(new Term("filename", term));
	    	filenameQuery.add(tq, BooleanClause.Occur.SHOULD);
	    	System.out.println(term);
	    }
	    
	    BooleanQuery likeThisQuery = new BooleanQuery();
	    likeThisQuery.add(filenameQuery, BooleanClause.Occur.SHOULD);

	    likeThisQuery.add(new TermQuery(
	        new Term("id", doc.get("id"))), BooleanClause.Occur.MUST_NOT);
//	    likeThisQuery.add(listIdQuery, BooleanClause.Occur.SHOULD);

	    TopDocs hits = indexSearcher.search(likeThisQuery, 10);

	    int size = max;
	    if (max > hits.scoreDocs.length) size = hits.scoreDocs.length;

	    Document[] docs = new Document[size];
	    for (int i = 0; i < size; i++) {
	    	docs[i] = indexReader.document(hits.scoreDocs[i].doc);
	    }
	    return docs;
	}
	
}