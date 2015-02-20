package linkservice.common;

import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Utils for analyzing jobs
 * 
 * @author newbiettn
 *
 */
public class AnalyzerUtils {
	private static Logger logger = LoggerFactory.getLogger(AnalyzerUtils.class);
	
	public static void displayTokens(Analyzer analyzer, String text)
			throws IOException {
		displayTokens(analyzer.tokenStream("contents", new StringReader(text)));
	}

	public static void displayTokens(TokenStream stream) throws IOException {
		CharTermAttribute term = stream.addAttribute(CharTermAttribute.class);
		stream.reset();
		while (stream.incrementToken()) {
			logger.info("[" + term.toString() + "] ");
		}
	}

	public static String tokenStreamToString(TokenStream ts) throws IOException {
		StringBuffer newSummary = new StringBuffer();
		CharTermAttribute term = ts.addAttribute(CharTermAttribute.class);
		ts.reset();
		while (ts.incrementToken()) {
			newSummary.append(term.toString()+" ");
		}
		return newSummary.toString();
	}
}
