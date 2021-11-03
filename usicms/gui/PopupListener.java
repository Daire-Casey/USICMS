package org.dapeng.usicms.gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JList;
import javax.swing.JPopupMenu;

public class PopupListener extends MouseAdapter {
	private JPopupMenu popup;
	public static int index;

	PopupListener(JPopupMenu popup) {
		this.popup = popup;
		this.index = -1;
	}

	public void mousePressed(MouseEvent evt) {
		maybeShowPopup(evt);
	}

	public void mouseReleased(MouseEvent evt) {
		maybeShowPopup(evt);
	}

	private void maybeShowPopup(MouseEvent evt) {
		if (evt.isPopupTrigger()) {
			JList list = (JList) evt.getSource();
			index = list.locationToIndex(evt.getPoint()); // stores the index of the list where the mouse was
															// right-clicked
			popup.show(evt.getComponent(), evt.getX(), evt.getY());
		}
	}
}