package com.divinesoft.activecloud.capsules;

import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import ubisoa.activecloud.capsules.NotificationCapsule;
import ubisoa.activecloud.exceptions.CapsuleInitException;
import ubisoa.activecloud.exceptions.ReceiveException;
import ubisoa.activecloud.exceptions.StartException;
import ubisoa.activecloud.exceptions.StopException;

public class WeatherReportCapsule extends NotificationCapsule{
	private static Logger log = Logger.getLogger(WeatherReportCapsule.class);
	private JPanel configUI;
	
	public void receive(byte[] payload) throws ReceiveException {
		log.debug("receive called, but not implemented");
	}

	public void receive(Element payload) throws ReceiveException {
		XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
		log.debug(out.outputString(payload));
	}

	@Override
	public JPanel getConfigUI() {
		configUI = new JPanel(new FlowLayout());
		configUI.add(new JLabel("WeatherReportCapsule"));
		return configUI;
	}

	public void init(Element e) throws CapsuleInitException {

	}

	public void start() throws StartException {

	}

	public void stop() throws StopException {

	}

}
