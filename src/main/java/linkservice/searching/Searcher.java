package linkservice.searching;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import linkservice.indexing.Indexer;
import linkservice.indexing.MyCustomAnalyzer;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
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

	private TermQuery termQuery;

	private QueryScorer queryScorer;

	private TopDocs topDocs;

	private MyCustomAnalyzer myCustomAnalyzer;

	public Searcher(String anIndexFileDir) throws IOException {
		this.indexFileDir = anIndexFileDir;
		this.myCustomAnalyzer = new MyCustomAnalyzer();
		Directory directory = FSDirectory.open(new File(this.indexFileDir));
		this.indexReader = DirectoryReader.open(directory);
		this.indexSearcher = new IndexSearcher(this.indexReader);
	}

	/**
	 * 
	 * @return
	 * @throws IOException
	 * @throws InvalidTokenOffsetsException
	 */
	public List<String> search() throws IOException, InvalidTokenOffsetsException {
		termQuery = new TermQuery(new Term("contents", "image"));
		topDocs = indexSearcher.search(termQuery, 10);
		this.queryScorer = new QueryScorer(this.termQuery, "contents");
		Highlighter highlighter = new Highlighter(this.queryScorer);
		highlighter.setTextFragmenter(new SimpleSpanFragmenter(this.queryScorer));
		
		List<String> searchResult = new ArrayList<String>();
		for (ScoreDoc sd : topDocs.scoreDocs) {
			String result = getSingleResult(sd, highlighter);
			searchResult.add(result);
		}
		return searchResult;
	}
	
	public String getSingleResult(ScoreDoc sd, Highlighter highlighter) throws IOException, InvalidTokenOffsetsException {
		Document doc = indexSearcher.doc(sd.doc);
		String contents = doc.get("contents");
		TokenStream stream = TokenSources.getAnyTokenStream(
				indexSearcher.getIndexReader(), sd.doc, "contents", doc, myCustomAnalyzer);
		String fragment = highlighter.getBestFragment(stream, contents);
		return fragment;
	}
}