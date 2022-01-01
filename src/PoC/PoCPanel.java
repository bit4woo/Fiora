package PoC;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;

import GUI.MainGUI;
import PoC.search.SearchTextField;
import PoCParser.NucleiParser;
import PoCParser.PoctParser;
import burp.BurpExtender;
import burp.Commons;

public class PoCPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel buttonPanel;
	private static LineTable titleTable;

	//add table and tablemodel to GUI
	private static LineTableModel titleTableModel = new LineTableModel();
	PrintWriter stdout;
	PrintWriter stderr;
	private IndexedLinkedHashMap<String,LineEntry> BackupLineEntries;

	private static JTextField textFieldSearch;
	public static JRadioButton rdbtnUseRobotInput;
	public static JLabel lblStatus;
	public static JButton buttonFresh;

	public static JTextField getTextFieldSearch() {
		return textFieldSearch;
	}

	/*
	public static void setTextFieldSearch(JTextField textFieldSearch) {
		TitlePanel.textFieldSearch = textFieldSearch;
	}*/

	public static LineTable getTitleTable() {
		return titleTable;
	}

	public static LineTableModel getTitleTableModel() {
		return titleTableModel;
	}

	public IndexedLinkedHashMap<String,LineEntry> getBackupLineEntries() {
		return BackupLineEntries;
	}

	public PoCPanel() {//构造函数

		try{
			stdout = new PrintWriter(BurpExtender.getCallbacks().getStdout(), true);
			stderr = new PrintWriter(BurpExtender.getCallbacks().getStderr(), true);
		}catch (Exception e){
			stdout = new PrintWriter(System.out, true);
			stderr = new PrintWriter(System.out, true);
		}

		this.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.setLayout(new BorderLayout(0, 0));
		this.add(createButtonPanel(), BorderLayout.NORTH);



		titleTable = new LineTable(titleTableModel);
		this.add(titleTable.getTableAndDetailSplitPane(),BorderLayout.CENTER);
		//LoadData(MainGUI.poctRootPath+File.separator+"script");
		LoadData(MainGUI.poctRootPath);
	}

	/**
	 * 扫描PoC-T中的poc脚本。D:\github\POC-T\script
	 * @param dir
	 * @return
	 */
	public IndexedLinkedHashMap<String,LineEntry> scanPoCFiles(String dir) {
		IndexedLinkedHashMap<String,LineEntry> lineEntries = new IndexedLinkedHashMap<String,LineEntry>();
		if (null==dir || !new File(dir).exists()){
			return lineEntries;
		}
		Collection<File> files = FileUtils.listFiles(new File(dir), FileFilterUtils.suffixFileFilter(".py"), DirectoryFileFilter.INSTANCE);
		for (File file:files) {
			//System.out.println(file.toString());
			if (file.exists() && file.isFile() && !file.getName().startsWith("__")) {
				LineEntry entry = PoctParser.Parser(file.toString());
				lineEntries.put(file.toString(), entry);
			}
		}
		return lineEntries;
	}
	/**
	 * 默认路径 /Users/bit4woo
	 * @param dir
	 * @return
	 */
	public IndexedLinkedHashMap<String,LineEntry> scanUncleiPoCs(String dir) {
		IndexedLinkedHashMap<String,LineEntry> lineEntries = new IndexedLinkedHashMap<String,LineEntry>();
		if (null==dir || !new File(dir).exists()){
			return lineEntries;
		}
		Collection<File> files = FileUtils.listFiles(new File(dir), FileFilterUtils.suffixFileFilter(".yaml"), DirectoryFileFilter.INSTANCE);
		for (File file:files) {
			//System.out.println(file.toString());
			if (file.exists() && file.isFile()) {
				LineEntry entry = NucleiParser.Parser(file.toString());
				if (entry == null){
					continue;
				}
				lineEntries.put(file.toString(), entry);
			}
		}
		return lineEntries;
	}


	public boolean LoadData(String dir){
		try {//这其中的异常会导致burp退出
			System.out.println("=================================");
			System.out.println("==Start Loading Data From: " + dir+"==");
			stdout.println("==Start Loading Data From: " + dir+"==");

			//showToPoCPanel(scanPoCFiles(dir));
			showToPoCPanel(scanUncleiPoCs(dir));

			System.out.println("==End Loading Data From: "+ dir +"==");//输出到debug console
			stdout.println("==End Loading Data From: "+ dir +"==");
			return true;
		} catch (Exception e) {
			stdout.println("Loading Failed!");
			e.printStackTrace();//输出到debug console
			e.printStackTrace(stderr);
			return false;
		}
	}
	
	public static void updateTemplate() {
		try {
			Process process = Runtime.getRuntime().exec("nuclei -ut");
			process.waitFor();//等待执行完成
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public JPanel createButtonPanel() {
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

		JButton buttonCreate = new JButton("Create PoC");
		buttonCreate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					File srcFile = new File(MainGUI.poctRootPath+File.separator+
							"cve2"+File.separator+"2021"+File.separator+"CVE-2021-1497.yaml");
					File destFile = getInputFile();
					if (null !=destFile) {
						FileUtils.copyFile(srcFile, destFile);
						PoCPanel.buttonFresh.doClick();
						Commons.editWithVSCode(destFile.getAbsolutePath());
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}

			public File getInputFile() {
				String message = "New PoC File Name";
				String initialSelectionValue = null;
				while (true) {
					String pocFileName = JOptionPane.showInputDialog(message, initialSelectionValue);
					if (null == pocFileName) {
						return null;
					}
					else if(!pocFileName.trim().equals("")) {
						if (!pocFileName.endsWith(".yaml")) {
							pocFileName = pocFileName+".yaml";
						}

						File destFile = new File(MainGUI.poctRootPath+File.separator+pocFileName);
						if (destFile.exists()) {
							initialSelectionValue = pocFileName;
							continue;
						}else {
							return destFile;
						}
					}
				}
			}
		});
		buttonPanel.add(buttonCreate);

		JButton buttonSearch = new JButton("Search");
		
		textFieldSearch = new SearchTextField("",buttonSearch);
		buttonPanel.add(textFieldSearch);
		
		buttonSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String keyword = textFieldSearch.getText().trim();
				titleTable.search(keyword);
				lblStatus.setText(titleTableModel.getStatusSummary());
			}
		});
		buttonPanel.add(buttonSearch);

		buttonFresh = new JButton("Fresh");
		buttonPanel.add(buttonFresh);
		buttonFresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateTemplate();
				LoadData(MainGUI.poctRootPath);
				lblStatus.setText(titleTableModel.getStatusSummary());
				buttonSearch.doClick();
			}
		});
		
		JButton buttonProxy = new JButton("Proxy");
		buttonPanel.add(buttonProxy);
		buttonProxy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String originProxy = MainGUI.getGlobalConfig().getProxy();
					String proxy = JOptionPane.showInputDialog("Proxy To Use: eg. http://127.0.0.1:8080", originProxy);
					if (proxy!= null) {
						MainGUI.getGlobalConfig().setProxy(proxy.trim());
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		
		JButton buttonHelp = new JButton("Help");
		buttonPanel.add(buttonHelp);
		buttonHelp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Commons.browserOpen("https://nuclei.projectdiscovery.io/nuclei/get-started/#running-nuclei", null);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});

		rdbtnUseRobotInput = new JRadioButton("RobotInput");
		if (Commons.isMac()) {
			rdbtnUseRobotInput.setSelected(false);
		}else {
			rdbtnUseRobotInput.setSelected(false);
		}
		
		buttonPanel.add(rdbtnUseRobotInput);

		lblStatus = new JLabel("Status");
		buttonPanel.add(lblStatus);

		return buttonPanel;
	}

	/*
	 * 用于从DB文件中加载数据，没有去重检查。
	 */
	public void showToPoCPanel(IndexedLinkedHashMap<String,LineEntry> lineEntries) {
		//titleTableModel.setLineEntries(new ArrayList<LineEntry>());//clear
		//这里没有fire delete事件，会导致排序号加载文件出错，但是如果fire了又会触发tableModel的删除事件，导致数据库删除。改用clear()
		titleTableModel.clear(false);//clear
		titleTableModel.setListenerIsOn(false);
		int row = lineEntries.size();
		titleTableModel.setLineEntries(lineEntries);//如果listener是on，将触发listener--同步到db文件
		if (row>=1) {
			titleTableModel.fireTableRowsInserted(0, row-1);
		}
		titleTableModel.setListenerIsOn(true);
		System.out.println(row+" title entries loaded from database file");
		stdout.println(row+" title entries loaded from database file");
		PoCPanel.getTitleTable().search("");// hide checked items
		lblStatus.setText(titleTableModel.getStatusSummary());
	}
}
