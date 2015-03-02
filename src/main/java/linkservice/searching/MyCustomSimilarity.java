package linkservice.searching;

import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.search.similarities.TFIDFSimilarity;
import org.apache.lucene.util.BytesRef;

/**
 * Adjust the scoring for document searching by Lucene
 * 
 * @author newbiettn
 *
 */
public class MyCustomSimilarity extends TFIDFSimilarity {

	@Override
	public float coord(int overlap, int maxOverlap) {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public float queryNorm(float sumOfSquaredWeights) {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public float tf(float freq) {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public float idf(long docFreq, long numDocs) {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public float lengthNorm(FieldInvertState state) {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public float decodeNormValue(long norm) {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public long encodeNormValue(float f) {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public float sloppyFreq(int distance) {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public float scorePayload(int doc, int start, int end, BytesRef payload) {
		// TODO Auto-generated method stub
		return 1;
	}

	
}
