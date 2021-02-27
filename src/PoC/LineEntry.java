package PoC;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
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

	public static Logger getLog() {
		return log;
	}

	LineEntry(){

	}

	public LineEntry(String pocfile) {
		parse(pocfile);
	}

	public String ToJson(){//注意函数名称，如果是get set开头，会被认为是Getter和Setter函数，会在序列化过程中被调用。
		return new Gson().toJson(this);
	}

	public static LineEntry FromJson(String json){//注意函数名称，如果是get set开头，会被认为是Getter和Setter函数，会在序列化过程中被调用。
		return new Gson().fromJson(json, LineEntry.class);
	}

	private void parse(String pocfile) {
		File poc = new File(pocfile);
		if (poc.exists() && poc.isFile()) {
			try {
				this.setPocFileFullPath(pocfile);
				this.setPocfile(poc.getName());

				String content = FileUtils.readFileToString(poc);

				//final String DOMAIN_NAME_PATTERN = "__.*__s+=s+\\\"\\\"\\\".*\\\"\\\"\\\"";
				//有问号的是非贪婪模式，不带问号就是贪婪模式，直到最后一个三引号。
				final String DOMAIN_NAME_PATTERN = "__.*?__.*?\\\"\\\"\\\"[\\s\\S]*?\\\"\\\"\\\"";
				Pattern pDomainNameOnly = Pattern.compile(DOMAIN_NAME_PATTERN);
				Matcher matcher = pDomainNameOnly.matcher(content);
				while (matcher.find()) {//多次查找
					String found = matcher.group();
					//System.out.println(found);
					if (!(found.startsWith("__") && found.contains("="))) {
						continue;
					}
					if (found.startsWith("__author__")) {
						this.setAuthor(fetchValue(found));
					}
					if (found.startsWith("__CVE__")) {
						this.setCVE(fetchValue(found));
					}
					if (found.startsWith("__VulnApp__")) {
						this.setVulnApp(fetchValue(found));
					}
					if (found.startsWith("__VulnVersion__")) {
						this.setVulnVersion(fetchValue(found));
					}
					if (found.startsWith("__VulnURL__")) {
						this.setVulnURL(fetchValue(found));
					}
					if (found.startsWith("__VulnParameter__")) {
						this.setVulnParameter(fetchValue(found));
					}
					if (found.startsWith("__VulnType__")) {
						this.setVulnType(fetchValue(found));
					}
					if (found.startsWith("__VulnDescription__")) {
						this.setVulnDescription(fetchValue(found));
					}
					if (found.startsWith("__Reference__")) {
						this.setReference(fetchValue(found));
					}
					if (found.startsWith("__isPoCVerified__")) {
						this.setIsPoCVerified(fetchValue(found));
					}
				}
			}catch(Exception e) {
				log.error(e);
			}
		}
	}

	public static String fetchValue(String line) {
		line = line.split("=",2)[1].trim();
		if (line.startsWith("\'\'\'") || line.startsWith("\"\"\"")) {
			line = line.substring(3, line.length()-3);
		}else if (line.startsWith("\'") || line.startsWith("\"")) {
			line = line.substring(1, line.length()-1);
		}
		return line;
	}


	public String fetchDetail() {
		StringBuilder detail = new StringBuilder();
		detail.append("Vuln App:");
		detail.append(System.lineSeparator());
		detail.append(this.getVulnApp());
		detail.append(System.lineSeparator()+System.lineSeparator());

		detail.append("Vuln Version:");
		detail.append(System.lineSeparator());
		detail.append(this.getVulnVersion());
		detail.append(System.lineSeparator()+System.lineSeparator());

		detail.append("Vuln URL:");
		detail.append(System.lineSeparator());
		detail.append(this.getVulnURL());
		detail.append(System.lineSeparator()+System.lineSeparator());

		detail.append("Vuln Parameter:");
		detail.append(System.lineSeparator());
		detail.append(this.getVulnParameter());
		detail.append(System.lineSeparator()+System.lineSeparator());

		detail.append("Vuln Description:");
		detail.append(System.lineSeparator());
		detail.append(this.getVulnDescription());
		detail.append(System.lineSeparator()+System.lineSeparator());

		detail.append("Vuln Reference:");
		detail.append(System.lineSeparator());
		detail.append(this.getReference());
		detail.append(System.lineSeparator()+System.lineSeparator());

		return detail.toString();
	}

	public static void main(String args[]) {
		String file1 = "D:\\github\\CVE-2019-3396_EXP\\RCE_exp.py";
		LineEntry entry = new LineEntry(file1);
		System.out.println(entry.ToJson());
	}
}
