package GUI;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class OptionsPanel extends JPanel {

	private static JTextField nucleiTemplatesPath;

	public static JTextField getNucleiTemplatesPath() {
		return nucleiTemplatesPath;
	}


	public static void setNucleiTemplatesPath(JTextField nucleiTemplatesPath) {
		OptionsPanel.nucleiTemplatesPath = nucleiTemplatesPath;
	}


	OptionsPanel(){
		GridBagLayout gbl_fourFourthPanel = new GridBagLayout();
		gbl_fourFourthPanel.columnWidths = new int[]{215, 215, 0};
		gbl_fourFourthPanel.rowHeights = new int[]{27, 0, 0, 0, 27, 0, 0, 0, 0, 0, 27, 27, 27, 27, 0, 0, 0, 0};
		gbl_fourFourthPanel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_fourFourthPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gbl_fourFourthPanel);

		JLabel lblNewLabel = new JLabel("nuclei-templates directory:");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.fill = GridBagConstraints.BOTH;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		add(lblNewLabel, gbc_lblNewLabel);

		nucleiTemplatesPath = new JTextField();
		GridBagConstraints gbc_BrowserPath = new GridBagConstraints();
		gbc_BrowserPath.fill = GridBagConstraints.BOTH;
		gbc_BrowserPath.insets = new Insets(0, 0, 5, 0);
		gbc_BrowserPath.gridx = 1;
		gbc_BrowserPath.gridy = 0;
		add(nucleiTemplatesPath, gbc_BrowserPath);
		nucleiTemplatesPath.setColumns(50);
		nucleiTemplatesPath.getDocument().addDocumentListener(new TextFieldListener());
	}


	public static void saveToConfigFromGUI() {
		String pocDir = nucleiTemplatesPath.getText();
		if (pocDir != null && new File(pocDir).exists()) {
			MainGUI.getGlobalConfig().setPoctRootPath(pocDir);
			MainGUI.getGlobalConfig().saveToDisk();
			nucleiTemplatesPath.setForeground(new Color(51, 51, 51));
		}else {
			nucleiTemplatesPath.setForeground(Color.RED);
		}
	}
}
