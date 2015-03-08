package linkservice.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class SearchListener implements ActionListener{

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
	
//	private Searcher searcher;
//	private LinkServiceGetPropertyValues myDocumentIndexedProp;
//	private JList<MyDocument> resultList;
//	private JTextField queryTextField;
//
//	public SearchListener(JList<MyDocument> aList, JTextField aQueryTextField) throws IOException, InvalidTokenOffsetsException {
//		myDocumentIndexedProp = new LinkServiceGetPropertyValues(GeneralConfigPath.PROPERTIES_PATH);
//		String index_dir = myDocumentIndexedProp.getProperty("linkservice.index_dir");
//		searcher = new Searcher(index_dir);
//		this.resultList = aList;
//		this.queryTextField = aQueryTextField;
//	}
//
//	public void actionPerformed(ActionEvent e) {
////		try {
////			List<MyDocument> searchResult = searcher.search(queryTextField.getText());
////			DefaultListModel<MyDocument> model = new DefaultListModel<MyDocument>();			
////			for (MyDocument singleDoc : searchResult) {
////				model.addElement(singleDoc);
////			}
////			resultList.setModel(model);
////			
////		} catch (IOException e1) {
////			e1.printStackTrace();
////		} catch (InvalidTokenOffsetsException e1) {
////			e1.printStackTrace();
////		} 
//	}

}
