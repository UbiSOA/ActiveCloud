package com.ubisoa.activecloud.services;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.ubisoa.activecloud.capsules.IRule;
import com.ubisoa.activecloud.capsules.NotificationCapsule;

public class DeliveryLink {
	private static Logger log = Logger.getLogger(DeliveryLink.class);
	private NotificationCapsule nc;
	private ArrayList<IRule> rules;
	private Element config;
	
	public Element getConfig() {
		return config;
	}

	public void setConfig(Element config) {
		this.config = config;
	}

	public DeliveryLink(NotificationCapsule nc){
		this.nc = nc;
		this.rules = new ArrayList<IRule>();
		this.config = nc.getConfigElement();
	}
	
	public NotificationCapsule getNotificationCapsule(){
		return nc;
	}
	
	public ArrayList<IRule> getRules() {
		return rules;
	}
	
	public void addRule(IRule rule){
		rules.add(rule);
	}
	
	public void clearRules(){
		rules.clear();
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		String name = nc.getClass().getName();
		sb.append(name.substring(name.lastIndexOf('.')+1)+"\n");
		XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
		sb.append(out.outputString(config));
		log.debug(out.outputString(config));
		return sb.toString();
	}
}