package com.divinesoft.activecloud.events;

import java.util.EventObject;
import java.util.Vector;

import net.tinyos.tinysoa.common.Parameter;

public class ParameterEvent extends EventObject{
	private Vector<Parameter> parameters;
	public ParameterEvent(Object source, Vector<Parameter> parameters) {
		super(source);
		this.parameters = parameters;
	}
	
	public Vector<Parameter> getParameters(){
		return parameters;
	}

}
