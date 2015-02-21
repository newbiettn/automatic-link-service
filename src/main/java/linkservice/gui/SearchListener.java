package linkservice.gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;

import linkservice.common.GeneralConfigPath;
import linkservice.common.LinkServiceGetPropertyValues;
import linkservice.searching.Searcher;

public class SearchListener implements ActionListener{
	
	private Searcher searcher;
	private LinkServiceGetPropertyValues myDocumentIndexedProp;
	private JTextPane resultTextPane;
	private JTextField queryTextField;
	
	public SearchListener(JTextPane aTextPane, JTextField aQueryTextField) throws IOException, InvalidTokenOffsetsException {
		myDocumentIndexedProp = new LinkServiceGetPropertyValues(GeneralConfigPath.PROPERTIES_PATH);
		String index_dir = myDocumentIndexedProp.getProperty("linkservice.index_dir");
		searcher = new Searcher(index_dir);
		this.resultTextPane = aTextPane;
		this.queryTextField = aQueryTextField;
	}
	
	public void actionPerformed(ActionEvent e) {
		try {
			List<String> searchResult = searcher.search(queryTextField.getText());
			StyleContext sc = StyleContext.getDefaultStyleContext();
			StyledDocument doc = new DefaultStyledDocument(sc);
			for (String result : searchResult) {
				resultTextPane.setText(result);
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
