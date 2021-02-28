package PoC;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import burp.BurpExtender;
import burp.Commons;

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


		JMenuItem editPoCItem = new JMenuItem(new AbstractAction("Edit this PoC(double click index)") {
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
		
		JMenuItem copyFilePathItem = new JMenuItem(new AbstractAction("Copy File Path") {
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
		
		JMenuItem checkURLItem = new JMenuItem(new AbstractAction("check the vuln url") {
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

		JMenuItem googleSearchItem = new JMenuItem(new AbstractAction("Seach on Google") {
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

		JMenuItem SearchOnHunterItem = new JMenuItem(new AbstractAction("Seach On Hunter") {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				String searchContent = LineEntryMenu.getValue(rows[0], columnIndex);
				PoCPanel.getTextFieldSearch().setText(searchContent);
			}
		});

		this.add(itemNumber);
		this.addSeparator();
		
		this.add(editPoCItem);
		this.add(checkURLItem);
		this.add(copyFilePathItem);
		this.add(googleSearchItem);
		this.add(SearchOnGithubItem);
		this.add(SearchOnHunterItem);
		this.add(SearchOnFoFaItem);
	}
}
