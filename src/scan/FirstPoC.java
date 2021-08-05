package scan;

import java.util.List;
import java.util.Map;

import com.github.kevinsawicki.http.HttpRequest;

import burp.IBurpCollaboratorClientContext;
import burp.IBurpExtenderCallbacks;
import burp.IHttpRequestResponse;
import burp.IScannerInsertionPoint;
import burp.j2ee.CustomScanIssueForColla;

/**
 * 有三种输入格式：host+port\url\burp数据包
 * 核心是URL，host+port构造成URL
 * Burp数据包，解析成URL+headers+body
 * 
 * 检测思路：
 * 1、仅根据URL进行判断
 * 2、仅根据版本进行判断
 * 3、漏洞无害利用PoC
 * 
 * 
 * 运行场景：
 * 1、命令行运行
 * 2、独立GUI运行
 * 3、集成到burp中运行，作为扫描插件！
 * 要做到以上三点,PoC的编写必须不依赖burp的方法。
 */

/*
public interface FirstPoC {
	public List<issue> ScanHostAndPort(){
		return ScanURL();
	}
	public List<issue> ScanURL(String method,String url,Map<String, String> headers,byte[] body){
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
		
		request.
		HttpRequest.put(url).headers(headers).send(body);
	};
	
	public List<issue> scan(IBurpExtenderCallbacks callbacks,
            IHttpRequestResponse baseRequestResponse, 
            IScannerInsertionPoint insertionPoint,IBurpCollaboratorClientContext collabContext);
}
public interface CollaModule {
	
	//Do request and check the collaborator server to identify the issue.
	// pass the issue object to the collaborator
	    public List<CustomScanIssueForColla> scan(IBurpExtenderCallbacks callbacks,
	            IHttpRequestResponse baseRequestResponse, 
	            IScannerInsertionPoint insertionPoint,IBurpCollaboratorClientContext collabContext);
	    
	    //when CollaboratorThread instance find a interaction, call this method to add issue
		//public boolean handleCollaboratorInteraction(IBurpCollaboratorInteraction interaction);
		
		//if the id of interaction is equals to the current payloadid, then add scan issue. else, do nothing. 
		
		//more thinking:
		//Actually, this interface is no need, we can imply our class that use collaborator server base on IModule.
		//之前考虑scan函数需要传入CollaboratorThread对象，安装现有collaboratorThread的实现，完全不需要，返回类型也可以和IModule一样。扫描完了就return一个空的issue。
		//后续流程就完全交给CollaboratorThread对象了，它会主动去pull，然后尝试调用各个类的handleCollaboratorInteraction函数。没有使用collaborator的类，直接什么也不做就好了。
		//但为了逻辑更清醒，让程序少点循环，还是决定分开写了。
	    public String getScanLevel();
	   //3 level: host url insertpoint
	    String aa = '''sss''';
	}
*/