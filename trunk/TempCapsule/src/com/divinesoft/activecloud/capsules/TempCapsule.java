package com.divinesoft.activecloud.capsules;

import java.awt.FlowLayout;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import net.tinyos.tinysoa.common.Network;
import net.tinyos.tinysoa.common.Parameter;
import net.tinyos.tinysoa.server.InfoServ;
import net.tinyos.tinysoa.server.NetServ;

import org.apache.log4j.Logger;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.client.XFireProxyFactory;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.jdom.Attribute;
import org.jdom.Element;

import com.ubisoa.activecloud.capsules.HardwareCapsule;
import com.ubisoa.activecloud.capsules.IAction;
import com.ubisoa.activecloud.exceptions.ActionInvokeException;
import com.ubisoa.activecloud.exceptions.CapsuleInitException;
import com.ubisoa.activecloud.exceptions.StartException;
import com.ubisoa.activecloud.exceptions.StopException;
import com.ubisoa.activecloud.services.DeliveryService;

public class TempCapsule extends HardwareCapsule{
	private JPanel configUI;
	private static Logger log = Logger.getLogger(TempCapsule.class);
	private InfoServ infoServ = null;
	private NetServ netServ = null;
	private static final String defaultTimestampFormat = "yyyy-MM-dd 'at' HH:mm:ss";
	private HashMap<String, String> configValues;
	private SimpleDateFormat timestamp;
	private Service infoServiceModel;
	private Service netServiceModel;

	@Override
	public void init(Element e) throws CapsuleInitException{
		//It's a good idea to call super so the config element is configured for us
		super.init(e);
		//Setup TinySOA services
		infoServiceModel = new ObjectServiceFactory().create(InfoServ.class);
		netServiceModel = new ObjectServiceFactory().create(NetServ.class);
		//Startup infoServ
		try{
			URL infoServUrl = new URL(configValues.get("serverurl"));
			infoServ = (InfoServ)new XFireProxyFactory().create(infoServiceModel,
					infoServUrl.getProtocol()+"://"+infoServUrl.getHost()+":"+
					infoServUrl.getPort()+infoServUrl.getPath());
		} catch(MalformedURLException mue) {
			System.out.println(mue.getLocalizedMessage());
		} catch(XFireRuntimeException xre) {
			System.out.println(xre.getLocalizedMessage());
			JOptionPane.showMessageDialog(null, "Could not invoke information service. Is TinySOA server running?");
		}
		//Try to format the timestamp according to what the user provided
		try{
			timestamp = new SimpleDateFormat(configValues.get("timestampformat"));
		}catch(NullPointerException npe){
			timestamp = new SimpleDateFormat(defaultTimestampFormat);
		}catch(IllegalArgumentException iae){
			timestamp = new SimpleDateFormat(defaultTimestampFormat);						
		}
		
		/*
		 * ACTIONS
		 * */

		addAction(new IAction(){
			public void run(){
				Element payload = new Element("payload");
				payload.setAttribute(new Attribute("class", TempCapsule.class.getName()));

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

			public String getName(){
				return "TempCapsule.TinySOANetworks";
			}

			public String getDescription(){
				return "Gets the name of all TinySOA Networks available";
			}
		});

		addAction(new IAction(){
			public void run(){
				Element payload = new Element("payload");
				payload.setAttribute(new Attribute("class",TempCapsule.class.getName()));
				List<Element> paramEntries = getConfigElement().getChildren();

				/*For each param passed, add a fake reading*/
				for(final Element p : paramEntries){
					Element entry = new Element("entry");
					//The entry name is the same of the attribute passed
					entry.setAttribute("name",p.getAttributeValue("name"));
					entry.setAttribute("timestamp",timestamp.format(new Date()));	
					//Add a random value between 0 and 100
					entry.addContent(String.valueOf(new Random().nextFloat() * 100));
					//Add the new fake entry to the payload element
					payload.addContent(entry);
				}

				//publish the payload
				DeliveryService.get().publish(payload);
			}

			public String getDescription(){
				return "Simulate a TinySOA reading";
			}

			public String getName(){
				return "TempCapsule.TinySOAReading";
			}

			@Override
			public String toString(){
				return getName();
			}
		});

		addAction(new IAction(){
			public String getDescription() {
				return "Select which TinySOA Network to use";
			}

			public String getName() {
				return "TempCapsule.SelectNetwork";
			}

			public void run() throws ActionInvokeException {
				Element payload = new Element("payload");
				payload.setAttribute(new Attribute("class", TempCapsule.class.getName()));
				
				try{
					URL networkUrl = new URL(configValues.get("netserv"));
					netServ = (NetServ)new XFireProxyFactory().create(netServiceModel,
	                    networkUrl.getProtocol()+"://"+networkUrl.getHost()+":"+
	                    networkUrl.getPort()+networkUrl.getPath());
					Element success = new Element("entry");
					success.setAttribute("name", "Success");
					success.setAttribute("timestamp",timestamp.format(new Date()));
					success.addContent("Successfully connected to "+configValues.get("netserv"));
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
		
		addAction(new IAction(){
			public String getDescription() {
				return "List the sensors available to the TinySOA network";
			}

			@Override
			public String getName() {
				return "TempCapsule.NetworkSensors";
			}

			public void run() throws ActionInvokeException {
				Element payload = new Element("payload");
				payload.setAttribute(new Attribute("class", TempCapsule.class.getName()));
				
				if(netServ != null){
					for(Parameter p : netServ.getSensorTypesList()){
						Element entry = new Element("entry");
						entry.setAttribute("name", p.getName());
						entry.setAttribute("value", p.getDescription());
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
		configUI.add(new JLabel("TempCapsule"));
		return configUI;
	}

	public void start() throws StartException {
	}

	public void stop() throws StopException {
	}
	
	/**Sets the capsule configuration and stores config values into a hash map*/
	@Override
	public void setConfigElement(Element e){
		for(Element key : (List<Element>)e.getChildren()){
			configValues.put(key.getAttributeValue("name"), key.getAttributeValue("value"));
		}
	}
}