package PoC;

import java.awt.Desktop;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.PrintWriter;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;

import GUI.TextAreaMouseListener;
import PoC.search.History;
import PoC.search.LineSearch;
import burp.BurpExtender;
import burp.Commons;

public class LineTable extends JTable
{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private LineTableModel lineTableModel;
	private TableRowSorter<LineTableModel> rowSorter;//TableRowSorter vs. RowSorter

	PrintWriter stdout;
	PrintWriter stderr;

	private JSplitPane tableAndDetailSplitPane;//table area + detail area
	private JTextArea textAreaTarget;
	private JTextArea textAreaPoCDetail;
	public JSplitPane getTableAndDetailSplitPane() {
		return tableAndDetailSplitPane;
	}

	public JTextArea getTextAreaTarget() {
		return textAreaTarget;
	}

	public void setTextAreaTarget(JTextArea textAreaTarget) {
		this.textAreaTarget = textAreaTarget;
	}

	public JTextArea getTextAreaPoCDetail() {
		return textAreaPoCDetail;
	}

	public void setTextAreaPoCDetail(JTextArea textAreaPoCDetail) {
		this.textAreaPoCDetail = textAreaPoCDetail;
	}

	//将选中的行（图形界面的行）转换为Model中的行数（数据队列中的index）.因为图形界面排序等操作会导致图像和数据队列的index不是线性对应的。
	public int[] SelectedRowsToModelRows(int[] SelectedRows) {

		int[] rows = SelectedRows;
		for (int i=0; i < rows.length; i++){
			rows[i] = convertRowIndexToModel(rows[i]);//转换为Model的索引，否则排序后索引不对应〿
		}
		Arrays.sort(rows);//升序

		return rows;
	}

	public LineTable(LineTableModel lineTableModel)
	{
		//super(lineTableModel);//这个方法创建的表没有header
		try{
			stdout = new PrintWriter(BurpExtender.getCallbacks().getStdout(), true);
			stderr = new PrintWriter(BurpExtender.getCallbacks().getStderr(), true);
		}catch (Exception e){
			stdout = new PrintWriter(System.out, true);
			stderr = new PrintWriter(System.out, true);
		}

		this.lineTableModel = lineTableModel;
		this.setFillsViewportHeight(true);//在table的空白区域显示右键菜单
		//https://stackoverflow.com/questions/8903040/right-click-mouselistener-on-whole-jtable-component
		this.setModel(lineTableModel);

		tableinit();
		//FitTableColumns(this);
		addClickSort();
		registerListeners();

		tableAndDetailSplitPane = tableAndDetailPanel();
	}

	@Override
	public void changeSelection(int row, int col, boolean toggle, boolean extend)
	{
		// show the log entry for the selected row
		LineEntry Entry = this.lineTableModel.getLineEntries().getValueAtIndex(super.convertRowIndexToModel(row));

		this.lineTableModel.setCurrentlyDisplayedItem(Entry);
		String detail = Entry.getDetail();
		textAreaPoCDetail.setText(detail);
		super.changeSelection(row, col, toggle, extend);
	}

	@Override
	public LineTableModel getModel(){
		//return (LineTableModel) super.getModel();
		return lineTableModel;
	}


	public JSplitPane tableAndDetailPanel(){
		JSplitPane splitPane = new JSplitPane();//table area + detail area
		splitPane.setResizeWeight(0.4);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		//TitlePanel.add(splitPane, BorderLayout.CENTER); // getTitlePanel to get it

		JScrollPane scrollPaneRequests = new JScrollPane(this,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);//table area
		//允许横向滚动条
		//scrollPaneRequests.setViewportView(titleTable);//titleTable should lay here.
		splitPane.setLeftComponent(scrollPaneRequests);

		JSplitPane RequestDetailPanel = new JSplitPane();//request and response
		RequestDetailPanel.setResizeWeight(0.2);
		splitPane.setRightComponent(RequestDetailPanel);

		JTabbedPane RequestPanel = new JTabbedPane();
		RequestDetailPanel.setLeftComponent(RequestPanel);

		JScrollPane scrollPane = new JScrollPane();
		RequestPanel.addTab("targets", null, scrollPane, null);

		textAreaTarget = new JTextArea();
		scrollPane.setViewportView(textAreaTarget);
		textAreaTarget.addMouseListener(new TextAreaMouseListener(textAreaTarget));

		JTabbedPane ResponsePanel = new JTabbedPane();
		RequestDetailPanel.setRightComponent(ResponsePanel);

		JScrollPane scrollPane1 = new JScrollPane();
		ResponsePanel.addTab("detail", null, scrollPane1, null);

		textAreaPoCDetail = new JTextArea();
		textAreaPoCDetail.setLineWrap(true);
		scrollPane1.setViewportView(textAreaPoCDetail);
		textAreaPoCDetail.addMouseListener(new TextAreaMouseListener(textAreaPoCDetail));

		this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);//配合横向滚动条

