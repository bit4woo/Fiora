package GUI;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * 保存各个路径设置参数，自动保存的listener
 */

public class TextFieldListener implements DocumentListener {

	@Override
	public void removeUpdate(DocumentEvent e) {
//		if (listenerIsOn) {
//			saveToConfigFromGUI();
//		}
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
//		if (listenerIsOn) {
//		saveToConfigFromGUI();
//	}
	}

	@Override
	public void changedUpdate(DocumentEvent arg0) {
//		if (listenerIsOn) {
//		saveToConfigFromGUI();
//	}
	}
}