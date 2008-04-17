package com.divinesoft.activecloud.capsules;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JPanel;

import net.tinyos.tinysoa.common.Network;
import net.tinyos.tinysoa.common.Parameter;
import net.tinyos.tinysoa.common.Reading;
import net.tinyos.tinysoa.server.InfoServ;
import net.tinyos.tinysoa.server.NetServ;
import net.tinyos.tinysoa.server.TinySOAServer;

import org.apache.log4j.Logger;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.client.XFireProxyFactory;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.jdom.Attribute;
import org.jdom.Element;

import com.ubisoa.activecloud.capsules.Action;
import com.ubisoa.activecloud.capsules.HardwareCapsule;
import com.ubisoa.activecloud.exceptions.ActionInvokeException;
import com.ubisoa.activecloud.exceptions.CapsuleInitException;
import com.ubisoa.activecloud.exceptions.StartException;
import com.ubisoa.activecloud.exceptions.StopException;
import com.ubisoa.activecloud.services.DeliveryService;

public class TempCapsule extends HardwareCapsule{
	private static Logger log = Logger.getLogger(TempCapsule.class);
	private InfoServ infoServ = null;
	private NetServ netServ = null;
	private final String defaultTimestampFormat = "yyyy-MM-dd 'at' HH:mm:ss";
	private HashMap<String, String> configValues;
	private SimpleDateFormat timestamp;
	private Service infoServiceModel;
	private Service netServiceModel;
	
	public TempCapsule(){
		configValues = new HashMap<String, String>();
		//Try to format the timestamp according to what the user provided
		try{
			timestamp = new SimpleDateFormat(configValues.get("timestampformat"));
		}catch(NullPointerException npe){
			timestamp = new SimpleDateFormat(defaultTimestampFormat);
		}catch(IllegalArgumentException iae){
			timestamp = new SimpleDateFormat(defaultTimestampFormat);						
		}
	}

	@Override
	public void init(Element e) throws CapsuleInitException{
		//It's a good idea to call super so the config element is configured for us
		super.init(e);
		//Setup TinySOA services
		infoServiceModel = new ObjectServiceFactory().create(InfoServ.class);
		netServiceModel = new ObjectServiceFactory().create(NetServ.class);
		
		/*
		 * ACTIONS
		 * */

		addAction(new Action("TempCapsule.TinySOANetworks", 
				"Gets the name of all TinySOA Networks available"){
			public void run(Element e){
				Element payload = createPayloadElement();
				payload.setAttribute("type","network");

				//Obtain the list
				if(infoServ != null){
					Vector<Network> networks = infoServ.getNetworksList();
					Element entry = null;
					Element entryNet = null;

					for(Network net : networks){
						entry = new Element("entry");
						entryNet = new Element("network");
						entry.setAttribute("timestamp",timestamp.format(new Date()));
						entryNet.setAttribute("name",net.getName());
						entryNet.setAttribute("description",net.getDescription());
						entryNet.setAttribute("wsdl",net.getWsdl());
						entryNet.setAttribute("id",Integer.toString(net.getId()));
						entry.addContent(entryNet);
						payload.addContent(entry);
					}
				}
				DeliveryService.get().publish(payload);
			}
		});

		addAction(new Action("TempCapsule.SelectNetwork",
				"Select which TinySOA Network to use"){
			public void run(Element e) throws ActionInvokeException {
				Element payload = createPayloadElement();
				
				try{
					URL networkUrl = new URL(configValues.get("netserv"));
					netServ = (NetServ)new XFireProxyFactory().create(netServiceModel,
	                    networkUrl.getProtocol()+"://"+networkUrl.getHost()+":"+
	                    networkUrl.getPort()+networkUrl.getPath());
					Element success = new Element("entry");
					success.setAttribute("name", "Success");
					success.setAttribute("timestamp",timestamp.format(new Date()));
					success.addContent("Successfully connected to "+
							configValues.get("netserv"));
					payload.addContent(success);
				}catch(MalformedURLException mue){
					Element error = new Element("entry");
					error.setAttribute("name","Exception");
					error.setAttribute("timestamp",timestamp.format(new Date()));
					error.addContent(mue.getMessage());
					payload.addContent(error);
				}
				DeliveryService.get().publish(payload);
			}
		});
		
		addAction(new Action("TempCapsule.NetworkSensors",
				"Get a list of available sensors in the TinySOA network"){
			public void run(Element e) throws ActionInvokeException {
				Element payload = createPayloadElement();
				payload.setAttribute("type","parameter");
				
				if(netServ != null){
					Vector<Parameter> parameters = netServ.getSensorTypesList();
					for(Parameter p : parameters){
						Element entry = new Element("entry");
						entry.setAttribute("name", p.getName());
						entry.setAttribute("description", p.getDescription());
						payload.addContent(entry);
					}
				}
				DeliveryService.get().publish(payload);
			}
		});
		
		addAction(new Action("TempCapsule.GetReadings",
				"Get all readings in a specified timespan"){
			public void run(Element e) throws ActionInvokeException{
				Element payload = createPayloadElement();
				payload.setAttribute("type","reading");
				//Get the variables passed
				setVariables(e);

				String startDate = get("startdatetime");
				String endDate = get("enddatetime"); 
				String sensorType = get("sensortype");
				int limit = 0;
				
				try{
					limit = Integer.parseInt(configValues.get("limit"));
				}catch(NumberFormatException nfe){
					log.error(nfe.getMessage());
				}

				if(netServ != null){
					Vector<Reading> readings = netServ.getReadings(startDate, 
							endDate, sensorType, limit);
					for(Reading r : readings){
						Element entry = new Element("entry");
						entry.setAttribute("name",r.getParameter());
						entry.setAttribute("timestamp",timestamp.format(new Date()));
						entry.setAttribute("readingtime",r.getDateTime());
						entry.setAttribute("value",r.getValue());
						entry.setAttribute("nodeid",String.valueOf(r.getNid()));
						payload.addContent(entry);
					}
				}
				DeliveryService.get().publish(payload);
			}
		});
		
		addAction(new Action("TempCapsule.GetLastReadings","A listing of the last " +
				"readings of the sensor network"){
			public void run(Element e){
				Element payload = createPayloadElement();
				payload.setAttribute("type","reading");
				
				setVariables(e);
				String sensorType = configValues.get("sensortype");
				int limit = 0;
				try{
					limit = Integer.parseInt(configValues.get("limit"));
				}catch(NumberFormatException nfe){
					log.error(nfe.getMessage());
				}
				
				if(netServ != null){
					Vector<Reading> readings = netServ.getLastReadings(sensorType, limit);
					for(Reading r : readings){
						Element entry = new Element("entry");
						entry.setAttribute("name",r.getParameter());
						entry.setAttribute("value",r.getValue());
						entry.setAttribute("timestamp",timestamp.format(new Date()));
						entry.setAttribute("readingtime",r.getDateTime());
						entry.setAttribute("value",r.getValue());
						entry.setAttribute("nodeid",String.valueOf(r.getNid()));
						payload.addContent(entry);
					}
				}
				DeliveryService.get().publish(payload);
			}
		});
		
		addAction(new Action("TempCapsule.GetReadigsUntil","A listing of all available " +
				"readings until a certain date"){
			public void run(Element e){
				Element payload = createPayloadElement();
				payload.setAttribute("type","reading");
				
				setVariables(e);
				String startdate = configValues.get("startdate");
				int limit = 0;
				try{
					limit = Integer.parseInt(configValues.get("limit"));
				}catch(NumberFormatException nfe){
					log.error(nfe.getMessage());
				}
				
				if(netServ != null){
					Vector<Reading> readings = netServ.getReadingsUntil(startdate, limit);
					for(Reading r : readings){
						Element entry = new Element("entry");
						entry.setAttribute("name",r.getParameter());
						entry.setAttribute("readingtime",r.getDateTime());
						entry.setAttribute("value",r.getValue());
						entry.setAttribute("nodeid",String.valueOf(r.getNid()));
						payload.addContent(entry);
					}
				}
				DeliveryService.get().publish(payload);
			}
		});
		
		addAction(new Action("TempCapsule.GetAllReadings","A listing of all available" +
				"readings until a certain date, with no limit and ordered by date ascending"){
			public void run(Element e){
				Element payload = createPayloadElement();
				payload.setAttribute("type","reading");
				
				setVariables(e);
				String startDate = get("startdatetime");
				String endDate = get("enddatetime"); 
				String sensorType = get("sensortype");
				
				if(netServ != null){
					Vector<Reading> readings = netServ.getAllReadings(startDate, endDate, sensorType);
					for(Reading r : readings){
						Element entry = new Element("entry");
						entry.setAttribute("name",r.getParameter());
						entry.setAttribute("timestamp",timestamp.format(new Date()));
						entry.setAttribute("readingtime",r.getDateTime());
						entry.setAttribute("value",r.getValue());
						entry.setAttribute("nodeid",String.valueOf(r.getNid()));
						payload.addContent(entry);
					}
				}
				DeliveryService.get().publish(payload);
			}
		});
	}

