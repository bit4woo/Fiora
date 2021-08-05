package PoCParser;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import PoC.LineEntry;

/**
 * 解析PoC-T中的脚本，返回一个LineEntry对象。
 * @author liwenjun
 *
 */
public class PoctParser {
	private static final Logger log=LogManager.getLogger(PoctParser.class);
	
	public LineEntry Parser(String pocfile) {
		LineEntry result = new LineEntry();
		File poc = new File(pocfile);
		if (poc.exists() && poc.isFile()) {
			try {
				result.setPocFileFullPath(pocfile);
				result.setPocfile(poc.getName());

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
						result.setAuthor(fetchValue(found));
					}
					if (found.startsWith("__CVE__")) {
						result.setCVE(fetchValue(found));
					}
					if (found.startsWith("__VulnApp__")) {
						result.setVulnApp(fetchValue(found));
					}
					if (found.startsWith("__VulnVersion__")) {
						result.setVulnVersion(fetchValue(found));
					}
					if (found.startsWith("__VulnURL__")) {
						result.setVulnURL(fetchValue(found));
					}
					if (found.startsWith("__VulnParameter__")) {
						result.setVulnParameter(fetchValue(found));
					}
					if (found.startsWith("__VulnType__")) {
						result.setVulnType(fetchValue(found));
					}
					if (found.startsWith("__VulnDescription__")) {
						result.setVulnDescription(fetchValue(found));
					}
					if (found.startsWith("__Reference__")) {
						result.setReference(fetchValue(found));
					}
					if (found.startsWith("__isPoCVerified__")) {
						result.setIsPoCVerified(fetchValue(found));
					}
				}
				return result;
			}catch(Exception e) {
				log.error(e);
			}
		}
		return null;
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
	
}
