package httpbase;

import java.util.List;
import java.util.Map;

import com.github.kevinsawicki.http.HttpRequest;


public class HttpUtil {
	public static void main(String[] args) {
		request("get","https://rosetta.garenanow.com/transify/1366?lang=1",null,null);
	}
	
	/**
	 * 
	 * @param method http请求方法
	 * @param url 请求的URL地址，其实httpService就够了
	 * @param headers 请求头
	 * @param body 请求body
	 * @param proxy 使用的代理服务器 127.0.0.1:8080
	 * @param proxyBasic 代理服务器的账号密码 username:password
	 */
	public static byte[] makeRequest(String method,String url,Map<String, String> headers,byte[] body,String proxy,String proxyBasic){
		HttpRequest request;
		if (url == null) {
			return null;
		}
		
		if (method== null) {
			method = "GET";
		}else {
			method = method.toUpperCase();
		}
		
		request = new HttpRequest(url,method);
		
		if (headers != null) {
			request.headers(headers);
		}
		if (body != null) {
			request.send(body);
		}
		return response
		System.out.println(request.headers());
		System.out.println(request.bytes());
		System.out.println(request.body());
		
		
	};
}
