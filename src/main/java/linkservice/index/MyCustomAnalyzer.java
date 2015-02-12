package linkservice.index;

import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.AnalyzerWrapper;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.TypeTokenFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;

import org.apache.lucene.util.Version;

/**
 * Define customized Analyzer which is baesed on StandardAnalyzer.
 * 
 * This Analyzer supports stemming by using PorterStemFilter.
 * 
 * @author newbiettn
 *
 */
public class MyCustomAnalyzer extends AnalyzerWrapper {
	private Analyzer baseAnalyzer;

	public MyCustomAnalyzer(Analyzer aBaseAnalyzer) {
		this.baseAnalyzer = aBaseAnalyzer;
	}

	@Override
	protected Analyzer getWrappedAnalyzer(String fieldName) {
		return baseAnalyzer;
	}

	@Override
	public void close() {
		baseAnalyzer.close();
		super.close();
	}

	@Override
	protected TokenStreamComponents wrapComponents(String fieldName, TokenStreamComponents components) {
		TokenStream ts = components.getTokenStream();
		Set<String> filteredTypes = new HashSet<String>();
		filteredTypes.add("<NUM>");
		TypeTokenFilter numberFilter = new TypeTokenFilter(Version.LUCENE_46, ts, filteredTypes);
		
		//use PorterStem to stem words
		PorterStemFilter porterStem = new PorterStemFilter(numberFilter);
		return new TokenStreamComponents(components.getTokenizer(), porterStem);
	}

//	public static void main(String[] args) throws IOException {
//
//		// Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_46);
//		MyCustomAnalyzer analyzer = new MyCustomAnalyzer(new StandardAnalyzer(Version.LUCENE_46));
//		String text = "This is a testing example . It should tests the Porter stemmer version 111";
//
//		TokenStream ts = analyzer.tokenStream("fieldName", new StringReader(text));
//		ts.reset();
//
//		while (ts.incrementToken()) {
//			CharTermAttribute ca = ts.getAttribute(CharTermAttribute.class);
//			System.out.println(ca.toString());
//		}
//		analyzer.close();
//	}
}
