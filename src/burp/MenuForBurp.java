package burp;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenuItem;

import PoC.PoCPanel;

public class MenuForBurp{

	public PrintWriter stderr = BurpExtender.getStderr();
	public static IExtensionHelpers helpers = BurpExtender.getCallbacks().getHelpers();
	public PrintWriter stdout = BurpExtender.getStdout();

	public List<JMenuItem> createMenuItemsForBurp(IContextMenuInvocation invocation) {
		List<JMenuItem> JMenuItemList = new ArrayList<JMenuItem>();

		JMenuItem sendhost = new JMenuItem("^_^ Send Host To Fiora");
		sendhost.addActionListener(new sendHostToFiora(invocation));

		JMenuItem sendurl = new JMenuItem("^_^ Send URL To Fiora");
		sendurl.addActionListener(new sendURLToFiora(invocation));

		JMenuItemList.add(sendhost);
		JMenuItemList.add(sendurl);
		return JMenuItemList;
	}

	public class sendHostToFiora implements ActionListener{
		private IContextMenuInvocation invocation;
		sendHostToFiora(IContextMenuInvocation invocation) {
			this.invocation  = invocation;
		}
		@Override
		public void actionPerformed(ActionEvent e)
		{
			try{
				List<String> hosts = new ArrayList<String>();
				IHttpRequestResponse[] messages = invocation.getSelectedMessages();
				for (IHttpRequestResponse message:messages) {
					message.getHttpService().getHost();
					hosts.add(message.getHttpService().getHost());
				}
				String xxx = String.join(System.lineSeparator(), hosts);
				PoCPanel.getTitleTable().getTextAreaTarget().setText(xxx);
			}
			catch (Exception e1)
			{
				e1.printStackTrace(stderr );
			}
		}
	}

	public class sendURLToFiora implements ActionListener{
		private IContextMenuInvocation invocation;
		sendURLToFiora(IContextMenuInvocation invocation) {
			this.invocation  = invocation;
		}
		@Override
		public void actionPerformed(ActionEvent e)
		{
			try{
				List<String> urls = new ArrayList<String>();
				Getter getter = new Getter(helpers);
				IHttpRequestResponse[] messages = invocation.getSelectedMessages();
				for (IHttpRequestResponse message:messages) {
					URL url = getter.getFullURL(message);
					urls.add(url.toString());
				}
				String xxx = String.join(System.lineSeparator(), urls);
				PoCPanel.getTitleTable().getTextAreaTarget().setText(xxx);
			}
			catch (Exception e1)
			{
				e1.printStackTrace(stderr );
			}
		}
	}
}
