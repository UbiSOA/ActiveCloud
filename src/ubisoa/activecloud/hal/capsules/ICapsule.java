package ubisoa.activecloud.hal.capsules;

import javax.swing.JPanel;

import org.jdom.Element;

public interface ICapsule {
	public JPanel getConfigUI();
	public void init(Element e) throws Exception;
	public void stop() throws Exception;
	public void start() throws Exception;
	public void send(byte[] data) throws Exception;
	public void subscribe(Object o) throws Exception;
}
