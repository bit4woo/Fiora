package PoC;

import java.awt.event.ActionEvent;
import java.io.PrintWriter;

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

		JMenuItem googleSearchItem = new JMenuItem(new AbstractAction("Seach on Google (double click index)") {
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
					String url= "https://github.com/search?q=%22"+searchContent+"%22+%22jdbc.url%22&type=Code";
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
		this.add(googleSearchItem);
		this.add(SearchOnGithubItem);
		this.add(SearchOnHunterItem);
		this.add(SearchOnFoFaItem);

	}

}
