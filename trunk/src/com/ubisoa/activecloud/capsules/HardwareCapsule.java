package com.ubisoa.activecloud.capsules;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;

import org.jdom.Element;

import com.ubisoa.activecloud.exceptions.CapsuleInitException;
import com.ubisoa.activecloud.exceptions.StartException;
import com.ubisoa.activecloud.exceptions.StopException;


public abstract class HardwareCapsule implements ICapsule{
	protected JPanel configUI;
	protected BufferedImage icon = null;
	protected Element configElement = new Element("config");
	protected ArrayList<Action> actions;
	
	public HardwareCapsule(){
		actions = new ArrayList<Action>();
	}
	
	public void init(Element e) throws CapsuleInitException{
		Element config = e.getChild("config");
		if(config != null){
			setConfigElement(e.getChild("config"));	
		}
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
	
	@Override
	public String toString(){
		return this.getClass().getSimpleName();
	}
	
	/*Hardware specific*/
	public ArrayList<Action> getActions() {
		return actions;
	}
	
	public void addAction(Action action){
		actions.add(action);
	}
	
	/*From ICapsule*/
	public abstract void stop() throws StopException;
	public abstract void start() throws StartException;
	
}
