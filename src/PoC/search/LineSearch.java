package PoC.search;

import java.util.regex.Pattern;

import PoC.LineEntry;

public class LineSearch {
	public static boolean textFilte(LineEntry line,String keyword) {
		if (keyword.length() == 0) {
			return true;
		}else {//全局搜索
			if (line.ToJson().toLowerCase().contains(keyword)) {
				return true;
			}
			return false;
		}
	}

	public static boolean regexFilte(LineEntry line,String regex) {
		//BurpExtender.getStdout().println("regexFilte: "+regex);
		Pattern pRegex = Pattern.compile(regex);

		if (regex.trim().length() == 0) {
			return true;
		} else {
			if (pRegex.matcher(line.ToJson().toLowerCase()).find()) {
				return true;
			}
			return false;
		}
	}
	
	
	public static void main(String args[]) {
		String title = "标题";
		System.out.println(title.toLowerCase().contains("标题"));
		
//		String webpack_PATTERN = "app\\.([0-9a-z])*\\.js";//后文有小写转换
//		System.out.println(webpack_PATTERN);
//		
//		System.out.println("regex:app\\.([0-9a-z])*\\.js");
//		
//		System.out.println(SearchDork.REGEX.toString()+":"+webpack_PATTERN);
	}
}
