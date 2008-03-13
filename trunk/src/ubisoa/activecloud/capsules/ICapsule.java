package ubisoa.activecloud.capsules;

import java.awt.Image;

import javax.swing.JPanel;

import org.jdom.Element;

import ubisoa.activecloud.exceptions.CapsuleInitException;
import ubisoa.activecloud.exceptions.StartException;
import ubisoa.activecloud.exceptions.StopException;

public interface ICapsule {
	/*Common*/
	public JPanel getConfigUI();
	public void setConfigUI(JPanel configUI);
	public Image getIcon();
	public void setIcon(Image icon);
	public Element getConfigElement();
	public void setConfigElement(Element configElement);
	public void stop() throws StopException;
	public void start() throws StartException;
	public void init(Element e) throws CapsuleInitException;
}
