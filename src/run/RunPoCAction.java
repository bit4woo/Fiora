package run;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;

import org.apache.commons.io.FileUtils;

import GUI.MainGUI;
import PoC.PoCPanel;

public class RunPoCAction{

	public PrintWriter stdout;
	public PrintWriter stderr;

	public static void runWithPoCT(List<String> targets,String poc) {
		try{
			boolean useRobot = (PoCPanel.rdbtnUseRobotInput.isSelected());
			if (useRobot) {
				RobotInput.startCmdConsole();//尽早启动减少出错概率
			}

			String para = "";
			if (targets.size() <=0) {
				return;
			}else if (targets.size() ==1) {
				para = "-s "+poc.trim()+" -iS "+targets.get(0);
			}else {
				File tmpTargets = new File("tmpTargets.txt");
				FileUtils.writeByteArrayToFile(tmpTargets, String.join(System.lineSeparator(),targets).getBytes());
				para = "-s "+poc.trim()+" -iF "+tmpTargets.getAbsolutePath();
			}

			String poctPath = "PoC-T.py";
			if (new File(MainGUI.poctRootPath).exists()) {
				poctPath  = MainGUI.poctRootPath+File.separator+"PoC-T.py";
			}
			//POC-T.py -s cas-deser-RCE -iS 127.0.0.1

			RobotInput ri = new RobotInput();
			if (useRobot) {
				String command = RobotInput.genCmd("python",poctPath,para);
				ri.inputString(command);
			}else {
				TerminalExec exec = new TerminalExec(null,"poct-fiora.bat","python",poctPath,para);
				exec.run();
			}
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
		}
	}

	public static void run(String poc) {
		try{
			boolean useRobot = (PoCPanel.rdbtnUseRobotInput.isSelected());
			if (useRobot) {
				RobotInput.startCmdConsole();//尽早启动减少出错概率
			}

			RobotInput ri = new RobotInput();
			if (useRobot) {
				String command = RobotInput.genCmd("python",poc,"");
				ri.inputString(command);
			}else {
				TerminalExec exec = new TerminalExec(null,"poct-fiora.bat","python",poc,"");
				exec.run();
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
