package linkservice.gui;

import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
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
	private JPanel homePane;
	private JPanel advancePane;
	private JTextField queryTextField;
	private JButton searchBtn;
//	private JList<MyDocument> resultList;

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
		
		// tab
		JTabbedPane tabbedPane = new JTabbedPane();
		setContentPane(tabbedPane);
		
		// home
		homePane = new JPanel(new MigLayout("", "[grow][]", "[][][grow]"));
		setUpHomePane();
		tabbedPane.addTab("Home", null, homePane, "Does nothing");
		
		// clustering
//		advancePane = new JPanel(new MigLayout("", "[167.00][67.00][grow]", "[][][][][grow]"));
//		tabbedPane.addTab("Advance", null, advancePane, "Does nothing");
//		JLabel clustersLb = new JLabel();
//		clustersLb.setText("Clusters (k)");
//		advancePane.add(clustersLb, "cell 0 0,grow");
//		JTextField clustersTxtField = new JTextField();
//		advancePane.add(clustersTxtField, "cell 1 0,grow");
//		
//		JLabel distanceMeasureLb = new JLabel();
//		distanceMeasureLb.setText("Distance measure");
//		advancePane.add(distanceMeasureLb, "cell 0 1,grow");
//		JTextField distanceMeasureTxtField = new JTextField();
//		advancePane.add(distanceMeasureTxtField, "cell 1 1,grow");
//		
//		JLabel iterationsLb = new JLabel();
//		iterationsLb.setText("Iterations");
//		advancePane.add(iterationsLb, "cell 0 2,grow");
//		JTextField iterationsTxtField = new JTextField();
//		advancePane.add(iterationsTxtField, "cell 1 2,grow");
//		
//		JLabel covergenceLb = new JLabel();
//		covergenceLb.setText("Covergence");
//		advancePane.add(covergenceLb, "cell 0 3,grow");
//		JTextField convergenceTxtField = new JTextField();
//		
//		advancePane.add(convergenceTxtField, "cell 1 3,grow");
//		tabbedPane.addTab("Home", null, homePane, "Does nothing");
//		setUpAdvance();
//		
//		JPanel graph = new KMeanGraphPanel();
//		advancePane.add(graph, "cell 2 0 1 5");
	}
	public void setUpHomePane() throws IOException, InvalidTokenOffsetsException {
		homePane = new JPanel(new MigLayout("", "[grow][]", "[][][grow]"));
		
		// query text field
		queryTextField = new JTextField();
		queryTextField.setColumns(20);
		homePane.add(queryTextField, "cell 0 1,grow");

		// // search button
		searchBtn = new JButton("Search");
		homePane.add(searchBtn, "cell 1 1,grow");

//		resultList = new JList<MyDocument>();
//		DocCellRenderer renderer = new DocCellRenderer();
//		resultList.setCellRenderer(renderer);
//		resultList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
//		resultList.setVisibleRowCount(-1);
//		JScrollPane scrollPane = new JScrollPane(resultList);
//		homePane.add(scrollPane, "cell 0 2,grow");
//
//		// result
//		ActionListener searchBtnListener = new SearchListener(resultList,
//				queryTextField);
//		searchBtn.addActionListener(searchBtnListener);
	}
	

	public void setUpAdvance() {
	}
}
