package run;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.apache.commons.io.FileUtils;

import GUI.MainGUI;
import PoC.PoCPanel;

public class RunNucleiAction {

	public PrintWriter stdout;
	public PrintWriter stderr;

	public static String genPara(String paraKey, String paraValue) {
		if (paraKey == null || paraValue == null) {
			return "";
		}
		paraKey = paraKey.trim();
		paraValue = paraValue.trim();
		if (paraValue.contains(" ")) {
			paraValue = "\"" + paraValue + "\"";
		}
		String result = paraKey + " " + paraValue;
		return result.trim();
	}

	public static String prepareTargets(List<String> targets) {
		if (targets.size() <= 0) {
			return "";
		} else if (targets.size() == 1) {
			return genPara("-u", targets.get(0));
		} else {
			try {
				File tmpTargets = new File("tmpTargets.txt");
				FileUtils.writeByteArrayToFile(tmpTargets, String.join(System.lineSeparator(), targets).getBytes());
				String targetpath = tmpTargets.getAbsolutePath();
				return genPara("-l", targetpath);
			} catch (IOException e) {
				e.printStackTrace();
				return "";
			}
		}
	}

	public static String prepareProxy() {
		String proxy = MainGUI.getGlobalConfig().fetchHttpProxy();
		if (proxy.length() >= "http://1.0.0.1:80".length()) {
			return genPara("-proxy", proxy);
		}
		return "";
	}
	
	public static String genCommandRunAll(List<String> targets) {
		try {
			String para = "";
			String target = prepareTargets(targets);
			String proxy = prepareProxy();
			
			if (targets.toString().toLowerCase().contains("http://")
					|| targets.toString().toLowerCase().contains("https://")) {
				para = target+" -as "+proxy; //-as自动进行指纹识别后扫描
			}

			String command = TerminalExec.genCmd(null, "nuclei", para);
			return command;
		} catch (Exception e1) {
			e1.printStackTrace();
			return null;
		}
	}
	

	public static String genCommand(List<String> targets, String poc) {
		try {
			String para = "";
			poc = genPara("-t",poc);
			String target = prepareTargets(targets);
			String proxy = prepareProxy();
			para = poc+" "+target;
			
			if (targets.toString().toLowerCase().contains("http://")
					|| targets.toString().toLowerCase().contains("https://")) {
				para = para+" "+proxy;
			}

			String command = TerminalExec.genCmd(null, "nuclei", para);
			return command;
		} catch (Exception e1) {
			e1.printStackTrace();
			return null;
		}
	}

	public static String genWorkflowCommand(List<String> targets, String poc) {
		try {
			String para = "";
			poc = genPara("-w",poc);
			String target = prepareTargets(targets);
			String proxy = prepareProxy();
			para = poc+" "+target;
			
			if (targets.toString().toLowerCase().contains("http://")
					|| targets.toString().toLowerCase().contains("https://")) {
				para = para+" "+proxy;
			}
			
			String command = TerminalExec.genCmd(null, "nuclei", para);
			return command;
		} catch (Exception e1) {
			e1.printStackTrace();
			return null;
		}
	}

	public static String genTagsCommand(List<String> targets, String tags) {
		try {
			
			String para = "";
			String poc = genPara("-tags",tags);
			String target = prepareTargets(targets);
			String proxy = prepareProxy();
			para = poc+" "+target;
			
			if (targets.toString().toLowerCase().contains("http://")
					|| targets.toString().toLowerCase().contains("https://")) {
				para = para+" "+proxy;
			}

			String command = TerminalExec.genCmd(null, "nuclei", para);
			return command;
		} catch (Exception e1) {
			e1.printStackTrace();
			return null;
		}
	}

	public static void run(String command) {
		try {
			boolean useRobot = (PoCPanel.rdbtnUseRobotInput.isSelected());
			if (useRobot) {
				RobotInput.startCmdConsole();// 尽早启动减少出错概率
			}

			RobotInput ri = new RobotInput();
			if (useRobot) {
				ri.inputString(command);
			} else {
				String batFile = TerminalExec.genBatchFile(command, "Nuclei_cmd.bat");
				//避免和可执行文件同名
				TerminalExec.runBatchFile(batFile);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public static void main(String[] args) {
	}
}