		return splitPane;
	}

	public void tableinit(){
		//Font f = new Font("Arial", Font.PLAIN, 12);
		Font f = this.getFont();
		FontMetrics fm = this.getFontMetrics(f);
		int width = fm.stringWidth("A");//一个字符的宽度

		Map<String,Integer> preferredWidths = LineEntry.fetchTableHeaderAndWidth();

		for(String header:LineTableModel.getTitletList()){
			try{//避免动态删除表字段时，出错
				int multiNumber = preferredWidths.get(header);
				this.getColumnModel().getColumn(this.getColumnModel().getColumnIndex(header))
				.setPreferredWidth(width*multiNumber);
			}catch (Exception e){

			}
		}
		//this.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
		this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);//配合横向滚动条

	}

	public void addClickSort() {//双击header头进行排序

		rowSorter = new TableRowSorter<LineTableModel>(lineTableModel);//排序和搜索
		LineTable.this.setRowSorter(rowSorter);

		JTableHeader header = this.getTableHeader();
		header.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					LineTable.this.getRowSorter().getSortKeys().get(0).getColumn();
					////当Jtable中无数据时，jtable.getRowSorter()是nul
				} catch (Exception e1) {
					e1.printStackTrace(stderr);
				}
			}
		});
	}

	//搜索功能函数
	public void search(String Inputkeyword) {
		History.getInstance().addRecord(Inputkeyword);//记录搜索历史,单例模式

		final String keyword = Inputkeyword.trim().toLowerCase();

		final RowFilter filter = new RowFilter() {
			@Override
			public boolean include(Entry entry) {
				//entry --- a non-null object that wraps the underlying object from the model
				int row = (int) entry.getIdentifier();
				LineEntry line = rowSorter.getModel().getLineEntries().getValueAtIndex(row);

				return LineSearch.textFilte(line,keyword);
			}
		};
		rowSorter.setRowFilter(filter);
	}

	public void registerListeners(){
		LineTable.this.setRowSelectionAllowed(true);
		this.addMouseListener( new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e) {
				//双击进行google搜索、双击浏览器打开url、双击切换Check状态
				if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2){//左键双击
					int[] rows = SelectedRowsToModelRows(getSelectedRows());

					//int row = ((LineTable) e.getSource()).rowAtPoint(e.getPoint()); // 获得行位置
					int col = ((LineTable) e.getSource()).columnAtPoint(e.getPoint()); // 获得列位置

					LineEntry selecteEntry = LineTable.this.lineTableModel.getLineEntries().getValueAtIndex(rows[0]);
					if ((col==0 || col == 1)) {//双击Index或文件名。打开文件
						String path = selecteEntry.getPocFileFullPath();
						Commons.editWithVSCode(path);
					}else{//其他都进行搜索
						String host = selecteEntry.getPocfile();
						String url= "https://www.google.com/search?q=site%3A"+host;
						try {
							URI uri = new URI(url);
							Desktop desktop = Desktop.getDesktop();
							if(Desktop.isDesktopSupported()&&desktop.isSupported(Desktop.Action.BROWSE)){
								desktop.browse(uri);
							}
						} catch (Exception e2) {
							e2.printStackTrace();
						}
					}
				}
			}

			@Override//title表格中的鼠标右键菜单
			public void mouseReleased( MouseEvent e ){
				if ( SwingUtilities.isRightMouseButton( e )){
					if (e.isPopupTrigger() && e.getComponent() instanceof JTable ) {
						//getSelectionModel().setSelectionInterval(rows[0], rows[1]);
						int[] rows = getSelectedRows();
						int col = ((LineTable) e.getSource()).columnAtPoint(e.getPoint()); // 获得列位置
						if (rows.length>0){
							rows = SelectedRowsToModelRows(getSelectedRows());
							new LineEntryMenu(LineTable.this, rows, col).show(e.getComponent(), e.getX(), e.getY());
						}else{//在table的空白处显示右键菜单
							//https://stackoverflow.com/questions/8903040/right-click-mouselistener-on-whole-jtable-component
							//new LineEntryMenu(_this).show(e.getComponent(), e.getX(), e.getY());
						}
					}
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
				mouseReleased(e);
			}
		});
	}
}
