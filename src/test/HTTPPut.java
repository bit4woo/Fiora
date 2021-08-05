package test;

import java.util.List;
import java.util.Map;

import com.github.kevinsawicki.http.HttpRequest;


public class HTTPPut {
	public static void main(String[] args) {
		request("get","https://rosetta.garenanow.com/transify/1366?lang=1",null,null);
		String aa= 'aaa';
		System.out.println(aa);
	}
	
	public static void request(String method,String url,Map<String, String> headers,byte[] body){
		HttpRequest request;
		if (url == null) {
			return;
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
		System.out.println(request.headers());
		System.out.println(request.bytes());
		System.out.println(request.body());
		
		
	};
}
