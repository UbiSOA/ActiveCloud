package com.divinesoft.activecloud.capsules;

import java.awt.Font;
import java.util.List;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.EventListenerList;

import net.tinyos.tinysoa.common.Network;
import net.tinyos.tinysoa.common.Parameter;
import net.tinyos.tinysoa.common.Reading;

import org.apache.log4j.Logger;
import org.jdom.Element;

import com.divinesoft.activecloud.events.NetworkEvent;
import com.divinesoft.activecloud.events.NetworkEventListener;
import com.divinesoft.activecloud.events.ParameterEvent;
import com.divinesoft.activecloud.events.ParameterEventListener;
import com.divinesoft.activecloud.events.ReadingEvent;
import com.divinesoft.activecloud.events.ReadingEventListener;
import com.ubisoa.activecloud.capsules.NotificationCapsule;
import com.ubisoa.activecloud.exceptions.ReceiveException;
import com.ubisoa.activecloud.exceptions.StartException;
import com.ubisoa.activecloud.exceptions.StopException;

public class TinySOANotification extends NotificationCapsule{
	private EventListenerList readingListeners = new EventListenerList();
	private EventListenerList networkListeners = new EventListenerList();
	private EventListenerList parameterListeners = new EventListenerList();
	private static final String READING = "reading";
	private static final String NETWORK = "network";
	private static final String PARAMETER = "parameter";
	private static Logger log = Logger.getLogger(TinySOANotification.class);
	
	@SuppressWarnings("unchecked")
	public void receive(Element payload) throws ReceiveException {
		log.debug(getClass().getName() + "'s receive called");
		//See what's the payload type
		String type = payload.getAttributeValue("type");
		List<Element> entries = payload.getChildren("entry");
		if(type.equals(READING)){
			Vector<Reading> readings = new Vector<Reading>();
			
			for(Element e : entries){
				Reading r = new Reading();
				r.setDateTime(e.getAttributeValue("readingtime"));
				r.setNid(Integer.parseInt(e.getAttributeValue("nodeid")));
				r.setParameter(e.getAttributeValue("name"));
				r.setValue(e.getAttributeValue("value"));
				readings.add(r);
			}
			fireReadingEvent(new ReadingEvent(this,readings));			
		}else if(type.equals(NETWORK)){
			Vector<Network> networks = new Vector<Network>();
			for(Element e : entries){
				Network n = new Network();
				n.setDescription(e.getAttributeValue("description"));
				n.setId(Integer.parseInt(e.getAttributeValue("id")));
				n.setName(e.getAttributeValue("name"));
				n.setWsdl(e.getAttributeValue("wsdl"));
				networks.add(n);
			}
			fireNetworkEvent(new NetworkEvent(this,networks));
		}else if(type.equals(PARAMETER)){
			Vector<Parameter> parameters = new Vector<Parameter>();
			for(Element e : entries){
				Parameter p = new Parameter();
				p.setDescription(e.getAttributeValue("description"));
				p.setName(e.getAttributeValue("name"));
				parameters.add(p);
			}
			fireParameterEvent(new ParameterEvent(this,parameters));
		}
	}

	public void start() throws StartException {
		
	}

	public void stop() throws StopException {
		
	}
	
	@Override
	public JPanel getConfigUI(){
		JPanel configPanel = new JPanel();
		JTextArea text = new JTextArea();
		text.setWrapStyleWord(true);
		JScrollPane scroll = new JScrollPane(text);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		configPanel.add(scroll);
		//configPanel.setPreferredSize(new Dimension(400,300));
		text.setEditable(false);
		text.setFont(new Font(Font.SERIF, Font.PLAIN, 18));
		text.setText("TinySOANotification raises an event when a TinySOA reading has completed.\n" +
				"To use: register TinySOANotification as an observer for a TinySOA capsule, and in\n" +
				"your own code register for the events fired by TinySOANotification.\n" +
				"TinySOANotification passes a Vector with the original data that came from TinySOA.");
		return configPanel;
	}
	
	public void addReadingEventListener(ReadingEventListener evt){
		readingListeners.add(ReadingEventListener.class, evt);
	}
	
	public void addNetworkEventListener(NetworkEventListener evt){
		networkListeners.add(NetworkEventListener.class, evt);
	}
	
	public void addParameterEventListener(ParameterEventListener evt){
		parameterListeners.add(ParameterEventListener.class, evt);
	}
	
	public void removeReadingEventListener(ReadingEventListener evt){
		readingListeners.remove(ReadingEventListener.class, evt);
	}
	
	public void removeNetworkEventListener(NetworkEventListener evt){
		networkListeners.remove(NetworkEventListener.class, evt);
	}
	
	public void removeParameterEventListener(ParameterEventListener evt){
		parameterListeners.remove(ParameterEventListener.class, evt);
	}
	
	protected void fireReadingEvent(ReadingEvent evt){
		Object[] registeredListeners = readingListeners.getListenerList();
		for(int i=0; i<registeredListeners.length; i+=2){
			if(registeredListeners[i] == ReadingEventListener.class){
				((ReadingEventListener)registeredListeners[i+1]).readingReceived(evt);	
			}
		}
	}
	
	protected void fireNetworkEvent(NetworkEvent evt){
		Object[] registeredListeners = networkListeners.getListenerList();
		for(int i=0; i<registeredListeners.length; i+=2){
			if(registeredListeners[i] == NetworkEventListener.class){
				((NetworkEventListener)registeredListeners[i+1]).networkListReceived(evt);
			}
		}
	}
	
	protected void fireParameterEvent(ParameterEvent evt){
		Object[] registeredListeners = networkListeners.getListenerList();
		for(int i=0; i<registeredListeners.length; i+=2){
			if(registeredListeners[i] == ParameterEventListener.class){
				((ParameterEventListener)registeredListeners[i+1]).parameterListReceived(evt);
			}
		}
	}

}
