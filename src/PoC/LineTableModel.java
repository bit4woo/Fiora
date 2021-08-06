package PoC;

import java.io.PrintWriter;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import PoCParser.YamlInfo;
import burp.BurpExtender;
import burp.Commons;
import burp.IntArraySlice;


public class LineTableModel extends AbstractTableModel implements Serializable {
	//https://stackoverflow.com/questions/11553426/error-in-getrowcount-on-defaulttablemodel
	//when use DefaultTableModel, getRowCount encounter NullPointerException. why?
	/**
	 * LineTableModel中数据如果类型不匹配，或者有其他问题，可能导致图形界面加载异常！
	 */
	private static final long serialVersionUID = 1L;
	private LineEntry currentlyDisplayedItem;

	/*
	 * 为了提高LineEntry的查找速度，改为使用LinkedHashMap,
	 * http://infotechgems.blogspot.com/2011/11/java-collections-performance-time.html
	 * 
	 * LinkedHashMap是继承于HashMap，是基于HashMap和双向链表来实现的。
	 * HashMap无序；LinkedHashMap有序，可分为插入顺序和访问顺序两种。默认是插入顺序。
	 * 如果是访问顺序，那put和get操作已存在的Entry时，都会把Entry移动到双向链表的表尾(其实是先删除再插入)。
	 * LinkedHashMap是线程不安全的。
	 */
	private IndexedLinkedHashMap<String,LineEntry> lineEntries =new IndexedLinkedHashMap<String,LineEntry>();
	private IndexedLinkedHashMap<String,Set<String>> noResponseDomain =new IndexedLinkedHashMap<String,Set<String>>();
	//private boolean EnableSearch = Runtime.getRuntime().totalMemory()/1024/1024/1024 > 16;//if memory >16GB enable Search. else disable.
	private boolean ListenerIsOn = true;
	//private PrintWriter stderr = new PrintWriter(BurpExtender.callbacks.getStderr(), true);

	PrintWriter stdout;
	PrintWriter stderr;

//	private static final String[] standardTitles = new String[] {
//			"#", "filename", "CVE", "VulnApp", "VulnVersion", "VulnURL","VulnParameter","VulnType","VulnDescription","Reference","Verified"};
//	
//	private static List<String> titletList = new ArrayList<>(Arrays.asList(standardTitles));
	private static List<String> titletList = LineEntry.fetchFieldNames();
	//为了实现动态表结构
	public static List<String> getTitletList() {
		return titletList;
	}


	public LineTableModel(){

		try{
			stdout = new PrintWriter(BurpExtender.getCallbacks().getStdout(), true);
			stderr = new PrintWriter(BurpExtender.getCallbacks().getStderr(), true);
		}catch (Exception e){
			stdout = new PrintWriter(System.out, true);
			stderr = new PrintWriter(System.out, true);
		}
		/*
		关于这个listener，主要的目标的是当数据发生改变时，更新到数据库。通过fireTableRowsxxxx来触发。
		但是clear()中对lineEntries的操作也触发了，注意
		The call to fireTableRowsDeleted simply fires off the event to indicate rows have been deleted, you still need to actually remove them from the model.
		 */
		this.addTableModelListener(new TableModelListener() {//表格模型监听
			@Override
			public void tableChanged(TableModelEvent e) {
			}
		});
	}

	public IndexedLinkedHashMap<String, LineEntry> getLineEntries() {
		return lineEntries;
	}

	public void setLineEntries(IndexedLinkedHashMap<String, LineEntry> lineEntries) {
		this.lineEntries = lineEntries;
	}

	public boolean isListenerIsOn() {
		return ListenerIsOn;
	}

	public void setListenerIsOn(boolean listenerIsOn) {
		this.ListenerIsOn = listenerIsOn;
	}


	////////////////////// extend AbstractTableModel////////////////////////////////

	@Override
	public int getColumnCount()
	{
		return titletList.size();//the one is the request String + response String,for search
	}

	@Override
	public Class<?> getColumnClass(int columnIndex)
	{

		if (columnIndex == titletList.indexOf("#")) {
			return Integer.class;//id
		}
		if (columnIndex == titletList.indexOf("isPoCVerified")) {
			return boolean.class;//id
		}
		return String.class;
	}

	@Override
	public int getRowCount()
	{
		return lineEntries.size();
	}

