package linkservice.gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import javax.swing.JButton;
import javax.swing.JTextPane;

import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;

import java.awt.event.ActionListener;
import java.io.IOException;

public class LinkServiceGUI extends JFrame {

	private JPanel contentPane;
	private JTextField queryTextField;
	private JButton searchBtn;
	private JTextPane resultTextPane;
	
	/**
	 * Launch the application.
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
	 * @throws InvalidTokenOffsetsException 
	 * @throws IOException 
	 */
	public LinkServiceGUI() throws IOException, InvalidTokenOffsetsException  {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 524, 341);
		contentPane = new JPanel(new MigLayout("", "[grow][]", "[][grow]"));
		setContentPane(contentPane);
		
		//query text field
		queryTextField = new JTextField();
		queryTextField.setColumns(20);
		contentPane.add(queryTextField, "cell 0 0,grow");
		
		//search button
		searchBtn = new JButton("Search");
		contentPane.add(searchBtn, "cell 1 0,grow");
		
		//result
		resultTextPane = new JTextPane();
		ActionListener searchBtnListener = new SearchListener(resultTextPane);
		searchBtn.addActionListener(searchBtnListener);
		contentPane.add(resultTextPane, "cell 0 1,grow");
		
	}
}
