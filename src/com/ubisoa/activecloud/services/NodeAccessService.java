package com.ubisoa.activecloud.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.ubisoa.activecloud.capsules.Action;
import com.ubisoa.activecloud.capsules.CapsuleLoader;
import com.ubisoa.activecloud.capsules.ClassPathHacker;
import com.ubisoa.activecloud.capsules.HardwareCapsule;
import com.ubisoa.activecloud.capsules.ICapsule;
import com.ubisoa.activecloud.capsules.NotificationCapsule;
import com.ubisoa.activecloud.exceptions.ActionInvokeException;
import com.ubisoa.activecloud.exceptions.CapsuleInitException;
import com.ubisoa.activecloud.exceptions.InvalidCapsuleException;


/**
 * The Node Access Service (NAS) maintains the hardware repository and keeps the services
 * available to the end user updated. NodeAccessService is a singleton class*/
public final class NodeAccessService{
	private static NodeAccessService singleton;
	private CapsuleLoader loader;
	private static Logger log = Logger.getLogger(NodeAccessService.class);
	
	private NodeAccessService() throws CapsuleInitException{
		loader = new CapsuleLoader();
	}
	
	public int loadedCapsulesSize(){
		return loader.getCapsulesCount();
	}
	
	public int loadedHardwareCapsulesSize(){
		return loader.getHardwareCapsulesCount();
	}
	
	public int loadedNotificationCapsulesSize(){
		return loader.getNotifcationCapsulesCount();
	}
	
	public HardwareCapsule getHardwareCapsuleByAction(String action){
		int[] ids = loader.actionToId(action);
		if(ids[0] != -1 && ids[1] != -1){
			return getHardwareCapsule(ids[0]);
		}
		return null;
	}
	
	public HardwareCapsule getHardwareCapsule(String name){
		int i = 0;
		for(HardwareCapsule cap : loader.getHardwareCapsules()){
			if(name.indexOf('.') == -1){
				if(cap.getClass().getSimpleName().equals(name)){
					return loader.getHardwareCapsuleAtIndex(i);
				}
			}else{
				if(cap.getClass().getName().equals(name)){
					return loader.getHardwareCapsuleAtIndex(i);
				}				
			}
			i++;
		}
		return null;
	}
	
	public HardwareCapsule getHardwareCapsule(int id){
		if(loader.getHardwareCapsulesCount() > id){
			return loader.getHardwareCapsuleAtIndex(id);
		}
		return null;
	}
	
	public NotificationCapsule getNotificationCapsule(int id){
		if(loader.getNotifcationCapsulesCount() > id){
			return loader.getNotificationCapsuleAtIndex(id);
		}
		return null;
	}
	
	public NotificationCapsule getNotificationCapsule(String name){
		/*If the name is missing a '.' in the name, the user is trying to locate the capsule
		 * by it's simple name*/
		int i = 0;
		for(NotificationCapsule cap : loader.getNotificationCapsules()){
			if(name.indexOf('.') == -1){
				if(cap.getClass().getSimpleName().equals(name)){
					return loader.getNotificationCapsuleAtIndex(i);
				}
			}else{
				if(cap.getClass().getName().equals(name)){
					return loader.getNotificationCapsuleAtIndex(i);
				}				
			}
			i++;
		}
		return null;
	}
	
	public ArrayList<HardwareCapsule> getHardwareCapsules(){
		return loader.getHardwareCapsules();
	}
	
	public int getHardwareCapsuleId(String capsuleName){
		/*iterate the loaded hardware capsules*/
		int i = 0;
		for(HardwareCapsule cap : loader.getHardwareCapsules()){
			if(cap.getClass().getName().equals(capsuleName)){
				return i;
			}
			i++;
		}
		return -1;
	}
	
	public ArrayList<NotificationCapsule> getNotificationCapsules(){
		return loader.getNotificationCapsules();
	}
	
	public Action[] getActionList(){
		ArrayList<Action> actionList = new ArrayList<Action>();
		for(HardwareCapsule hc : loader.getHardwareCapsules()){
			for(Action action : hc.getActions()){
				actionList.add(action);
			}
		}
		Action[] result = new Action[actionList.size()];
		return actionList.toArray(result);
	}
	
	public boolean isCapsuleLoaded(String capsule){
		//if there are no capsules loaded, it's not there
		if(loader.getCapsulesCount() == 0){
			log.debug("CapsuleLoader is reporting 0 capsules loaded so "+capsule
					+" is not there.");
			return false;
		}
		
		/*iterate the loaded hardware capsules*/
		for(HardwareCapsule cap : loader.getHardwareCapsules()){
			if(cap.getClass().getCanonicalName().equals(capsule))
				return true;
		}
		
		/*iterate the loaded notification capsules*/
		for(NotificationCapsule cap : loader.getNotificationCapsules()){
			if(cap.getClass().getCanonicalName().equals(capsule))
				return true;
		}
		
		/*It's not loaded*/
		return false;
	}
	
