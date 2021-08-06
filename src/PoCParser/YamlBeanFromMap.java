package PoCParser;

import java.util.List;
import java.util.Map;

/**
 * 从Map
 * 
 * 对应nuclei的yaml poc文件的java bean
 * @author bit4woo
 *
 */
@Deprecated
public class YamlBeanFromMap {

	String id;
	Map<String,Object> info;//实际上转为了map
	//List<YamlRequests> requests;//这部分的内容太复杂，难以自动转换为对象。

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public Map<String, Object> getInfo() {
		return info;
	}
	
	public void setInfo(Map<String, Object> info) {
		this.info = info;
	}
//	public List<YamlRequests> getRequests() {
//		return requests;
//	}
//	public void setRequests(List<YamlRequests> requests) {
//		this.requests = requests;
//	}
}
