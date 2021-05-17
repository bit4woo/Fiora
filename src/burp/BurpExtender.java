package burp;

import java.awt.Component;
import java.io.PrintWriter;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import GUI.MainGUI;
import bsh.This;

public class BurpExtender implements IBurpExtender, ITab,IContextMenuFactory,IExtensionStateListener{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private static IBurpExtenderCallbacks callbacks;
	private static PrintWriter stdout;
	private static PrintWriter stderr;
	private static String ExtenderName = "Fiora";
	private static String Version =  This.class.getPackage().getImplementationVersion();
	private static String Author = "by bit4woo";
	private static String github = "https://github.com/bit4woo/Fiora";
	private static MainGUI gui;
	private static final Logger log=LogManager.getLogger(BurpExtender.class);

	public static PrintWriter getStdout() {
		//不同的时候调用这个参数，可能得到不同的值
		try{
			stdout = new PrintWriter(callbacks.getStdout(), true);
		}catch (Exception e){
			stdout = new PrintWriter(System.out, true);
		}
		return stdout;
	}

	public static PrintWriter getStderr() {
		try{
			stderr = new PrintWriter(callbacks.getStderr(), true);
		}catch (Exception e){
			stderr = new PrintWriter(System.out, true);
		}
		return stderr;
	}

	public static IBurpExtenderCallbacks getCallbacks() {
		return callbacks;
	}

	public static String getGithub() {
		return github;
	}

	public static MainGUI getGui() {
		return gui;
	}

	public static String getExtenderName() {
		return ExtenderName;
	}

	//name+version+author
	public static String getFullExtenderName(){
		return ExtenderName+" "+Version+" "+Author;
	}

	private IExtensionHelpers helpers;

	//插件加载过程中需要做的事
	@Override
	public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks)
	{
		BurpExtender.callbacks = callbacks;
		helpers = callbacks.getHelpers();

		getStdout();
		getStderr();
		stdout.println(getFullExtenderName());
		stdout.println(github);

		callbacks.setExtensionName(getFullExtenderName()); //插件名称
		callbacks.registerContextMenuFactory(this);
		callbacks.registerExtensionStateListener(this);

		stdout.println("saving PoC-T Path to config: "+MainGUI.poctRootPath);
		MainGUI.poctRootPath = BurpExtender.getCallbacks().loadExtensionSetting(BurpExtender.ExtenderName);
		gui = new MainGUI();
		SwingUtilities.invokeLater(new Runnable()
		{//create GUI
			public void run()
			{
				BurpExtender.callbacks.addSuiteTab(BurpExtender.this); //这里的BurpExtender.this实质是指ITab对象，也就是getUiComponent()中的contentPane.这个参数由GUI()函数初始化。
				//如果这里报java.lang.NullPointerException: Component cannot be null 错误，需要排查contentPane的初始化是否正确。
			}
		});
	}

	//ITab必须实现的两个方法
	@Override
	public String getTabCaption() {
		return (ExtenderName);
	}
	@Override
	public Component getUiComponent() {
		return gui.getContentPane();
	}

	@Override
	public List<JMenuItem> createMenuItems(IContextMenuInvocation invocation) {
		return new MenuForBurp().createMenuItemsForBurp(invocation);
	}

	@Override
	public void extensionUnloaded() {
		BurpExtender.getCallbacks().saveExtensionSetting(BurpExtender.ExtenderName, MainGUI.poctRootPath);
		stdout.println("saving PoC-T Path to config: "+MainGUI.poctRootPath);
	}
}