package linkservice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import linkservice.clustering.jsonify.OrganizeSearchResultObjectByClusters;
import linkservice.clustering.methods.AbstractClustering;
import linkservice.clustering.methods.ClusteringByFuzzyKMeans;
import linkservice.common.GeneralConfigPath;
import linkservice.common.LinkServiceGetPropertyValues;
import linkservice.common.SequenceFileFromLuceneIndex;
import linkservice.common.UpdatePath;
import linkservice.indexing.Indexer;
import linkservice.searching.Searcher;
import linkservice.searching.result.SearchResultObject;
import linkservice.searching.result.SearchResultObjectByCluster;

import com.owlike.genson.Genson;

public class AutomaticLink {
	
	Logger logger = LoggerFactory.getLogger(AutomaticLink.class);
	LinkServiceGetPropertyValues myDocumentIndexedProp;
	Configuration conf;
	
	String indexDir;
	String dataFileDir;
	
	public AutomaticLink() throws IOException {
		conf = new Configuration();
		myDocumentIndexedProp = new LinkServiceGetPropertyValues(
				GeneralConfigPath.PROPERTIES_PATH);
		indexDir = myDocumentIndexedProp.getProperty("linkservice.index_dir");
		dataFileDir = myDocumentIndexedProp.getProperty("linkservice.data_dir");
	}
	public void initialize() throws Exception {
		String sequenceFileDir = myDocumentIndexedProp.getProperty("linkservice.sequence_dir");
		String sparseVectorsDir = myDocumentIndexedProp.getProperty("linkservice.mahout.sparse_vector_dir");
		
		Indexer indexer = new Indexer(indexDir, dataFileDir);
		SequenceFileFromLuceneIndex lucene2Seq = new SequenceFileFromLuceneIndex(
				indexer, sequenceFileDir);
		
		indexer.runIndex();
		indexer.close();
		
		// convert index files to sequence files
		lucene2Seq.run();
		
		AbstractClustering.generateSparseVectors(sequenceFileDir, sparseVectorsDir);
	}
	
	public void checForNewDocs() throws Exception {
		Indexer indexer = new Indexer(indexDir, dataFileDir);
		indexer.runIndex();
		indexer.close();
	}
	public String run(String query) throws Exception {
		Searcher searcher = new Searcher("/Users/newbiettn/Dropbox/Git/automatic-link-serivce/output/index_files");
		ClusteringByFuzzyKMeans clusteringByFuzzyKMeans = new ClusteringByFuzzyKMeans();
		
		List<SearchResultObject> results = searcher.search(query);
		List<SearchResultObjectByCluster> resultsByClusters = new ArrayList<SearchResultObjectByCluster>();
		
		String json = "";
		if (results.size() > 0) {
			UpdatePath.cleanForNewClustering();
			clusteringByFuzzyKMeans.run(results);
			resultsByClusters = OrganizeSearchResultObjectByClusters
					.run(results);
			Genson genson = new Genson();
			json = genson.serialize(resultsByClusters);
			logger.info(json + "");
		}
		return json;
	}
	
	public static void main(String[] args) throws Exception {
		AutomaticLink al = new AutomaticLink();
		if (UpdatePath.isOutputEmpty()) {
			al.initialize();
		} else {
			al.checForNewDocs();
		}
		al.run("image");
	}
}
