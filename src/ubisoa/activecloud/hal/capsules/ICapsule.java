package ubisoa.activecloud.hal.capsules;

import java.awt.Image;
import javax.swing.JPanel;
import org.jdom.Element;
import ubisoa.activecloud.exceptions.*;

public interface ICapsule {
	public JPanel getConfigUI();
	public void setConfigUI(JPanel configUI);
	public Image getIcon();
	public void setIcon(Image icon);
	public Element getConfigElement();
	public void setConfigElement(Element configElement);
	public void init(Element e) throws CapsuleInitException;
	public void stop() throws StopException;
	public void start() throws StartException;
	public void send(byte[] data) throws SendException;
	public void subscribe(Object o);
}
