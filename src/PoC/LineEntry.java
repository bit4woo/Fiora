package PoC;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

public class LineEntry {
	private static final Logger log=LogManager.getLogger(LineEntry.class);

	//{"#", "filename", "VulnApp", "VulnVersion", "VulnURL","VulnParameter","VulnType","VulnDescription","Refrence","isPoCVerified", "22","33"};
	private String pocFileFullPath = ""; //PoC文件完整路径，用于定位文件进行编辑。不显示。
	private String pocfile = ""; //PoC文件名称，用于显示
	private String VulnApp = ""; //存在漏的目标应用程序名称
	private String VulnVersion = "";//存在漏洞的版本
	private String VulnURL = "";//存在漏洞的URL地址
	private String VulnParameter = "";//存在漏的参数或者位置，
	private String VulnType = "";//漏洞类型
	private String VulnDescription = "";//漏洞描述，
	private String Reference = "";//参考资料，复现文章，复现环境等地址
	private String isPoCVerified = "false";//poc是否经过确认，真实有效
	private String Author = "";//poc的作者
	private String CVE = "";
	private String severity = ""; //unclei的字段
	private String tags = ""; //unclei的字段
	private String detail = ""; //存储nuclei中的文件内容。
	
	public static LinkedHashMap<String,Integer> fetchTableHeaderAndWidth(){
		LinkedHashMap<String,Integer> preferredWidths = new LinkedHashMap<String,Integer>();
		preferredWidths.put("#",5);
		preferredWidths.put("pocfile",30);
		preferredWidths.put("CVE","CVE-2019-1234567".length());
		preferredWidths.put("severity",10);
		preferredWidths.put("tags",10);
		preferredWidths.put("VulnDescription",30);
		preferredWidths.put("Reference",30);
		preferredWidths.put("Author",10);
		return preferredWidths;
	}

	public static List<String> fetchTableHeaderList(){
		LinkedHashMap<String, Integer> headers = fetchTableHeaderAndWidth();
		List<String> keys = new ArrayList<>(headers.keySet());//这样不会有问题吗？
		return keys;
	}
	public String getPocFileFullPath() {
		return pocFileFullPath;
	}

	public void setPocFileFullPath(String pocFileFullPath) {
		this.pocFileFullPath = pocFileFullPath;
	}

	public String getPocfile() {
		return pocfile;
	}

	public void setPocfile(String pocfile) {
		this.pocfile = pocfile;
	}

	public String getVulnApp() {
		return VulnApp;
	}

	public void setVulnApp(String vulnApp) {
		VulnApp = vulnApp;
	}

	public String getVulnVersion() {
		return VulnVersion;
	}

	public void setVulnVersion(String vulnVersion) {
		VulnVersion = vulnVersion;
	}

	public String getVulnURL() {
		return VulnURL;
	}

	public void setVulnURL(String vulnURL) {
		VulnURL = vulnURL;
	}

	public String getVulnParameter() {
		return VulnParameter;
	}

	public void setVulnParameter(String vulnParameter) {
		VulnParameter = vulnParameter;
	}

	public String getVulnType() {
		return VulnType;
	}

	public void setVulnType(String vulnType) {
		VulnType = vulnType;
	}

	public String getVulnDescription() {
		return VulnDescription;
	}

	public void setVulnDescription(String vulnDescription) {
		VulnDescription = vulnDescription;
	}

	public String getReference() {
		return Reference;
	}

	public void setReference(String reference) {
		Reference = reference;
	}

	public String getIsPoCVerified() {
		return isPoCVerified;
	}

	public void setIsPoCVerified(String isPoCVerified) {
		this.isPoCVerified = isPoCVerified;
	}

	public String getAuthor() {
		return Author;
	}

	public void setAuthor(String author) {
		Author = author;
	}

	public String getCVE() {
		return CVE;
	}

	public void setCVE(String cVE) {
		CVE = cVE;
	}

	public String getSeverity() {
		return severity;
	}

	public void setSeverity(String severity) {
		this.severity = severity;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}
	

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public static Logger getLog() {
		return log;
	}

	public LineEntry(){

	}

	public String ToJson(){//注意函数名称，如果是get set开头，会被认为是Getter和Setter函数，会在序列化过程中被调用。
		return new Gson().toJson(this);
	}

	public static LineEntry FromJson(String json){//注意函数名称，如果是get set开头，会被认为是Getter和Setter函数，会在序列化过程中被调用。
		return new Gson().fromJson(json, LineEntry.class);
	}
	
	public static List<String> fetchFieldNames(){
		Field[] fields = LineEntry.class.getDeclaredFields();
		List<String> result = new ArrayList<String>();
		for(Field field : fields){
			result.add(field.getName());
		}
		return result;
	}
	
	@Deprecated
	public Object callGetter(String paraName) throws Exception {
		Method[] methods = LineEntry.class.getMethods();
		for(Method method : methods){
			if(method.getName().equalsIgnoreCase("get"+paraName)) {
				Class<?> returnType = method.getReturnType();
				Object result = method.invoke(this);
				return returnType.cast(result);
			}
		}
		return "";
	}
	
	public Object fetchValue(String paraName) throws Exception {
		//Field[] fields = LineEntry.class.getDeclaredFields();
		Field field = LineEntry.class.getDeclaredField(paraName);
		return field.get(this);
	}
	
	public boolean isWorkflow() {
		boolean result =  CVE.toLowerCase().contains("workflow") && (severity== null || severity.equals(""));
		//System.out.println(result);
		return result;
	}
	

	public static void main(String args[]) {
		System.out.println(fetchTableHeaderList());
	}
}
