package ubisoa.activecloud.hal.capsules;

import java.awt.Image;

import javax.swing.JPanel;

import org.jdom.Element;

public interface ICapsule {
	public JPanel getConfigUI();
	public void setConfigUI(JPanel configUI);
	public Image getIcon();
	public void setIcon(Image icon);
	public Element getConfigElement();
	public void setConfigElement(Element configElement);
	public void init(Element e) throws Exception;
	public void stop() throws Exception;
	public void start() throws Exception;
	public void send(byte[] data) throws Exception;
	public void subscribe(Object o) throws Exception;
}