	@Override
	public JPanel getConfigUI() {
		configUI = new JPanel(new FlowLayout());
		JButton startButton = new JButton("Start TinySOA");
		JButton stopButton = new JButton("Stop TinySOA");
		
		startButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt){
				startButtonClicked(evt);
			}
		});
		
		stopButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt){
				stopButtonClicked(evt);
			}
		});
		configUI.add(startButton);
		configUI.add(stopButton);
		configUI.setPreferredSize(new Dimension(400,300));
		return configUI;
	}

	public void start() throws StartException {
		//Startup infoServ
		try{
			URL infoServUrl = new URL(configValues.get("serverurl"));
			infoServ = (InfoServ)new XFireProxyFactory().create(infoServiceModel,
					infoServUrl.getProtocol()+"://"+infoServUrl.getHost()+":"+
					infoServUrl.getPort()+infoServUrl.getPath());
		} catch(MalformedURLException mue) {
			log.error(mue.getLocalizedMessage());
			throw new StartException("The URL provided is not valid.");
		} catch(XFireRuntimeException xre) {
			log.error(xre.getLocalizedMessage());
			throw new StartException(xre.getLocalizedMessage());
		}
	}

	public void stop() throws StopException {
		TinySOAServer.stop();
	}
	
	/**Sets the capsule configuration and stores config values into a hash map*/
	@SuppressWarnings("unchecked")
	@Override
	public void setConfigElement(Element e){
		super.setConfigElement(e);
		for(Element key : (List<Element>)e.getChildren()){
			configValues.put(key.getAttributeValue("name"), key.getAttributeValue("value"));
		}
	}
	
	private Element createPayloadElement(){
		Element payload = new Element("payload");
		payload.setAttribute(new Attribute("class", TempCapsule.class.getName()));
		return payload;
	}
	
	private void startButtonClicked(ActionEvent evt){
		try{
			start();
		}catch(StartException se){
			log.error(se.getMessage());
		}
	}
	
	private void stopButtonClicked(ActionEvent evt){
		try{
			stop();
		}catch(StopException se){
			log.error(se.getMessage());
		}
	}
}