	//define header of table???
	@Override
	public String getColumnName(int columnIndex) {
		if (columnIndex >= 0 && columnIndex <= titletList.size()) {
			return titletList.get(columnIndex);
		}else {
			return "";
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (titletList.get(columnIndex).equals("Comments")) {//可以编辑comment
			return true;
		}else {
			return false;
		}
	}



	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		LineEntry entry = lineEntries.getValueAtIndex(rowIndex);
		//entry.parse();---
		//"#", "filename", "VulnApp", "VulnVersion", "VulnURL","VulnParameter","VulnType","VulnDescription","Reference","isPoCVerified", "22","33"};
		if (columnIndex == titletList.indexOf("#")) {
			return rowIndex;
		}
		String titleName = titletList.get(columnIndex);
		try {
			return entry.callGetter(titleName);
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
		/*
		if (columnIndex == titletList.indexOf("filename")){
			return entry.getPocfile();
		}
		if (columnIndex == titletList.indexOf("CVE")){
			return entry.getCVE();
		}
		if (columnIndex == titletList.indexOf("VulnApp")){
			return entry.getVulnApp();
		}
		if (columnIndex == titletList.indexOf("VulnVersion")){
			return entry.getVulnVersion();
		}
		if (columnIndex == titletList.indexOf("VulnURL")){
			return entry.getVulnURL();
		}
		if (columnIndex == titletList.indexOf("VulnParameter")){
			return entry.getVulnParameter();
		}
		if (columnIndex == titletList.indexOf("VulnType")){
			return entry.getVulnType();
		}
		if (columnIndex == titletList.indexOf("VulnDescription")){
			return entry.getVulnDescription();
		}
		if (columnIndex == titletList.indexOf("Reference")){
			return entry.getReference();
		}
		if (columnIndex == titletList.indexOf("Verified")){
			return entry.getIsPoCVerified();
		}
		return "";
		*/
	}


	@Override
	public void setValueAt(Object value, int row, int col) {
	}
	//////////////////////extend AbstractTableModel////////////////////////////////

	public String getStatusSummary() {
		int all = lineEntries.size();
		return String.format(" [%s PoCs in total]",all);
	}

	public void clear(boolean syncToFile) {
		//		if (syncToFile){
		//			this.setListenerIsOn(true);
		//		}else {
		//			this.setListenerIsOn(false);
		//		}
		this.setListenerIsOn(false);//这里之所以要关闭listener，是因为LineEntries为空时，执行listener中的逻辑将出错而退出。而后续获取title的逻辑就会中断。就丢失了title的历史记录。
		int rows = this.getRowCount();
		stderr.print("rows:"+rows);
		//this.setLineEntries(new ArrayList<LineEntry>());//这个方式无法通过listenser去同步数据库，因为LineEntries已经空了。
		//虽然触发了，却无法更新数据库。
		this.setLineEntries(new IndexedLinkedHashMap<String,LineEntry>());//如果ListenerIsOn，将会触发listener
		System.out.println("clean lines of old data,"+rows+" lines cleaned");
		if (rows-1 >=0)	fireTableRowsDeleted(0, rows-1);
		this.setListenerIsOn(true);
	}
	//为了同时fire多个不连续的行，自行实现这个方法。
	private void fireDeleted(int[] rows) {
		List<int[]> slice = IntArraySlice.slice(rows);
		//必须逆序，从高位index开始删除，否则删除的对象和预期不一致！！！
		//上面得到的顺序就是从高位开始的
		for(int[] sli:slice) {
			System.out.println(Arrays.toString(sli));
			this.fireTableRowsDeleted(sli[sli.length-1],sli[0]);//这里传入的值必须是低位数在前面，高位数在后面
		}
	}

	private void fireUpdated(int[] rows) {
		List<int[]> slice = IntArraySlice.slice(rows);
		for(int[] sli:slice) {
			System.out.println(Arrays.toString(sli));
			this.fireTableRowsUpdated(sli[sli.length-1],sli[0]);//同上，修复更新多个记录时的错误
		}
	}

	///////////////////多个行内容的增删查改/////////////////////////////////

	public void addNewLineEntry(LineEntry lineEntry){
		if (lineEntry == null) {
			return;
		}
		synchronized (lineEntries) {
			int oldsize = lineEntries.size();
			String key = lineEntry.getPocfile();
			lineEntries.put(key,lineEntry);
			int newsize = lineEntries.size();
			int index = lineEntries.IndexOfKey(key);
			if (oldsize == newsize) {//覆盖
				fireTableRowsUpdated(index, index);
			}else {//新增
				fireTableRowsInserted(index, index);
			}

			//need to use row-1 when add setRowSorter to table. why??
			//https://stackoverflow.com/questions/6165060/after-adding-a-tablerowsorter-adding-values-to-model-cause-java-lang-indexoutofb
			//fireTableRowsInserted(newsize-1, newsize-1);
		}
	}
	/*
	这个方法更新了URL的比对方法，无论是否包含默认端口都可以成功匹配
	 */
	public LineEntry findLineEntry(String url) {//这里的URL需要包含默认端口!!!
		if (lineEntries == null) return null;
		//之前的方法：统一使用URL的格式进行比较，最需要自己主动用for循环去遍历元素，然后对比。但这种方法不能发挥hashmap的查找速度优势。
		//更好的方法：用hashMap的get方法去查找，看是否能找到对象，get方法是根据key的hash值进行查找的速度比自行循环对比快很多。

		//统一URL字符串的格式
		url = Commons.formateURLString(url);
		return lineEntries.get(url);
	}

	/*
	 * find all lineEntries base host，当需要对整个主机的所有服务进行操作时用这个方法
	 * 正确的范围是一个service，即Host+port，弃用这个函数
	 */
	@Deprecated
	public List<LineEntry> findLineEntriesByHost(String host) {//
		if (lineEntries == null) return null;
		List<LineEntry> result = new ArrayList<LineEntry>();
		for (String urlkey:lineEntries.keySet()) {
			try{//根据host查找
				URL URL = new URL(urlkey);
				if (URL.getHost().equalsIgnoreCase(host)) {
					result.add(lineEntries.get(urlkey));
				}
			}catch (Exception e){
				e.printStackTrace(BurpExtender.getStderr());
			}
		}
		return result;
	}

	public LineEntry getCurrentlyDisplayedItem() {
		return this.currentlyDisplayedItem;
	}

	public void setCurrentlyDisplayedItem(LineEntry currentlyDisplayedItem) {
		this.currentlyDisplayedItem = currentlyDisplayedItem;
	}

}