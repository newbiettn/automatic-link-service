package linkservice.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

import javax.swing.JTextPane;

import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;

import linkservice.common.GeneralConfigPath;
import linkservice.common.LinkServiceGetPropertyValues;
import linkservice.searching.Searcher;

public class SearchListener implements ActionListener{
	
	private Searcher searcher;
	private LinkServiceGetPropertyValues myDocumentIndexedProp;
	private JTextPane resultTextPane;
	
	public SearchListener(JTextPane resultTextPane) throws IOException, InvalidTokenOffsetsException {
		myDocumentIndexedProp = new LinkServiceGetPropertyValues(GeneralConfigPath.PROPERTIES_PATH);
		String index_dir = myDocumentIndexedProp.getProperty("linkservice.index_dir");
		searcher = new Searcher(index_dir);
	}
	
	public void actionPerformed(ActionEvent e) {
		try {
			List<String> searchResult = searcher.search();
			for (String result : searchResult) {
				resultTextPane.setText("aaa");
				//System.out.println(result);
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InvalidTokenOffsetsException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

}
