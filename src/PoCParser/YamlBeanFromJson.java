package PoCParser;

/**
 * 对应nuclei的yaml poc文件的java bean
 * 
 * @author bit4woo
 *
 */
public class YamlBeanFromJson {

	String id;
	YamlInfo info;//实际上转为了map
	//List<YamlRequests> requests;//这部分的内容太复杂，难以自动转换为对象。

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public YamlInfo getInfo() {
		return info;
	}
	public void setInfo(YamlInfo info) {
		this.info = info;
	}
}
