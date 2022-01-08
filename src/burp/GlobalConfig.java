package burp;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;

import GUI.OptionsPanel;
import PoC.PoCPanel;
import PoC.search.History;


public class GlobalConfig {
	private static final String localdir = 
			System.getProperty("user.home")+File.separator+".Fiora";
	public static final String defaultPoCRootPath = 
			System.getProperty("user.home")+File.separator+"nuclei-templates";
	private String poctRootPath = defaultPoCRootPath;
	private String targets = "";
	private History searchHistory;
	private String proxy = "http://127.0.0.1";

	public String getPoctRootPath() {
		return poctRootPath;
	}

	public void setPoctRootPath(String poctRootPath) {
		this.poctRootPath = poctRootPath;
	}

	public String getTargets() {
		return targets;
	}

	public void setTargets(String targets) {
		this.targets = targets;
	}

	public History getSearchHistory() {
		return searchHistory;
	}

	public void setSearchHistory(History searchHistory) {
		this.searchHistory = searchHistory;
	}

	public String saveToDisk() {
		File localFile = new File(localdir+File.separator+"config.json");
		try {
			searchHistory = History.getInstance();
			FileUtils.write(localFile, this.ToJson());
			BurpExtender.getStdout().println("Saving Config To Disk");
			return localFile.toString();
		} catch (IOException e) {
			e.printStackTrace();
			e.printStackTrace(BurpExtender.getStderr());
			return null;
		}
	}

	public static GlobalConfig loadFromDisk() {
		File localFile = new File(localdir+File.separator+"config.json");
		return loadFromDisk(localFile.toString());
	}

	public static GlobalConfig loadFromDisk(String projectFile) {
		try {
			File localFile = new File(projectFile);
			if (localFile.exists()) {
				String jsonstr = FileUtils.readFileToString(localFile);
				GlobalConfig config = FromJson(jsonstr);
				return config;
			}
		} catch (IOException e) {
			e.printStackTrace();
			e.printStackTrace(BurpExtender.getStderr());
		}
		return new GlobalConfig();
	}

	public static void showConfigToUI(GlobalConfig config) {
		PoCPanel.getTitleTable().getTextAreaTarget().setText(config.getTargets());
		OptionsPanel.getNucleiTemplatesPath().setText(config.getPoctRootPath());
		History.setInstance(config.getSearchHistory());
	}

	public static void showConfigToUI() {
		showConfigToUI(loadFromDisk());
	}

	public String ToJson() {
		return new Gson().toJson(this);
	}

	public  static GlobalConfig FromJson(String instanceString) {
		return new Gson().fromJson(instanceString, GlobalConfig.class);
	}

	public String getProxy() {
		return proxy;
	}

	public void setProxy(String proxy) {
		this.proxy = proxy;
	}

	public String fetchHttpProxy() {
		String result = proxy;
		if (result.length()<8){
			return "";
		}
		if (!result.toLowerCase().startsWith("http://") && !result.toLowerCase().startsWith("https://")) {
			result = "http://"+result;
		}
		return result;
	}

	public String fetchSocketProxy() {
		String result = proxy;
		if (result.toLowerCase().startsWith("http://")) {
			result = result.replaceFirst("http://", "");
		}
		if (result.toLowerCase().startsWith("https://")) {
			result = result.replaceFirst("https://", "");
		}
		return result;
	}

}
