package linkservice.gui;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.JLabel;

import linkservice.document.MyDocument;

/**
 * Customize the document display in search result
 * 
 * @author newbiettn
 *
 */
@SuppressWarnings("serial")
public class DocCellRenderer extends JLabel implements
		ListCellRenderer<MyDocument> {

	public DocCellRenderer() {
		setOpaque(true);
	}

	public Component getListCellRendererComponent(
			JList<? extends MyDocument> list, MyDocument value, int index,
			boolean isSelected, boolean cellHasFocus) {
		String str = "<html>";
		str += "<p style=\" max-width: 10px;  \">";
		str += "<span style=\"font-size: 9px; \">" + value.getFileName() + "</span>" ;
		str += "<br>";
		str += "<span style=\"color: #424242; \">" + value.getFragment() + "</span>";
		str += "<br>";
		str += "<span>" + value.getUri() + "</span>";
		str += "<br>";
		str += "<span>" + value.getMimeType() + "</span>";
		str += "</p>";
		
		
		setText(str);
		setIcon(new ImageIcon("src/main/resources/linkservice/gui/icons/pdf.png"));
		if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		} else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}
		setEnabled(list.isEnabled());
		setFont(list.getFont());
		return this;
	}
}
