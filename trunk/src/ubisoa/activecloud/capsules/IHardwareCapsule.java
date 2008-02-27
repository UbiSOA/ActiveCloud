package ubisoa.activecloud.capsules;

import java.awt.Image;
import javax.swing.JPanel;
import org.jdom.Element;
import ubisoa.activecloud.exceptions.*;

public interface IHardwareCapsule {
	public JPanel getConfigUI();
	public void setConfigUI(JPanel configUI);
	public Image getIcon();
	public void setIcon(Image icon);
	public Element getConfigElement();
	public int getId();
	public void setConfigElement(Element configElement);
	public void init(Element e, int id) throws CapsuleInitException;
	public void stop() throws StopException;
	public void start() throws StartException;
	public void send(Element data) throws SendException;
}