	public boolean isCapsuleLoaded(ICapsule hc){
		if(loader.getCapsulesCount() == 0){
			log.debug("CapsuleLoader is reporting 0 capsules loaded so "+hc
					+" is not there.");
			return false;
		}
		
		/*iterate the loaded hardware capsules*/
		if(loader.getHardwareCapsules().contains(hc)){
			return true;
		}
		
		/*iterate the loaded notification capsules*/
		if(loader.getNotificationCapsules().contains(hc)){
			return true;
		}
		
		/*It's not loaded*/
		return false;
	}
	
	public synchronized ICapsule loadCapsule(JarFile capsule) 
		throws CapsuleInitException, InvalidCapsuleException{
		log.debug("Loading capsule "+capsule.getName());
		SAXBuilder builder = new SAXBuilder();
		
		try{
			Document doc = builder.build(capsule.getInputStream(
					new ZipEntry("config.xml")));
			Element root = doc.getRootElement();
			Element hal = (Element)root.getChildren().get(0);
			String className = hal.getAttributeValue("class");
			
			if(!isCapsuleLoaded(className)){
				//Add the capsule to the classpath
				ClassPathHacker.addFile(capsule.getName());
				
				/*Create the capsule object representation
				 * from the files previously read*/
				log.debug("Going to CapsuleLoader");
				return loader.initCapsule("capsule", root, capsule);	
			}else{
				log.error(className + " is reported as beign already loaded, so not loading again");
			}
			
		} catch (JDOMException jde) {
			log.error(jde.getMessage());
		} catch (IOException ioe) {
			log.error(ioe.getMessage());
		} catch (InvalidCapsuleException ice) {
			log.error(ice.getMessage());
		}
		return null;
	}
	
	/**Searches for the specified action and if found, executes it's run method*/
	public synchronized void invokeAction(String action, Element e){
		//Get the ID corresponding to the action
		int[] ids = loader.actionToId(action);
		final int id = ids[0];
		final int actId = ids[1];
		final Element el = e;
		
		/*If the action is found inside a loaded capsule, invoke it.
		 * When an action is not found, actionToId returns -1 in both
		 * the CapsuleID and ActionID*/
		if((id >= 0) && (actId >= 0)){
			final HardwareCapsule c = loader.getHardwareCapsuleAtIndex(id);
			
			new Runnable(){
				public void run(){
					
					try{
						c.getActions().get(actId).run(el);	
					}catch(ActionInvokeException aie){
						log.error(aie.getMessage());
					}
					
				}
			}.run();
			
		} else {
			log.debug("Action "+action+" was not found.");
		}
	}
	
	/**Creates an empty config Element and invokes the specified action. This method should
	 * be used when the action don't need parameters*/
	public synchronized void invokeAction(String action){
		invokeAction(action, new Element("config"));
	}
	
	/**Searches and invokes the specified action. Parameters can be passed as a comma delimited 
	 * string, which get converted to their corresponding XML Element e.g:
	 * 
	 * The following string:
	 * "startdate=01/01/2005, enddate=01/01/2008, parameter=light"
	 * 
	 * Produces:
	 * <config>
	 * 	<key name="startdate" value="01/01/2005" />
	 * 	<key name="enddate" value="01/01/2008" />
	 * 	<key name="parameter" value="light" />
	 * </config>
	 * 
	 * In order to get the values from the parameter string, a StringTokenizer is used. Each
	 * token is then divided by equal sign and removed from any leading or trailing whitespace
	 * (trim).
	 * */
	public synchronized void invokeAction(String action, String parameters) 
		throws ActionInvokeException{
		Element config = new Element("config");
		StringTokenizer st = new StringTokenizer(parameters,",");
		while(st.hasMoreElements()){
			String element = (String) st.nextElement();
			int i = element.indexOf('=');
			if(i == -1){
				throw new ActionInvokeException("Parameter String is not correctly formatted");
			}
			String name = element.substring(0,i).trim();
			String value = element.substring(i+1).trim();
			Element key = new Element("key");
			key.setAttribute("name",name);
			key.setAttribute("value",value);
			config.addContent(key);
		}
		invokeAction(action,config);
	}
	
	public synchronized void setConfiguration(String name, String parameters) 
		throws Exception{
		Element config = new Element("config");
		StringTokenizer st = new StringTokenizer(parameters,",");
		
		while(st.hasMoreElements()){
			String element = (String) st.nextElement();
			int i = element.indexOf('=');
			if(i == -1){
				throw new Exception("Parameter String is not correctly formatted");
			}
			Element key = new Element("key");
			key.setAttribute("name",element.substring(0,i).trim());
			key.setAttribute("value",element.substring(i+1).trim());
			config.addContent(key);
		}
		HardwareCapsule hc = getHardwareCapsule(name);
		if(hc != null){
			hc.setConfigElement(config);
		}
	}

	public static NodeAccessService get() throws CapsuleInitException{
		if(singleton == null){
			log.debug("Instantiating "+NodeAccessService.class.getName());
			singleton = new NodeAccessService();
		}
		return singleton;
	}
}