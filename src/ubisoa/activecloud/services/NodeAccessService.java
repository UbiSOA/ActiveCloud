package ubisoa.activecloud.services;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import ubisoa.activecloud.capsules.CapsuleLoader;
import ubisoa.activecloud.capsules.ClassPathHacker;
import ubisoa.activecloud.capsules.ICapsule;
import ubisoa.activecloud.capsules.IHardwareCapsule;
import ubisoa.activecloud.capsules.INotificationCapsule;
import ubisoa.activecloud.exceptions.ActionInvokeException;
import ubisoa.activecloud.exceptions.CapsuleInitException;
import ubisoa.activecloud.exceptions.InvalidCapsuleException;

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
	
	public ArrayList<IHardwareCapsule> getHardwareCapsules(){
		return loader.getHardwareCapsules();
	}
	
	public ArrayList<INotificationCapsule> getNotificationCapsules(){
		return loader.getNotificationCapsules();
	}
	
	public boolean isCapsuleLoaded(String capsule){
		//if there are no capsules loaded, it's not there
		if(loader.getCapsulesCount() == 0){
			log.debug("CapsuleLoader is reporting 0 capsules loaded so "+capsule
					+" is not there.");
			return false;
		}
		
		/*iterate the loaded hardware capsules*/
		for(IHardwareCapsule cap : loader.getHardwareCapsules()){
			if(cap.getClass().getCanonicalName().equals(capsule))
				return true;
		}
		
		/*iterate the loaded notification capsules*/
		for(INotificationCapsule cap : loader.getNotificationCapsules()){
			if(cap.getClass().getCanonicalName().equals(capsule))
				return true;
		}
		
		/*It's not loaded*/
		return false;
	}
	
	public ICapsule loadCapsule(JarFile capsule) 
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
				 * from the files previously readed*/
				log.debug("Going to CapsuleLoader");
				return loader.initHardwareCapsule("capsule", root, 
						loader.getHardwareCapsulesCount(), capsule);	
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
	
	public void invokeAction(String action, Element params) throws ActionInvokeException{
		//Get the ID corresponding to the action
		int[] ids = loader.actionToId(action);
		int id = ids[0];
		int actId = ids[1];
		
		/*If the action is found inside a loaded capsule, invoke it.
		 * When an action is not found, actionToId returns -1 in both
		 * the CapsuleID and ActionID*/
		if((id >= 0) && (actId >= 0)){
			IHardwareCapsule c = loader.getHardwareCapsuleAtIndex(id);
			c.getActions().get(actId).invoke(params);
		}
	}

	public static NodeAccessService get() throws CapsuleInitException{
		if(singleton == null){
			log.debug("Instantiating "+NodeAccessService.class.getName());
			singleton = new NodeAccessService();
		}
		return singleton;
	}
	
	public static void main(String args[]){
		String cP = "/home/cesar/Desktop/tinyoscapsule.jar";
		try{
			log.debug("Some stats");
			log.debug("Loaded Capsules: "+NodeAccessService.get().loadedCapsulesSize());
			log.debug("Is capsule loaded? "+NodeAccessService.get().isCapsuleLoaded("com.divinesoft.activecloud.capsules.TinyOS1Capsule"));
			log.debug("Trying to load capsule...");
			NodeAccessService.get().loadCapsule(new JarFile(new File(cP)));
			Thread.sleep(5000);
			log.debug("Done loading... getting stats again");
			log.debug("Some stats");
			log.debug("Loaded Capsules: "+NodeAccessService.get().loadedCapsulesSize());
			log.debug("Is capsule loaded? "+NodeAccessService.get().isCapsuleLoaded("com.divinesoft.activecloud.capsules.TinyOS1Capsule"));
		}catch(CapsuleInitException cie){
			log.error(cie.getMessage());
		}catch(IOException ioe){
			log.error(ioe.getMessage());
		}catch(Exception e){
			log.error(e.getMessage());
		}
	}
}