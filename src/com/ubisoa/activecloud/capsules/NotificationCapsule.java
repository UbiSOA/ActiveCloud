package com.ubisoa.activecloud.capsules;

import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import org.jdom.Element;

import com.ubisoa.activecloud.exceptions.CapsuleInitException;
import com.ubisoa.activecloud.exceptions.ReceiveException;


public abstract class NotificationCapsule implements ICapsule{
	protected JPanel configUI;
	protected BufferedImage icon;
	protected Element configElement = new Element("config");
	
	public void init(Element e) throws CapsuleInitException{
		setConfigElement(e.getChild("config"));
	}
	
	public JPanel getConfigUI(){
		return configUI;
	}
	
	public void setConfigUI(JPanel configUI){
		this.configUI = configUI;
	}
	
	public BufferedImage getIcon(){
		return icon;
	}
	
	public void setIcon(BufferedImage icon){
		this.icon = icon;
	}
	
	public Element getConfigElement(){
		return configElement;
	}
	
	public void setConfigElement(Element config){
		if(configElement != null){
			this.configElement = config;	
		}
	}
	
	@Override
	public String toString(){
		return getClass().getSimpleName();
	}
	
	/*Notification specific*/
	public abstract void receive(Element payload) throws ReceiveException;
}