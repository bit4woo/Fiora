package burp;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import run.TerminalExec;
import run.Utils;

public class Commons {

	public static String set2string(Set<?> set){
		Iterator iter = set.iterator();
		StringBuilder result = new StringBuilder();
		while(iter.hasNext())
		{
			//System.out.println(iter.next());  		
			result.append(iter.next()).append("\n");
		}
		return result.toString();
	}

	public static boolean isResponseNull(IHttpRequestResponse message){
		try {
			int x = message.getResponse().length;
			return false;
		}catch(Exception e){
			//stdout.println(e);
			return true;
		}
	}

	public static boolean uselessExtension(String urlpath) {
		Set<String> extendset = new HashSet<String>();
		extendset.add(".gif");
		extendset.add(".jpg");
		extendset.add(".png");
		extendset.add(".css");//gif,jpg,png,css,woff
		extendset.add(".woff");
		Iterator<String> iter = extendset.iterator();
		while (iter.hasNext()) {
			if(urlpath.endsWith(iter.next().toString())) {//if no next(), this loop will not break out
				return true;
			}
		}
		return false;
	}



	public static boolean isValidIP (String ip) {
		if (ip.contains(":")) {//处理带有端口号的域名
			ip = ip.substring(0,ip.indexOf(":"));
		}

		try {
			if ( ip == null || ip.isEmpty() ) {
				return false;
			}

			String[] parts = ip.split( "\\." );
			if ( parts.length != 4 ) {
				return false;
			}

			for ( String s : parts ) {
				int i = Integer.parseInt( s );
				if ( (i < 0) || (i > 255) ) {
					return false;
				}
			}
			if ( ip.endsWith(".") ) {
				return false;
			}

			return true;
		} catch (NumberFormatException nfe) {
			return false;
		}
	}


	public static String getNowTimeString() {
		SimpleDateFormat simpleDateFormat = 
				new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		return simpleDateFormat.format(new Date());
	}


	public static void browserOpen(Object url,String browser) throws Exception{
		String urlString = null;
		URI uri = null;
		if (url instanceof String) {
			urlString = (String) url;
			uri = new URI((String)url);
		}else if (url instanceof URL) {
			uri = ((URL)url).toURI();
			urlString = url.toString();
		}
		if(browser == null ||browser.equalsIgnoreCase("default") || browser.equalsIgnoreCase("")) {
			//whether null must be the first
			Desktop desktop = Desktop.getDesktop();
			if(Desktop.isDesktopSupported()&&desktop.isSupported(Desktop.Action.BROWSE)){
				desktop.browse(uri);
			}
		}else {
			String[] cmdArray = new String[] {browser,urlString};

			//runtime.exec(browser+" "+urlString);//当命令中有空格时会有问题
			Runtime.getRuntime().exec(cmdArray);
		}
	}


	public static byte[] buildCookieRequest(IExtensionHelpers helpers,String cookie, byte[] request) {
		if (cookie != null && !cookie.equals("")){
			if (!cookie.startsWith("Cookie: ")){
				cookie = "Cookie: "+cookie;
			}
			List<String > newHeader = helpers.analyzeRequest(request).getHeaders();
			int bodyOffset = helpers.analyzeRequest(request).getBodyOffset();
			byte[] byte_body = Arrays.copyOfRange(request, bodyOffset, request.length);
			newHeader.add(cookie);
			request = helpers.buildHttpMessage(newHeader,byte_body);
		}
		return request;
	}


	public static List<Integer> Port_prompt(Component prompt, String str){
		String defaultPorts = "8080,8000,8443";
		String user_input = JOptionPane.showInputDialog(prompt, str,defaultPorts);
		if (null == user_input || user_input.trim().equals("")) return  null; 
		List<Integer> portList = new ArrayList<Integer>();
		for (String port: user_input.trim().split(",")) {
			int portint = Integer.parseInt(port);
			portList.add(portint);
		}
		return portList;
	}

	public static boolean isWindows() {
		String OS_NAME = System.getProperties().getProperty("os.name").toLowerCase();
		if (OS_NAME.contains("windows")) {
			return true;
		} else {
			return false;
		}
	}

	public static ArrayList<String> regexFind(String regex,String content) {
		ArrayList<String> result = new ArrayList<String>();
		Pattern pRegex = Pattern.compile(regex);
		Matcher matcher = pRegex.matcher(content);
		while (matcher.find()) {//多次查找
			result.add(matcher.group());
		}
		return result;
	}


	public static void writeToClipboard(String text) {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		StringSelection selection = new StringSelection(text);
		clipboard.setContents(selection, null);
	}

	/*
	 *将形如 https://www.runoob.com的URL统一转换为
	 * https://www.runoob.com:443/
	 * 
	 * 因为末尾的斜杠，影响URL类的equals的结果。
	 * 而默认端口影响String格式的对比结果。
	 */

	public static String formateURLString(String urlString) {
		try {
			//urlString = "https://www.runoob.com";
			URL url = new URL(urlString);
			String host = url.getHost();
			int port = url.getPort();
			String path = url.getPath();

			if (port == -1) {
				String newHost = url.getHost()+":"+url.getDefaultPort();
				urlString = urlString.replace(host, newHost);
			}

			if (path.equals("")) {
				urlString = urlString+"/";
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return urlString;
	}
	
	public static String getShortUrl(String urlString) {
		try {
			//urlString = "https://www.runoob.com";
			URL url = new URL(urlString);
			String procotol = url.getProtocol();
			String host = url.getHost();
			int port = url.getPort();

			if (port == -1) {
				port = url.getDefaultPort();
			}
			return procotol+"://"+host+port+"/";
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return urlString;
		}
	}

	public static List<String> getLinesFromTextArea(JTextArea textarea){
		//user input maybe use "\n" in windows, so the System.lineSeparator() not always works fine!
		String[] lines = textarea.getText().replaceAll("\r\n", "\n").split("\n");
		List<String> result = new ArrayList<String>();
		for(String line: lines) {
			line = line.trim();
			if (line!="") {
				result.add(line.trim());
			}
		}
		return result;
	}

	public static void openPoCFile(String filepath) {
		if (TerminalExec.isInEnvironmentPath("code.cmd")) {
			try {
				String[] cmdArray = new String[] {"code.cmd","\""+filepath+"\""};
				Runtime.getRuntime().exec(cmdArray);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else if (TerminalExec.isInEnvironmentPath("idle.bat")){
			try {
				String[] cmdArray = new String[] {"idle.bat","\""+filepath+"\""};
				Runtime.getRuntime().exec(cmdArray);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else {
			try {
				//JOptionPane.showMessageDialog(null,"Not found editor(code.exe idle.bat) in environment.");
				File file = new File(filepath);
				Utils.OpenFolder(file.getParent());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void main(String args[]) {
		openPoCFile("D:\\github\\POC-T\\script\\activemq-upload.py");
	}
}
