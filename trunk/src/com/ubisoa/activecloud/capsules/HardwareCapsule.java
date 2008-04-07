package com.ubisoa.activecloud.capsules;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JPanel;

import org.jdom.Element;

import com.ubisoa.activecloud.exceptions.CapsuleInitException;
import com.ubisoa.activecloud.exceptions.StartException;
import com.ubisoa.activecloud.exceptions.StopException;


public abstract class HardwareCapsule implements ICapsule{
	private JPanel configUI;
	private BufferedImage icon = null;
	private Element configElement = new Element("config");
	private ArrayList<IAction> actions;
	
	public HardwareCapsule(){
		actions = new ArrayList<IAction>();
	}
	
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
	
	public void setConfigElement(Element configElement){
		if(configElement != null){
			this.configElement = configElement;	
		}
	}
	
	/*Hardware specific*/
	public ArrayList<IAction> getActions() {
		return actions;
	}
	
	public void addAction(IAction action){
		actions.add(action);
	}
	
	/*From ICapsule*/
	public abstract void stop() throws StopException;
	public abstract void start() throws StartException;
	
}
