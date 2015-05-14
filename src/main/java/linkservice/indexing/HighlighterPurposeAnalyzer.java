package linkservice.indexing;

import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Analyzer.TokenStreamComponents;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.en.KStemFilter;
import org.apache.lucene.analysis.miscellaneous.LengthFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.util.Version;

public class HighlighterPurposeAnalyzer extends Analyzer {

	public static final int DEFAULT_MAX_TOKEN_LENGTH = 255;

	@Override
	protected TokenStreamComponents createComponents(String fieldName,
			Reader reader) {

		final Tokenizer src = new WhitespaceTokenizer(Version.LUCENE_46, reader);
		TokenStream tok = new LengthFilter(Version.LUCENE_46, src, 1, DEFAULT_MAX_TOKEN_LENGTH);
		return new TokenStreamComponents(src, tok);
	}

}
