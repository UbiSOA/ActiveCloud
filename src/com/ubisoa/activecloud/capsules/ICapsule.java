package com.ubisoa.activecloud.capsules;

import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import org.jdom.Element;

import com.ubisoa.activecloud.exceptions.CapsuleInitException;
import com.ubisoa.activecloud.exceptions.StartException;
import com.ubisoa.activecloud.exceptions.StopException;


public interface ICapsule {
	/*Common*/
	public JPanel getConfigUI();
	public void setConfigUI(JPanel configUI);
	public BufferedImage getIcon();
	public void setIcon(BufferedImage icon);
	public Element getConfigElement();
	public void setConfigElement(Element configElement);
	public void stop() throws StopException;
	public void start() throws StartException;
	public void init(Element e) throws CapsuleInitException;
}
