package httpbase;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
/**
 * 自己实现的http请求执行方法，不再使用
 * 
 *
 */
@Deprecated
public class DoRequest {

	public static void main(String[] args) {
		try {
			//String httpservice = "http://www.faithfulfitnessforlife.com";
			String raws = "GET /fitness_blog/Captcha.aspx HTTP/1.1\r\n" + 
					"Cookie: ASP.NET_SessionId=xisp1kz0ttyhet55dhdjbe55\r\n" + 
					"Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8\r\n" + 
					"Upgrade-Insecure-Requests: 1\r\n" + 
					"User-Agent: Mozilla/5.0 (Windows NT 10.0; WOW64; rv:60.0) Gecko/20100101 Firefox/60.0\r\n" + 
					"Connection: close\r\n" + 
					"Accept-Language: zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2\r\n" + 
					"Accept-Encoding: gzip, deflate\r\n" + 
					"Host: www.faithfulfitnessforlife.com\r\n" + 
					"\r\n" + 
					"";
			String httpservice = "https://oms.meizu.com:8443";
			String raws2 = "GET /cas/captcha.htm HTTP/1.1\r\n" + 
					"Host: oms.meizu.com:8443\r\n" + 
					"User-Agent: Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:60.0) Gecko/20100101 Firefox/60.0\r\n" + 
					"Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8\r\n" + 
					"Accept-Language: en-US,en;q=0.5\r\n" + 
					"Accept-Encoding: gzip, deflate\r\n" + 
					"Connection: close\r\n" + 
					"Upgrade-Insecure-Requests: 1\r\n" + 
					"\r\n" + 
					"";
			String httpservice3 = "https://180.76.176.167";
			String raws3 ="GET /common/imgCode HTTP/1.1\r\n" + 
					"Host: 180.76.176.167\r\n" + 
					"User-Agent: Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:60.0) Gecko/20100101 Firefox/60.0\r\n" + 
					"Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8\r\n" + 
					"Accept-Language: en-US,en;q=0.5\r\n" + 
					"Accept-Encoding: gzip, deflate\r\n" + 
					"Cookie: JSESSIONID=8101DC75B8291044D12D1E1BEE1C8C9F; _jfinal_captcha=4b286d068bff44b2a88026199883ea7c\r\n" + 
					"Connection: close\r\n" + 
					"Upgrade-Insecure-Requests: 1\r\n" + 
					"Cache-Control: max-age=0\r\n" + 
					"\r\n" + 
					"";
			String proxy = "http://127.0.0.1:8080";
			System.out.println(new DoRequest().makeRequest(httpservice,raws2.getBytes(),proxy));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 *像burp一样发送请求，返回response对象
	 */
	public static Response makeRequest(String httpService,byte[] request,String proxy) throws Exception {
		Request req = new Request(httpService,request);
		//String proxy = "http://127.0.0.1:8080";
		if (req.getProtocol().equalsIgnoreCase("https")) {
			return makeHttpsRequest(req,proxy);
		}else {
			return makeHttpRequest(req,proxy);
		}
	}

	private static byte[] readInputStream(InputStream inStream) throws Exception{  
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();  
		byte[] buffer = new byte[1024];  
		int len = 0;
		while( (len=inStream.read(buffer)) != -1 ){  
			outStream.write(buffer, 0, len);  
		}  
		inStream.close();  
		return outStream.toByteArray();  
	}

	/*
	 * 发送请求，返回response对象,http
	 */
	private static Response makeHttpRequest(Request req,String proxyUrl) throws Exception {

		URL httpUrl = new URL(req.getUrl());
		//Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 8080));
		//HttpURLConnection conn = (HttpURLConnection)url.openConnection(proxy);
		HttpURLConnection conn;
		if (proxyUrl == null) {
			conn = (HttpURLConnection) httpUrl.openConnection();   //Connection reset error when certification is not match

			//可以使用HttpURLConnection请求https的链接，但是忽略证书时会出错。
		}else {
			URL url = new URL(proxyUrl);
			Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(url.getHost(), url.getPort()));
			conn = (HttpURLConnection) httpUrl.openConnection(proxy);
		}

		for (Map.Entry<String, String> entry : req.getHeaderMap().entrySet()) {
			conn.addRequestProperty(entry.getKey(),entry.getValue());
		}
		conn.setRequestMethod(req.getMethod());
		conn.setConnectTimeout(5 * 1000);
		conn.setReadTimeout(10*1000);

		if (req.getBody() != null) {
			conn.setDoOutput(true);
			DataOutputStream out = new DataOutputStream(conn.getOutputStream()); 
			out.write(req.getBody());
			out.flush(); 
			out.close();
		}

		InputStream inStream = conn.getInputStream();//InputStream的内容对应的是response的body部分

		HashMap<String,String> headerMap = new HashMap<String,String>();
		for (String key: conn.getHeaderFields().keySet()) {
			List<String> value = conn.getHeaderFields().get(key);
			String val = value.toString().substring(1,value.toString().length()-1);
			//System.out.println("aaa"+val);
			headerMap.put(key, val);
		}
		int code = conn.getResponseCode();
		byte[] body = readInputStream(inStream);

		Response resp = new Response(code,headerMap,body);
		return resp;
	}


	private static Response makeHttpsRequest(Request req,String proxyUrl) throws Exception {

		HostnameVerifier allHostsValid = new HostnameVerifier() {
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		};
		URL httpsUrl = new URL(req.getUrl());

		TrustManager[] tm = new TrustManager[]{new MyX509TrustManager()};
		SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");    
		sslContext.init(null, tm, new java.security.SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
		HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);//do not check certification
		
		HttpsURLConnection conn;
		
		if (proxyUrl == null || proxyUrl == "") {
			conn = (HttpsURLConnection) httpsUrl.openConnection();   //Connection reset error when certification is not match

			//可以使用HttpURLConnection请求https的链接，但是忽略证书时会出错。
		}else {
			URL url = new URL(proxyUrl);
			Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(url.getHost(), url.getPort()));
			conn = (HttpsURLConnection) httpsUrl.openConnection(proxy);
		}


		for (Map.Entry<String, String> entry : req.getHeaderMap().entrySet()) {
			conn.addRequestProperty(entry.getKey(),entry.getValue());
		}
		conn.setRequestMethod(req.getMethod());
		conn.setConnectTimeout(5 * 1000);
		conn.setReadTimeout(10*1000);

		if (req.getBody() != null) {
			conn.setDoOutput(true);
			DataOutputStream out = new DataOutputStream(conn.getOutputStream()); 
			out.write(req.getBody());
			out.flush(); 
			out.close();
		}

		InputStream inStream = conn.getInputStream();//InputStream的内容对应的是response的body部分

		HashMap<String,String> headerMap = new HashMap<String,String>();
		for (String key: conn.getHeaderFields().keySet()) {
			List<String> value = conn.getHeaderFields().get(key);
			String val = value.toString().substring(1,value.toString().length()-1);
			//System.out.println("aaa"+val);
			headerMap.put(key, val);
		}
		int code = conn.getResponseCode();
		byte[] body = readInputStream(inStream);

		Response resp = new Response(code,headerMap,body);
		return resp;
	}
}