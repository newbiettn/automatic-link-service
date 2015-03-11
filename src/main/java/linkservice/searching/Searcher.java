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
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
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
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
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

	public Searcher(String anIndexFileDir) throws IOException {
		this.indexFileDir = anIndexFileDir;
		this.myCustomAnalyzer = new MyCustomAnalyzer();
		Directory directory = FSDirectory.open(new File(this.indexFileDir));
		this.indexReader = DirectoryReader.open(directory);
		this.indexSearcher = new IndexSearcher(this.indexReader);
		this.multiFieldQueryParser = new MultiFieldQueryParser(Version.LUCENE_46, new String[] {
				MyDocumentIndexedProperties.CONTENT_FIELD_NO_FILTERING_TYPE, 
				MyDocumentIndexedProperties.FILE_NAME_FIELD,
				MyDocumentIndexedProperties.MIME_TYPE_FIELD
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
		this.queryScorer = new QueryScorer(query, MyDocumentIndexedProperties.CONTENT_FIELD_NO_FILTERING_TYPE);
		Highlighter highlighter = new Highlighter(this.queryScorer);
		highlighter.setTextFragmenter(new SimpleSpanFragmenter(this.queryScorer));
		
		List<SearchResultObject> searchResult = new ArrayList<SearchResultObject>();
		for (ScoreDoc sd : topDocs.scoreDocs) {
			//explain score
//			Explanation explanation = indexSearcher.explain(termQuery, sd.doc);
//			System.out.println("----------");
			
			MyDocument singleDoc = makeDocumentForResult(sd, highlighter);
			searchResult.add(buildSingleSearchResult(singleDoc));
			
			//explain score
//			System.out.println(explanation.toString());
		}
		return searchResult;
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
	public MyDocument makeDocumentForResult(ScoreDoc sd, Highlighter highlighter) throws IOException, InvalidTokenOffsetsException {
		MyDocument myDoc = new MyDocument();
		Document doc = indexSearcher.doc(sd.doc);
		String mimeType = doc.get(MyDocumentIndexedProperties.MIME_TYPE_FIELD);
		String filename = doc.get(MyDocumentIndexedProperties.FILE_NAME_FIELD);
		String id = doc.get(MyDocumentIndexedProperties.ID_FIELD);
		String filepath = doc.get(MyDocumentIndexedProperties.FILE_PATH_FIELD);
		String contents = doc.get(MyDocumentIndexedProperties.CONTENT_FIELD_NO_FILTERING_TYPE);
		TokenStream stream = TokenSources.getAnyTokenStream(
				indexSearcher.getIndexReader(), sd.doc, MyDocumentIndexedProperties.CONTENT_FIELD_NO_FILTERING_TYPE, doc, myCustomAnalyzer);
		String fragment = highlighter.getBestFragment(stream, contents);
		
		myDoc.setFragment(fragment);
		myDoc.setId(id);
		myDoc.setFileName(filename);;
		myDoc.setUri(filepath);
		myDoc.setMimeType(mimeType);
		return myDoc;
	}
	
	/**
	 * Make ResultObject
	 * 
	 * @param myDoc
	 * @return
	 */
	public SearchResultObject buildSingleSearchResult(MyDocument myDoc) {
		SearchResultObject searchResultObj = new SearchResultObject(myDoc);
		return searchResultObj;
	}
	
	
}