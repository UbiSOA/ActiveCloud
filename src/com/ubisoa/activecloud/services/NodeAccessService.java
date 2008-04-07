package com.ubisoa.activecloud.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.ubisoa.activecloud.capsules.CapsuleLoader;
import com.ubisoa.activecloud.capsules.ClassPathHacker;
import com.ubisoa.activecloud.capsules.HardwareCapsule;
import com.ubisoa.activecloud.capsules.IAction;
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
		int i = 0;
		for(NotificationCapsule cap : loader.getNotificationCapsules()){
			if(cap.getClass().getName().equals(name)){
				return loader.getNotificationCapsuleAtIndex(i);
			}
			i++;
		}
		return null;
	}
	
	public ArrayList<HardwareCapsule> getHardwareCapsules(){
		return loader.getHardwareCapsules();
	}
	
	public int getHardwareCapsuleId(String capsule){
		/*iterate the loaded hardware capsules*/
		int i = 0;
		for(HardwareCapsule cap : loader.getHardwareCapsules()){
			if(cap.getClass().getName().equals(capsule)){
				return i;
			}
			i++;
		}
		return -1;
	}
	
	public ArrayList<NotificationCapsule> getNotificationCapsules(){
		return loader.getNotificationCapsules();
	}
	
	public String[] getActionList(){
		ArrayList<String> actionList = new ArrayList<String>();
		for(HardwareCapsule hc : loader.getHardwareCapsules()){
			for(IAction action : hc.getActions()){
				actionList.add(action.getName());
			}
		}
		String[] result = new String[actionList.size()];
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
	public synchronized void invokeAction(String action) throws ActionInvokeException{
		//Get the ID corresponding to the action
		int[] ids = loader.actionToId(action);
		final int id = ids[0];
		final int actId = ids[1];
		
		/*If the action is found inside a loaded capsule, invoke it.
		 * When an action is not found, actionToId returns -1 in both
		 * the CapsuleID and ActionID*/
		if((id >= 0) && (actId >= 0)){
			final HardwareCapsule c = loader.getHardwareCapsuleAtIndex(id);
			
			new Thread(){
				public void run(){
					c.getActions().get(actId).run();
				}
			}.run();
			
		} else {
			log.debug("Action "+action+" was not found.");
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