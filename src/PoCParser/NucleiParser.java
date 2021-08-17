package PoCParser;

import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.gson.Gson;

import PoC.LineEntry;

/**
 * 解析nuclei中的脚本，返回一个LineEntry对象。
 * https://github.com/projectdiscovery/nuclei-templates
 * @author bit4woo
 *
 */
public class NucleiParser {
	private static final Logger log=LogManager.getLogger(NucleiParser.class);

	public static LineEntry Parser(String pocfile) {
		LineEntry result = new LineEntry();
		File poc = new File(pocfile);
		if (poc.exists() && poc.isFile()) {
			try {
				result.setPocFileFullPath(pocfile);
				result.setPocfile(poc.getName());

				String content = FileUtils.readFileToString(poc);

				//解析yaml
				YamlBeanFromJson bean = yamlToBeanWithFastJson(pocfile);

				result.setCVE(bean.getId());

				YamlInfo info = bean.getInfo();
				result.setPocfile(info.getName());
				result.setAuthor(info.getAuthor());
				result.setSeverity(info.getSeverity());
				result.setVulnDescription(info.getDescription());
				result.setReference(info.getReference());
				result.setTags(info.getTags());
				
				result.setDetail(content);
				return result;
			}catch(Exception e) {
				log.error(e);
				result.setVulnDescription(poc.getName()+"parser error!");
				return result;
			}
		}
		return null;
	}

	/**
	 * 直接使用snakeyaml解析成Map对象，然后用map创建yamlInfo对象。
	 * @param input
	 * @return
	 * @throws Exception
	 */
	@Deprecated
	private static YamlInfo fromYaml1(String input) throws Exception {
		FileReader bbb = new FileReader(input);
		Yaml yaml = new Yaml();
		Map<String,Object> data = (Map<String,Object>)yaml.load(bbb);
		String id = (String)data.get("id");
		Map<String, Object> info = (Map<String, Object>) data.get("info");

		List<Object> requests = (List<Object>) data.get("requests");

		System.out.println(info.toString());
		System.out.println(requests.toString());

		YamlInfo yamlInfo = new YamlInfo(info);
		return yamlInfo;
	}

	@Deprecated
	private static YamlInfo fromYaml(String inputFile) throws Exception {
		FileReader bbb = new FileReader(inputFile);
		Yaml yaml = new Yaml(new Constructor(YamlBeanFromMap.class));
		YamlBeanFromMap data = yaml.load(bbb);
		String id = data.getId();
		Map<String, Object> info = data.getInfo();
		YamlInfo yamlInfo = new YamlInfo(info);
		return yamlInfo;

	}

	/**
	 * 使用了jackson
	 * @param yaml
	 * @return
	 * @throws Exception
	 */
	public static String convertYamlToJson(String yaml) throws Exception {
		ObjectMapper yamlReader = new ObjectMapper(new YAMLFactory());
		Object obj = yamlReader.readTree(new File(yaml));

		ObjectMapper jsonWriter = new ObjectMapper();
		String jsonstr = jsonWriter.writeValueAsString(obj);
		//System.out.println(jsonstr);
		return jsonstr;
	}

	/**
	 * 将yaml转为JSON格式
	 * 使用了snakeyaml和org.json
	 * @param yamlString
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Deprecated
	private static String convertToJson(String yamlString) throws Exception {
		Yaml yaml= new Yaml();
		Map<String,Object> map= (Map<String, Object>) yaml.load(new FileReader(yamlString));

		JSONObject jsonObject=new JSONObject(map);
		//System.out.println(jsonObject.toString());
		return jsonObject.toString();
	}

	/**
	 * 使用google json解析
	 * @param yamlFile
	 * @return
	 * @throws Exception
	 */
	@Deprecated //Expected a string but was BEGIN_ARRAY at line 1 column 351 path $.info.reference
	public static YamlBeanFromJson yamlToBeanWithGson(String yamlFile) throws Exception{
		String jsonStr = convertYamlToJson(yamlFile);
		YamlBeanFromJson tmp = new Gson().fromJson(jsonStr, YamlBeanFromJson.class);
		return tmp;
	}
	
	/**
	 * 使用fastjson解析
	 * @param yamlFile
	 * @return
	 * @throws Exception
	 */
	public static YamlBeanFromJson yamlToBeanWithFastJson(String yamlFile) throws Exception{
		String jsonStr = convertYamlToJson(yamlFile);
		YamlBeanFromJson tmp = JSON.parseObject(jsonStr, YamlBeanFromJson.class);
		return tmp;
	}

	public static void main (String[] args) throws Exception {
		yamlToBeanWithGson("C:\\Users\\P52\\nuclei-templates\\cves\\2014\\CVE-2014-2321.yaml");
		yamlToBeanWithFastJson("C:\\Users\\P52\\nuclei-templates\\cves\\2014\\CVE-2014-2321.yaml");
//		YamlBeanFromJson bean = yamlToBean("/Users/liwenjun/nuclei-templates/cves/2007/CVE-2007-4556.yaml");
//		int a=1;
	}
}
