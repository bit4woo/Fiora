package httpbase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/** 
 * 解析响应数据包，构造成response对象
 * 
* @author bit4woo
* @github https://github.com/bit4woo 
* @version CreateTime：Jun 13, 2020 7:05:18 AM 
*/
public class Response {

	//解析后的内部变量
	private byte[] head;//不对外提供访问
	private byte[] body;
	
	//解析后的外部常用变量
	private int statusCode;
	private String mimeType;
	private List<String> headerList =new ArrayList<String>();
	private HashMap<String,String> headerMap = new HashMap<String,String>();

	public static void main(String args[]) {		

		String test = "HTTP/1.1 200 OK\r\n" + 
				"Date: Fri, 12 Jun 2020 14:59:39 GMT\r\n" + 
				"Content-Type: application/x-javascript\r\n" + 
				"Connection: close\r\n" + 
				"Last-Modified: Tue, 12 May 2020 05:59:21 GMT\r\n" + 
				"Vary: Accept-Encoding\r\n" + 
				"Expires: Fri, 12 Jun 2020 15:59:39 GMT\r\n" + 
				"Cache-Control: max-age=3600\r\n" + 
				"P3P: CP=\"CURa ADMa DEVa PSAo PSDo OUR BUS UNI PUR INT DEM STA PRE COM NAV OTC NOI DS\"\r\n" + 
				"Server: jfe\r\n" + 
				"Content-Length: 126867\r\n" + 
				"\r\n" + 
				"(function(a){var b={},c={};";
		new Response(test.getBytes());
	}

	/*
	 * 传入整个响应包，就像burp中见到的
	 */
	public Response(byte[] rawResponse) {
		parse(rawResponse);
	}
	
	public Response(int statusCode,HashMap<String,String> headerMap,byte[] body) {
		this.body = body;
		this.headerMap.clear();
		this.headerMap.putAll(headerMap);
		this.statusCode = statusCode;
		
		for (String key:headerMap.keySet()) {
			headerList.add(key+": "+headerMap.get(key).trim());
		}
		
		mimeType = headerMap.get("Content-Type");
		if (mimeType != null) {
			mimeType = mimeType.split(";")[0];
		}
	}

	private void parse(byte[] rawResponse) {
		int bodyOffset = findBodyOffset(rawResponse);
		System.out.println(bodyOffset);
		head = Arrays.copyOfRange(rawResponse, 0, bodyOffset-4);
		body = Arrays.copyOfRange(rawResponse, bodyOffset, rawResponse.length);//not length-1
		
		//System.arraycopy(rawResponse, 0, head, 0, bodyOffset-4);//0~bodyOffset-4-1
		//System.arraycopy(rawResponse, bodyOffset, body, 0, rawResponse.length-bodyOffset);

		//		System.out.println(new String(head));
		//		System.out.println();
		//		System.out.println();
		//		System.out.println(new String(body));

		String headString = new String(head);
		headerList = new ArrayList<String>(Arrays.asList(headString.split("\r\n")));
		ArrayList<String> headerListTmp = new ArrayList<String>();
		headerListTmp.addAll(headerList);

		String firstLine = headerListTmp.get(0);
		statusCode = Integer.parseInt(firstLine.split(" ")[1]);

		headerListTmp.remove(0);//移除第一行 Arrays.asList(arr) 转换的 List 并不能进行 add 和 remove 操作。

		for (String line:headerListTmp) {
			String[] keyAndValue = line.split(":",2);
			String key = keyAndValue[0];
			String value = keyAndValue[1].trim();
			headerMap.put(key, value);
		}


		mimeType = headerMap.get("Content-Type");
		if (mimeType != null) {
			mimeType = mimeType.split(";")[0];
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

	public List<String> getHeaders(){
		return headerList;
	}

	public HashMap<String, String> getHeaderMap() {
		return headerMap;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public String getMimeType() {
		return mimeType;
	}

	public byte[] getBody() {
		return body;
	}

}
