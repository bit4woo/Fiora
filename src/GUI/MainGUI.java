package GUI;

import java.awt.EventQueue;
import java.io.PrintWriter;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;

import PoC.PoCPanel;
import burp.BurpExtender;
import burp.GlobalConfig;


public class MainGUI extends JFrame {

	public static PoCPanel pocPanel;
	public static OptionsPanel optionsPanel;
	protected PrintWriter stdout;
	protected PrintWriter stderr;
	private static GlobalConfig globalConfig;

	public static PoCPanel getPoCPanel() {
		return pocPanel;
	}

	public static GlobalConfig getGlobalConfig() {
		return globalConfig;
	}

	public static void setGlobalConfig(GlobalConfig globalConfig) {
		MainGUI.globalConfig = globalConfig;
	}

	/**
	 * Create the frame.
	 */
	public MainGUI() {//构造函数
		try{
			stdout = new PrintWriter(BurpExtender.getCallbacks().getStdout(), true);
			stderr = new PrintWriter(BurpExtender.getCallbacks().getStderr(), true);
		}catch (Exception e){
			stdout = new PrintWriter(System.out, true);
			stderr = new PrintWriter(System.out, true);
		}

		try {
			String lookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName();
			UIManager.setLookAndFeel(lookAndFeel);
		} catch (Exception e) {
			e.printStackTrace();
		}
		JTabbedPane tabbedWrapper = new JTabbedPane();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1174, 497);
		setContentPane(tabbedWrapper);

		globalConfig = GlobalConfig.loadFromDisk();

		pocPanel = new PoCPanel(globalConfig.getPoctRootPath());
		tabbedWrapper.addTab("PoC", null, pocPanel, null);
		optionsPanel = new OptionsPanel();
		tabbedWrapper.addTab("Options", null, optionsPanel, null);

		GlobalConfig.showConfigToUI(globalConfig);
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainGUI frame = new MainGUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
