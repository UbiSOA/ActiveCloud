package com.divinesoft.activecloud.capsules;

import java.awt.FlowLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import ubisoa.activecloud.capsules.HardwareCapsule;
import ubisoa.activecloud.capsules.IAction;
import ubisoa.activecloud.exceptions.CapsuleInitException;
import ubisoa.activecloud.exceptions.StartException;
import ubisoa.activecloud.exceptions.StopException;
import ubisoa.activecloud.services.DeliveryService;

public class TempCapsule extends HardwareCapsule{
	private JPanel configUI;
	private static Logger log = Logger.getLogger(TempCapsule.class);
	private String timestampFormat;
	private static final String defaultTimestampFormat = "yyyy-MM-dd 'at' HH:mm:ss";
	
	public void init(Element e) throws CapsuleInitException{
		timestampFormat = e.getAttributeValue("timestampformat");
		
		addAction(new IAction(){
			public void run(Element e){
				Element payload = new Element("payload");
				payload.setAttribute(new Attribute("class",TempCapsule.class.getName()));
				List<Element> paramEntries = e.getChildren();
				Iterator<Element> iterator = paramEntries.iterator();
				
				/*For each param passed, add a fake reading*/
				while(iterator.hasNext()){
					Element p = iterator.next();
					Element entry = new Element("entry");
					//The entry name is the same of the attribute passed
					entry.setAttribute("name",p.getAttributeValue("name"));
					
					//Try to format the timestamp according to what the user provided
					try{
						SimpleDateFormat timestamp = new SimpleDateFormat(timestampFormat);
						entry.setAttribute("timestamp",timestamp.format(new Date()));
					}catch(Exception ex){
						SimpleDateFormat timestamp = new SimpleDateFormat(
								TempCapsule.defaultTimestampFormat);
						entry.setAttribute("timestamp",timestamp.format(new Date()));
					}
					
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
		});
		
		addAction(new IAction(){
			public void run(Element e){
				XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
				log.debug(out.outputString(e));
			}
			public String getDescription(){
				return "Print the element to the console";
			}
			public String getName(){
				return "TempCapsule.printElement";
			}
		});
		
		addAction(new IAction(){
			public void run(Element e){
				XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
				log.debug(out.outputString(e));
				try{
					URL google = new URL("http://www.google.com/");
					BufferedReader in = new BufferedReader(
							new InputStreamReader(google.openStream()));
					
					String inputLine;
					StringBuilder completeOutput = new StringBuilder();
					while((inputLine = in.readLine()) != null){
						log.info(inputLine);
						completeOutput.append(inputLine);
					}
					in.close();
					Element el = new Element("payload");
					el.setAttribute("class",TempCapsule.class.getName());
					el.addContent(completeOutput.toString());
					DeliveryService.get().publish(el);
				}catch(MalformedURLException mue){
					log.error(mue.getMessage());
				}catch(IOException ioe){
					log.error(ioe.getMessage());
				}
				
			}
			public String getDescription(){
				return "Retrieve Google Frontpage";
			}
			public String getName(){
				return "TempCapsule.retrieveGoogleFrontpage";
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
}