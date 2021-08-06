package PoCParser;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * 
 * 
 * @author bit4woo
 *
 */
public class YamlInfo {

	String name;
	String author;
	String severity;
	String description;
	String reference;
	String tags;

	public YamlInfo() {

	}

	public YamlInfo(String key,String value) {
		System.out.println(value);
	}

	/**
	 * 根据变量名称，找到对应的getter、setter函数。
	 * @param fieldName
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	public void callGetter(String key,Object value) throws Exception {
		Method[] methods = YamlInfo.class.getMethods();
		for(Method method : methods){
			if(method.getName().equalsIgnoreCase("get"+key)) {
				method.invoke(this,value);//user.getName();
			}
		}
	}
	
	/**
	 * 
	 * @param key
	 * @param value
	 * @throws Exception
	 */
	public void callSetter(String key,Object value) throws Exception {
		Method[] methods = YamlInfo.class.getMethods();
		for(Method method : methods){
			if(method.getName().equalsIgnoreCase("set"+key)) {
				method.invoke(this,value);//user.getName();
			}
		}
	}
	
	/**
	 * 根据map来创建当前对象
	 * @param info
	 */
	public YamlInfo(Map<String, Object> info) {
		for(String key : info.keySet()){
			try {
				callSetter(key,info.get(key));
			} catch (Exception e) {
				System.out.println(key);
				e.printStackTrace();
			}
		}
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getSeverity() {
		return severity;
	}
	public void setSeverity(String severity) {
		this.severity = severity;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getReference() {
		return reference;
	}
	public void setReference(String reference) {
		this.reference = reference;
	}
	public String getTags() {
		return tags;
	}
	public void setTags(String tags) {
		this.tags = tags;
	}

	@Override
	public String toString() {
		return "PocInfo [name=" + name + ", author=" + author + ", severity=" + severity + ", description="
				+ description + ", reference=" + reference + ", tags=" + tags + "]";
	}

}
