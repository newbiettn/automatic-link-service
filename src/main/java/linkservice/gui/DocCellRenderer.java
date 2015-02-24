package linkservice.gui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import linkservice.document.MyDocument;
import linkservice.document.MyDocument.MyDocumentType;

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
		String filename = value.getFileName();
		String fragment = value.getFragment();
		String uri = value.getUri();
		MyDocumentType mimeType = value.getMimeType();
		String filetype = mimeType.toString();
		
		//text
		String str = "<html>";
		str += "<p style=\" max-width: 50px; \">";
		str += "<span style=\"font-size: 9px; \">" + filename + "</span>" ;
		str += "<br>";
		str += "<span style=\"color: #424242; \">" + fragment + "</span>";
		str += "<br>";
		str += "<span style=\"color: #088A68; \">" + uri + "</span>";
		str += "<br>";
		str += "</p>";
		setText(str);
		
		//set icon
		String iconPath = String.format("src/main/resources/linkservice/gui/icons/%s.png", filetype);
		setIcon(new ImageIcon(iconPath));
		
		//background color
		if (isSelected) {
			setBackground(Color.decode("#5858FA"));
			setForeground(list.getSelectionForeground());
		} else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}
		setEnabled(list.isEnabled());
		setFont(list.getFont());
		list.setFixedCellHeight(-1);
		list.setFixedCellWidth(-1);
		list.setBorder(new EmptyBorder(2,2, 2, 2));
		list.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0,Color.BLACK));
		return this;
	}
}
