package linkservice.indexing;

import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.KStemFilter;
import org.apache.lucene.analysis.miscellaneous.LengthFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.util.Version;

public class MyCustomAnalyzer extends Analyzer {

	public static final int DEFAULT_MAX_TOKEN_LENGTH = 255;

	@Override
	protected TokenStreamComponents createComponents(String fieldName,
			Reader reader) {

		final StandardTokenizer src = new StandardTokenizer(Version.LUCENE_46, reader);
		TokenStream tok = new StandardFilter(Version.LUCENE_46, src);
		//tok = new LengthFilter(Version.LUCENE_46, tok, 3, DEFAULT_MAX_TOKEN_LENGTH); 
		tok = new LowerCaseFilter(Version.LUCENE_46, tok);
		tok = new StopFilter(Version.LUCENE_46, tok,
				MyStopWords.MY_ENGLISH_STOP_WORDS_SET);
		//stopFilter.setEnablePositionIncrements(true);
		tok = new KStemFilter(tok);
		//tok = new LengthFilter(Version.LUCENE_46, tok, 4, DEFAULT_MAX_TOKEN_LENGTH);
		return new TokenStreamComponents(src, tok) {
			@Override
			protected void setReader(final Reader reader) throws IOException {
				src.setMaxTokenLength(DEFAULT_MAX_TOKEN_LENGTH);
				super.setReader(reader);
			}
		};
	}

}
