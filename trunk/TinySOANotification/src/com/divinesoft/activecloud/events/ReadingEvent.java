package com.divinesoft.activecloud.events;

import java.util.EventObject;
import java.util.Vector;

import net.tinyos.tinysoa.common.Reading;

public class ReadingEvent extends EventObject{
	private Vector<Reading> readings;
	
	public ReadingEvent(Object source, Vector<Reading> readings) {
		super(source);
		this.readings = readings;
	}
	
	public Vector<Reading> getReadings(){
		return readings;
	}

	private static final long serialVersionUID = 1L;

}
