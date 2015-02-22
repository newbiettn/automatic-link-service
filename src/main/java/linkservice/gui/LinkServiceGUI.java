package linkservice.gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import javax.swing.JButton;

import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;

import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JList;

import linkservice.document.MyDocument;

public class LinkServiceGUI extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField queryTextField;
	private JButton searchBtn;
	private JList<MyDocument> resultList;

	/**
	 * Launch the application.
	 * 
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LinkServiceGUI frame = new LinkServiceGUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * 
	 * @throws InvalidTokenOffsetsException
	 * @throws IOException
	 */
	public LinkServiceGUI() throws IOException, InvalidTokenOffsetsException {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 895, 586);
		contentPane = new JPanel(new MigLayout("", "[grow][]", "[][grow]"));
		setContentPane(contentPane);

		// query text field
		queryTextField = new JTextField();
		queryTextField.setColumns(20);
		contentPane.add(queryTextField, "cell 0 0,grow");

		// search button
		searchBtn = new JButton("Search");
		contentPane.add(searchBtn, "cell 1 0,grow");
		
		resultList = new JList<MyDocument>();
		DocCellRenderer renderer = new DocCellRenderer();
		resultList.setCellRenderer(renderer);
		resultList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		resultList.setVisibleRowCount(100);
		JScrollPane scrollPane = new JScrollPane(resultList);
		contentPane.add(scrollPane, "cell 0 1,grow" );
		
		//contentPane.add(resultList, "cell 0 1,grow");
		
		
		// result
		ActionListener searchBtnListener = new SearchListener(resultList, queryTextField);
		searchBtn.addActionListener(searchBtnListener);
	}
}
