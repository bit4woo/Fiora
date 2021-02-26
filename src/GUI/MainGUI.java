package GUI;

import java.awt.EventQueue;
import java.io.File;
import java.io.PrintWriter;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import PoC.PoCPanel;
import burp.BurpExtender;


public class MainGUI extends JFrame {

	public static PoCPanel pocPanel;
	public static File currentDBFile;
	protected PrintWriter stdout;
	protected PrintWriter stderr;
	public static String poctRootPath ="D:\\github\\POC-T";

	public static PoCPanel getPoCPanel() {
		return pocPanel;
	}

	public static File getCurrentDBFile() {
		return currentDBFile;
	}

	public static void setCurrentDBFile(File currentDBFile) {
		MainGUI.currentDBFile = currentDBFile;
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

		JTabbedPane tabbedWrapper = new JTabbedPane();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1174, 497);
		setContentPane(tabbedWrapper);
		pocPanel = new PoCPanel();
		tabbedWrapper.addTab("PoC", null, pocPanel, null);
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
