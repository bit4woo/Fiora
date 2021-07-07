package PoC;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;

import GUI.MainGUI;
import PoC.search.SearchTextField;
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
		LoadData(MainGUI.poctRootPath+File.separator+"script");
	}

	//D:\github\POC-T\script
	public IndexedLinkedHashMap<String,LineEntry> scanPoCFiles(String dir) {
		IndexedLinkedHashMap<String,LineEntry> lineEntries = new IndexedLinkedHashMap<String,LineEntry>();
		if (null==dir || !new File(dir).exists()){
			return lineEntries;
		}
		Collection<File> files = FileUtils.listFiles(new File(dir), FileFilterUtils.suffixFileFilter(".py"), DirectoryFileFilter.INSTANCE);
		for (File file:files) {
			//System.out.println(file.toString());
			if (file.exists() && file.isFile() && !file.getName().startsWith("__")) {
				LineEntry entry = new LineEntry(file.toString());
				lineEntries.put(file.toString(), entry);
			}
		}
		return lineEntries;
	}

	public boolean FindPoCTRootByEnv() {
		String pathvalue = System.getenv().get("PATH");
		//在mac中，这个方法只能获取到/etc/paths中的内容，所以需要将路径写入这个文件。
		String[] items;
		if (Commons.isMac()){
			items = pathvalue.split(":");
		}else{
			items = pathvalue.split(";");
		}
		for (String item:items) {
			File tmpPath = new File(item);
			if (tmpPath.isDirectory()) {
				Collection<File> files = FileUtils.listFiles(tmpPath, FileFilterUtils.suffixFileFilter("py"), DirectoryFileFilter.INSTANCE);
				for (File file:files) {
					String path = file.toString();
					if (path.contains("POC-T"+File.separator+"script") || path.endsWith("POC-T.py")) {
						MainGUI.poctRootPath = item;
						return true;
					}
				}
			}
		}
		return false;
	}

	@Deprecated
	public void FindPoCTRoot() {
		SwingWorker<Map, Map> worker = new SwingWorker<Map, Map>() {

			@Override
			protected Map doInBackground() throws Exception {
				for (File driver:File.listRoots()) {
					Collection<File> files = FileUtils.listFiles(driver, FileFilterUtils.suffixFileFilter("py"), DirectoryFileFilter.INSTANCE);
					for (File file:files) {
						String path = file.toString();
						String keyword = "POC-T"+File.separator+"script";
						if (path.contains("POC-T"+File.separator+"script")) {
							String poctRootPath = path.substring(0,path.indexOf(keyword)+keyword.length()+1);
							MainGUI.poctRootPath = poctRootPath;
							return null;
						}
					}
				}
				return null;
			}
		};
	}


	public boolean LoadData(String dir){
		try {//这其中的异常会导致burp退出
			System.out.println("=================================");
			System.out.println("==Start Loading Data From: " + dir+"==");
			stdout.println("==Start Loading Data From: " + dir+"==");

			showToPoCPanel(scanPoCFiles(dir));

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

	public JPanel createButtonPanel() {
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		
		JButton buttonFind = new JButton("Find PoC-T");//通过path环境变量获取，磁盘查找太慢了
		buttonFind.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!FindPoCTRootByEnv()){
					JOptionPane.showMessageDialog(null,"Not found PoC-T in Environment,you should add it to path");
					FindPoCTRoot();
				};
				if (null!=MainGUI.poctRootPath){
					System.out.println("PoC-T Founded : "+MainGUI.poctRootPath);
				}
			}
		});
		buttonPanel.add(buttonFind);

		JButton buttonOpenDir = new JButton("Open Folder");
		buttonPanel.add(buttonOpenDir);
		buttonOpenDir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					//JOptionPane.showMessageDialog(null,"Not found editor(code.exe idle.bat) in environment.");
					File file = new File(MainGUI.poctRootPath);
					String path = file.getPath()+File.separator+"script";
					Commons.OpenFolder(path);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});

		JButton buttonCreate = new JButton("Create PoC");
		buttonCreate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					File srcFile = new File(MainGUI.poctRootPath+File.separator+"script"+File.separator+"__AAA-Template.py");
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
						if (!pocFileName.endsWith(".py")) {
							pocFileName = pocFileName+".py";
						}

						File destFile = new File(MainGUI.poctRootPath+File.separator+"script"+File.separator+pocFileName);
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

		textFieldSearch = new SearchTextField().Create("");
		buttonPanel.add(textFieldSearch);

		JButton buttonSearch = new JButton("Search");
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
				LoadData(MainGUI.poctRootPath);
				lblStatus.setText(titleTableModel.getStatusSummary());
				buttonSearch.doClick();
			}
		});

/*		JButton btnRun = new JButton("Run");
		buttonPanel.add(btnRun);
		btnRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String pocFullPath = PoCPanel.getTitleTableModel().getCurrentlyDisplayedItem().getPocFileFullPath();
				RunPoCAction.run(pocFullPath);
			}
		});

		JButton buttonRun = new JButton("RunWithPoC-T");
		buttonPanel.add(buttonRun);
		buttonRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				List<String> targets = Commons.getLinesFromTextArea(PoCPanel.getTitleTable().getTextAreaTarget());
				String poc = PoCPanel.getTitleTableModel().getCurrentlyDisplayedItem().getPocfile();
				RunPoCAction.runWithPoCT(targets, poc);
			}
		});*/


		rdbtnUseRobotInput = new JRadioButton("RobotInput");
		rdbtnUseRobotInput.setSelected(true);
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
