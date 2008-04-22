package com.ubisoa.activecloud.capsules;

import java.util.HashMap;
import java.util.List;

import org.jdom.Element;

import com.ubisoa.activecloud.exceptions.ActionInvokeException;

public abstract class Action{
	private String description;
	private String name;
	private HashMap<String, String> configValues;
	
	public Action(String name, String description){
		this.name = name;
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description){
		this.description = description;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	@Override
	public String toString(){
		return name +": " + description;
	}
	
	/**Set the variables effective for all succesive calls of this action*/
	@SuppressWarnings("unchecked")
	public void setVariables(Element e){
		if(configValues == null){
			configValues = new HashMap<String, String>();
		}
		for(Element key : (List<Element>)e.getChildren()){
			String name = key.getAttributeValue("name");
			String value = key.getAttributeValue("value");
			configValues.put(name.toLowerCase(), value);
		}
	}
	
	/**Set the variables effective for all succesive calls of this action*/
	public void setVariable(String key, String value){
		if(configValues == null){
			configValues = new HashMap<String, String>();
		}
		configValues.put(key.toLowerCase(), value);
	}
	
	public void setVariables(String[] keys, String[] values){
		if(configValues == null){
			configValues = new HashMap<String, String>();
		}
		if(keys.length == values.length){
			for(int i=0; i<keys.length; i++){
				configValues.put(keys[i].toLowerCase(), values[i]);
			}
		}
	}
	
	public String get(String key){
		return configValues.get(key);
	}
	
	public abstract void run(Element e) throws ActionInvokeException;
}
