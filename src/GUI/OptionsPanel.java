package GUI;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import burp.BurpExtender;

public class OptionsPanel extends JPanel {

	private static JTextField nucleiTemplatesPath;

	public static JTextField getNucleiTemplatesPath() {
		return nucleiTemplatesPath;
	}


	public static void setNucleiTemplatesPath(JTextField nucleiTemplatesPath) {
		OptionsPanel.nucleiTemplatesPath = nucleiTemplatesPath;
	}


	OptionsPanel(){

		JLabel lblNewLabel = new JLabel("nuclei-templates directory:");


		nucleiTemplatesPath = new JTextField();
		nucleiTemplatesPath.setColumns(50);
		nucleiTemplatesPath.getDocument().addDocumentListener(new TextFieldListener());
		
		
		JButton buttonCreateFolder = new JButton("Create Folder");
		buttonCreateFolder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
		        String folderPath = nucleiTemplatesPath.getText();
		        File folder = new File(folderPath);
		        if (!folder.exists()) {
		            // 创建文件夹
		            if (folder.mkdirs()) {
		            	saveToConfigFromGUI();
		            }
		        }
			}
		});
		
		JButton btOpenFolder = new JButton("Open Folder");
		btOpenFolder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String folderPath = nucleiTemplatesPath.getText();
					Desktop.getDesktop().open(new File(folderPath));
				} catch (Exception Exception) {
					Exception.printStackTrace(BurpExtender.getStderr());
				}
			}
		});
		
		
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);

		//查找提取类
		int rowIndex = 0;
		int cloumnIndex = 0;

		add(lblNewLabel, new bagLayout(++rowIndex, ++cloumnIndex));
		add(nucleiTemplatesPath, new bagLayout(rowIndex, ++cloumnIndex));
		
		cloumnIndex = 0;
		add(buttonCreateFolder, new bagLayout(++rowIndex, cloumnIndex));
		add(btOpenFolder, new bagLayout(++rowIndex, cloumnIndex));
	}
	
	class bagLayout extends GridBagConstraints {
		/**
		 * 采用普通的行列计数，从1开始
		 *
		 * @param row
		 * @param column
		 */
		bagLayout(int row, int column) {
			this.fill = GridBagConstraints.BOTH;
			this.insets = new Insets(0, 0, 5, 5);
			this.gridx = column - 1;
			this.gridy = row - 1;
		}
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
