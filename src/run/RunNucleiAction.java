package run;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;

import org.apache.commons.io.FileUtils;

import GUI.MainGUI;
import PoC.PoCPanel;

public class RunNucleiAction{

	public PrintWriter stdout;
	public PrintWriter stderr;
	
	
	public static String genCommand(List<String> targets,String poc) {
		try{
			String para = "";
			poc = poc.trim();
			if (poc.contains(" ")) {
				poc = "\""+poc+"\"";
			}
			if (targets.size() <=0) {
				para = "--helps ";
			}else if (targets.size() ==1) {
				para = "-t "+poc+" -u "+targets.get(0);
			}else {
				File tmpTargets = new File("tmpTargets.txt");
				FileUtils.writeByteArrayToFile(tmpTargets, String.join(System.lineSeparator(),targets).getBytes());
				para = "-t "+poc+" -l "+tmpTargets.getAbsolutePath();
			}
			if (targets.toString().toLowerCase().contains("http://") || targets.toString().toLowerCase().contains("https://")) {
				String proxy = MainGUI.getGlobalConfig().fetchHttpProxy();
				if (proxy.length() >= "http://1.0.0.1:80".length()) {
					para = para + " -proxy "+ proxy;
				}
			}

			String command = TerminalExec.genCmd(null,"nuclei",para);
			return command;
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
			return null;
		}
	}
	
	public static String genWorkflowCommand(List<String> targets,String poc) {
		try{
			String para = "";
			poc = poc.trim();
			if (poc.contains(" ")) {
				poc = "\""+poc+"\"";
			}
			if (targets.size() <=0) {
				para = "--helps ";
			}else if (targets.size() ==1) {
				para = "-w "+poc+" -u "+targets.get(0);
			}else {
				File tmpTargets = new File("tmpTargets.txt");
				FileUtils.writeByteArrayToFile(tmpTargets, String.join(System.lineSeparator(),targets).getBytes());
				para = "-w "+poc+" -l "+tmpTargets.getAbsolutePath();
			}
			if (targets.toString().toLowerCase().contains("http://") || targets.toString().toLowerCase().contains("https://")) {
				String proxy = MainGUI.getGlobalConfig().fetchHttpProxy();
				para = para + " -proxy "+ proxy;
			}

			String command = TerminalExec.genCmd(null,"nuclei",para);
			return command;
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
			return null;
		}
	}
	
	public static String genTagsCommand(List<String> targets,String tags) {
		try{
			String para = "";
			if (targets.size() <=0) {
				para = "--helps ";
			}else if (targets.size() ==1) {
				para = "-tags "+tags.trim()+" -u "+targets.get(0);
			}else {
				File tmpTargets = new File("tmpTargets.txt");
				FileUtils.writeByteArrayToFile(tmpTargets, String.join(System.lineSeparator(),targets).getBytes());
				para = "-tags "+tags.trim()+" -l "+tmpTargets.getAbsolutePath();
			}
			if (targets.toString().toLowerCase().contains("http://") || targets.toString().toLowerCase().contains("https://")) {
				String proxy = MainGUI.getGlobalConfig().fetchHttpProxy();
				para = para + " -proxy "+ proxy;
			}
			

			String command = TerminalExec.genCmd(null,"nuclei",para);
			return command;
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
			return null;
		}
	}

	public static void run(String command) {
		try{
			boolean useRobot = (PoCPanel.rdbtnUseRobotInput.isSelected());
			if (useRobot) {
				RobotInput.startCmdConsole();//尽早启动减少出错概率
			}
			
			RobotInput ri = new RobotInput();
			if (useRobot) {
				ri.inputString(command);
			}else {
				String batFile = TerminalExec.genBatchFile(command, "Nuclei.bat");
				TerminalExec.runBatchFile(batFile);
			}
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
		}
	}

	public static void main(String[] args){
	}
}
