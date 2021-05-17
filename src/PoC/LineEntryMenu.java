package PoC;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import org.apache.commons.io.FileUtils;

import GUI.MainGUI;
import burp.BurpExtender;
import burp.Commons;
import run.RunPoCAction;
import run.Utils;

public class LineEntryMenu extends JPopupMenu {


	private static final long serialVersionUID = 1L;
	PrintWriter stdout = BurpExtender.getStdout();
	PrintWriter stderr = BurpExtender.getStderr();
	private static LineTable lineTable;

	public static void main(String[] args) throws Exception {
		Commons.browserOpen("[]", null);
	}

	public static String getValue(int rowIndex,int columnIndex) {
		//由于所有的返回值都是String类型的，都可以直接强制类型转换
		Object value = lineTable.getModel().getValueAt(rowIndex, columnIndex);
		return (String)value;

	}

	LineEntryMenu(final LineTable lineTable, final int[] rows,final int columnIndex){
		this.lineTable = lineTable;

		JMenuItem itemNumber = new JMenuItem(new AbstractAction(rows.length+" Items Selected") {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
			}
		});

		//one line
		JMenuItem editPoCItem = new JMenuItem(new AbstractAction("Edit This PoC(Double Click Index)") {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				if (rows.length >=50) {
					return;
				}
				LineEntry selecteEntry = lineTable.getModel().getLineEntries().getValueAtIndex(rows[0]);
				String path = selecteEntry.getPocFileFullPath();
				Commons.openPoCFile(path);
			}
		});

		//one line
		JMenuItem renamePoCItem = new JMenuItem(new AbstractAction("Rename This PoC") {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				if (rows.length >=50) {
					return;
				}

				try {
					LineEntry selecteEntry = lineTable.getModel().getLineEntries().getValueAtIndex(rows[0]);
					String path = selecteEntry.getPocFileFullPath();
					File srcFile = new File(path);

					String oldname = selecteEntry.getPocfile();

					String pocFileName = JOptionPane.showInputDialog("New Name", oldname);
					if (pocFileName != null && !pocFileName.trim().equals("")) {
						if (!pocFileName.endsWith(".py")) {
							pocFileName = pocFileName+".py";
						}
						File destFile = new File(MainGUI.poctRootPath+"\\script\\"+pocFileName);

						FileUtils.moveFile(srcFile, destFile);
						PoCPanel.buttonFresh.doClick();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		//one line
		JMenuItem showInExplorerItem = new JMenuItem(new AbstractAction("Show In System Explorer") {
			@Override		
			public void actionPerformed(ActionEvent e) {
				try {
					//JOptionPane.showMessageDialog(null,"Not found editor(code.exe idle.bat) in environment.");
					File file = new File(MainGUI.poctRootPath);
					String path = file+File.pathSeparator+"script";
					Utils.OpenFolder(path);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});

		//multiple line
		JMenuItem copyFilePathItem = new JMenuItem(new AbstractAction("Copy PoC File Location") {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				if (rows.length >=50) {
					return;
				}
				List<String> paths = new ArrayList<String>();
				for (int row:rows) {
					LineEntry entry = lineTable.getModel().getLineEntries().getValueAtIndex(row);
					String path = entry.getPocFileFullPath();
					paths.add(path);
				}
				String textUrls = String.join(System.lineSeparator(), paths);
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				StringSelection selection = new StringSelection(textUrls);
				clipboard.setContents(selection, null);

			}
		});

		//multiple line
		JMenuItem copyVulnUrlItem = new JMenuItem(new AbstractAction("Copy The vuln URL") {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				if (rows.length >=50) {
					return;
				}
				List<String> paths = new ArrayList<String>();
				for (int row:rows) {
					LineEntry entry = lineTable.getModel().getLineEntries().getValueAtIndex(row);
					String path = entry.getPocFileFullPath();
					paths.add(path);
				}
				String textUrls = String.join(System.lineSeparator(), paths);
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				StringSelection selection = new StringSelection(textUrls);
				clipboard.setContents(selection, null);

			}
		});

		//one line
		JMenuItem checkURLItem = new JMenuItem(new AbstractAction("Check The Vuln URL") {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				if (rows.length >=50) {
					return;
				}
				LineEntry entry = lineTable.getModel().getLineEntries().getValueAtIndex(rows[0]);
				String path = entry.getVulnURL();
				List<String> targets = Commons.getLinesFromTextArea(PoCPanel.getTitleTable().getTextAreaTarget());
				for (String target:targets) {
					if (target.trim().equals("")) {
						continue;
					}else if (target.startsWith("http")) {
						String url = Commons.getShortUrl(target);
						if (path.startsWith("/")) {
							path = path.replaceFirst("/", "");
						}
						url = url+path;
						try {
							Commons.browserOpen(url, null);
						} catch (Exception e) {
							e.printStackTrace(stderr);
						}
					}else {
						String url= String.format("http://%s%s",target, path);
						String url1= String.format("http://%s%s",target, path);
						try {
							Commons.browserOpen(url, null);
							Commons.browserOpen(url1, null);
						} catch (Exception e) {
							e.printStackTrace(stderr);
						}
					}
				}
			}
		});

		//multiple line
		JMenuItem googleSearchItem = new JMenuItem(new AbstractAction("Seach On Google") {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				if (rows.length >=50) {
					return;
				}
				for (int row:rows) {
					//String searchContent = (String)lineTable.getModel().getValueAt(row, columnIndex);
					String searchContent = LineEntryMenu.getValue(row, columnIndex);
					String url= "https://www.google.com/search?q="+searchContent;
					try {
						Commons.browserOpen(url, null);
					} catch (Exception e) {
						e.printStackTrace(stderr);
					}
				}
			}
		});

		//multiple line
		JMenuItem SearchOnGithubItem = new JMenuItem(new AbstractAction("Seach On Github") {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				if (rows.length >=50) {
					return;
				}
				for (int row:rows) {
					String searchContent = LineEntryMenu.getValue(row, columnIndex);
					String url= "https://github.com/search?q=%22"+searchContent+"%22&type=Code";
					try {
						Commons.browserOpen(url, null);
					} catch (Exception e) {
						e.printStackTrace(stderr);
					}
				}
			}
		});

		//multiple line
		JMenuItem SearchOnFoFaItem = new JMenuItem(new AbstractAction("Seach On FoFa") {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {

				if (rows.length >=50) {
					return;
				}
				for (int row:rows) {
					String searchContent = LineEntryMenu.getValue(row, columnIndex);
					String url= "https://fofa.so/result?q=%22"+searchContent+"%22";
					try {
						Commons.browserOpen(url, null);
					} catch (Exception e) {
						e.printStackTrace(stderr);
					}
				}
			}

		});

		//one line
		JMenuItem SearchOnFioraItem = new JMenuItem(new AbstractAction("Seach On Fiora") {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				String searchContent = LineEntryMenu.getValue(rows[0], columnIndex);
				PoCPanel.getTextFieldSearch().setText(searchContent);
			}
		});

		JMenuItem RunItem = new JMenuItem(new AbstractAction("Run This PoC") {
			@Override
			public void actionPerformed(ActionEvent e) {
				String pocFullPath = PoCPanel.getTitleTableModel().getCurrentlyDisplayedItem().getPocFileFullPath();
				RunPoCAction.run(pocFullPath);
			}
		});

		JMenuItem RunWithPoCTItem = new JMenuItem(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				List<String> targets = Commons.getLinesFromTextArea(PoCPanel.getTitleTable().getTextAreaTarget());
				String poc = PoCPanel.getTitleTableModel().getCurrentlyDisplayedItem().getPocfile();
				RunPoCAction.runWithPoCT(targets, poc);
			}
		});

		this.add(itemNumber);
		this.addSeparator();

		this.add(editPoCItem);//edit
		this.add(showInExplorerItem);
		this.add(renamePoCItem);

		this.addSeparator();//run check
		this.add(RunItem);
		this.add(RunWithPoCTItem);
		this.add(checkURLItem);

		this.addSeparator();// copy
		this.add(copyFilePathItem);
		this.add(copyVulnUrlItem);

		this.addSeparator();// search
		this.add(googleSearchItem);
		this.add(SearchOnGithubItem);
		this.add(SearchOnFoFaItem);
		this.add(SearchOnFioraItem);// 1 line
	}
}
