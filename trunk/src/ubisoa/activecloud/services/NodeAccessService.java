package ubisoa.activecloud.services;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

import ubisoa.activecloud.exceptions.CapsuleAlreadyLoadedException;
import ubisoa.activecloud.hal.capsules.Capsule;

/**
 * The Node Access Service (NAS) maintains the hardware repository and keeps the services
 * available to the end user updated. NodeAccessService is a singleton class*/
public final class NodeAccessService{
	private static NodeAccessService singleton;
	private Properties properties;
	private static Logger log = Logger.getLogger(NodeAccessService.class);
	
	private NodeAccessService(){
		properties = new Properties();
		try{
			log.debug("loading loadedcapsules.properties");
			properties.load(new FileInputStream
					("loadedcapsules.properties"));
			log.debug("Clearing loadedcapsules file");
			properties.clear();
			log.debug("Saving clear loadedcapsules file");
			properties.store(new FileOutputStream("loadedcapsules.properties"), 
					null);
		} catch (IOException ioe) {
			log.error(ioe.getMessage());
		}
	}
	
	public boolean isCapsuleLoaded(Capsule capsule){
		if(properties.containsKey(capsule.getClassName()))
			return true;
		return false;
	}
	
	public void saveCapsule(Capsule capsule){
		if(isCapsuleLoaded(capsule))
			throw new CapsuleAlreadyLoadedException("The key "
					+capsule.getClassName()+" is present in the properties file");
		properties.setProperty(capsule.getClassName(), "1");
	}

	public static NodeAccessService get(){
		if(singleton == null){
			singleton = new NodeAccessService();
		}
		return singleton;
	}
}