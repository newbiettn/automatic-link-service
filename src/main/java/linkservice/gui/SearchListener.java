package linkservice.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;

import linkservice.common.GeneralConfigPath;
import linkservice.common.LinkServiceGetPropertyValues;
import linkservice.document.MyDocument;
import linkservice.searching.Searcher;

public class SearchListener implements ActionListener{
	
	private Searcher searcher;
	private LinkServiceGetPropertyValues myDocumentIndexedProp;
	private JList<MyDocument> resultList;
	private JTextField queryTextField;

	public SearchListener(JList<MyDocument> aList, JTextField aQueryTextField) throws IOException, InvalidTokenOffsetsException {
		myDocumentIndexedProp = new LinkServiceGetPropertyValues(GeneralConfigPath.PROPERTIES_PATH);
		String index_dir = myDocumentIndexedProp.getProperty("linkservice.index_dir");
		searcher = new Searcher(index_dir);
		this.resultList = aList;
		this.queryTextField = aQueryTextField;
	}

	public void actionPerformed(ActionEvent e) {
		try {
			List<MyDocument> searchResult = searcher.search(queryTextField.getText());
			DefaultListModel<MyDocument> model = new DefaultListModel<MyDocument>();			
			for (MyDocument singleDoc : searchResult) {
				model.addElement(singleDoc);
			}
			resultList.setModel(model);
			
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (InvalidTokenOffsetsException e1) {
			e1.printStackTrace();
		} 
	}

}
