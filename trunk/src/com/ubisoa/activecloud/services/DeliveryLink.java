package com.ubisoa.activecloud.services;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.ubisoa.activecloud.capsules.NotificationCapsule;

public class DeliveryLink {
	private static Logger log = Logger.getLogger(DeliveryLink.class);
	private NotificationCapsule nc;
	private Element config;
	
	public Element getConfig() {
		return config;
	}

	public void setConfig(Element config) {
		this.config = config;
	}

	public DeliveryLink(NotificationCapsule nc){
		this.nc = nc;
		this.config = nc.getConfigElement();
	}
	
	public NotificationCapsule getNotificationCapsule(){
		return nc;
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		String name = nc.getClass().getName();
		sb.append(name.substring(name.lastIndexOf('.')+1));
		if(config != null){
			XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
			log.debug(out.outputString(config));	
		}
		return sb.toString();
	}
}