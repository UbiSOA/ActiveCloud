package com.divinesoft.activecloud.capsules;

import java.awt.FlowLayout;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.ubisoa.activecloud.capsules.NotificationCapsule;
import com.ubisoa.activecloud.exceptions.CapsuleInitException;
import com.ubisoa.activecloud.exceptions.ReceiveException;
import com.ubisoa.activecloud.exceptions.StartException;
import com.ubisoa.activecloud.exceptions.StopException;


public class WeatherReportCapsule extends NotificationCapsule{
	private static Logger log = Logger.getLogger(WeatherReportCapsule.class);
	private JPanel configUI;
	private String prefix;
	
	public void receive(byte[] payload) throws ReceiveException {
		log.debug("receive called, but not implemented");
	}

	public void receive(Element payload) throws ReceiveException {
		XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
		if(prefix != null){
			log.debug(prefix);
			log.debug(out.outputString(payload));
		}else{
			log.debug(out.outputString(payload));
		}
	}

	@Override
	public JPanel getConfigUI() {
		configUI = new JPanel(new FlowLayout());
		configUI.add(new JLabel("WeatherReportCapsule"));
		return configUI;
	}

	public void init(Element e) throws CapsuleInitException {

	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void setConfigElement(Element e){
		super.setConfigElement(e);
		String pre = null;
		
		for(Element key : (List<Element>)e.getChildren()){
			if(key.getAttributeValue("name").equals("prefix")){
				pre = key.getValue();
			}
		}
		
		if(pre != null){
			this.prefix = pre;
		}
	}

	public void start() throws StartException {

	}

	public void stop() throws StopException {

	}

}