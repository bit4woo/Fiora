package httpbase;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/** 
 * 解析httpService和请求的raw数据，构造成request对象
 * 
* @author bit4woo
* @github https://github.com/bit4woo 
* @version CreateTime：Jun 13, 2020 7:05:18 AM 
*/
public class Request {

	//解析后的常用变量
	private String host;
	private int port;
	private String protocol;

	private String method; //GET POST
	private String url;

	private HashMap<String,String> headerMap = new HashMap<String,String>();
	private ArrayList<String> headerList;
	private byte[] body;
	
	Request(String httpservice,byte[] rawRequest) throws Exception{
		parser(httpservice,rawRequest);
	}

	private void parser(String httpservice,byte[] rawRequest) throws Exception {

		URL serviceUrl = new URL(httpservice);
		host = serviceUrl.getHost();
		port = serviceUrl.getPort();
		protocol = serviceUrl.getProtocol();
		httpservice = protocol+"://"+host+":"+port;

		int bodyOffset = findBodyOffset(rawRequest);
		byte[] head = Arrays.copyOfRange(rawRequest, 0, bodyOffset-4);
		body = Arrays.copyOfRange(rawRequest, bodyOffset, rawRequest.length);//not length-1

		String headString = new String(head);
		//List<String> headerList = Arrays.asList(headString.split("\r\n"));
		headerList = new ArrayList<String>(Arrays.asList(headString.split("\r\n")));

		ArrayList<String> headerListTmp = new ArrayList<String>();
		headerListTmp.addAll(headerList);

		String firstLine = headerListTmp.get(0);
		url = firstLine.split(" ")[1];
		url= httpservice + url;
		method = firstLine.split(" ")[0];

		headerListTmp.remove(0);

		for (String line:headerListTmp) {
			String[] keyAndValue = line.split(":",2);
			String key = keyAndValue[0];
			String value = keyAndValue[1].trim();
			headerMap.put(key, value);
		}
	}

	private static int findBodyOffset(byte[] requestOrResponse) {
		for(int i =0;i<=requestOrResponse.length-4;i++) {
			byte[] item = {requestOrResponse[i],requestOrResponse[i+1],requestOrResponse[i+2],requestOrResponse[i+3]};
			//System.out.println("---"+new String(item)+"---");
			if (Arrays.equals(item,"\r\n\r\n".getBytes())) {
				return i+4;//指向body中的第一个字符
			}
		}
		return -1;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getProtocol() {
		return protocol;
	}

	public byte[] getBody() {
		return body;
	}

	public ArrayList<String> getHeaderList() {
		return headerList;
	}

	public String getUrl() {
		return url;
	}

	public String getMethod() {
		return method;
	}

	/**
	 * 不包含请求数据包的第一行
	 */
	public HashMap<String, String> getHeaderMap() {
		return headerMap;
	}
}
