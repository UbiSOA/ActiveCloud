package ubisoa.activecloud.services;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import ubisoa.activecloud.capsules.IHardwareCapsule;
import ubisoa.activecloud.exceptions.CapsuleAlreadyLoadedException;
import ubisoa.activecloud.exceptions.CapsuleInitException;
import ubisoa.activecloud.exceptions.InvalidCapsuleException;
import ubisoa.activecloud.hal.capsuleloader.CapsuleLoader;
import ubisoa.activecloud.hal.capsuleloader.ClassPathHacker;

/**
 * The Node Access Service (NAS) maintains the hardware repository and keeps the services
 * available to the end user updated. NodeAccessService is a singleton class*/
public final class NodeAccessService{
	private static NodeAccessService singleton;
	private Properties properties;
	private CapsuleLoader loader;
	private int loadedCapsules = 0;
	private static Logger log = Logger.getLogger(NodeAccessService.class);
	
	private NodeAccessService(){
		loader = new CapsuleLoader();
		properties = new Properties();
		try{
			log.debug("Initializing loadedCapsules counter: "+loadedCapsules);
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
	
	public boolean isCapsuleLoaded(String capsule){
		if(properties.containsKey(capsule))
			return true;
		return false;
	}
	
	private void saveCapsule(String capsule){
		if(isCapsuleLoaded(capsule))
			throw new CapsuleAlreadyLoadedException("The key "
					+capsule+" is present in the properties file");
		properties.setProperty(capsule, Integer.toString(loadedCapsules));
		loadedCapsules++;
		log.debug("Saved property? "+properties.containsKey(capsule));
	}
	
	public IHardwareCapsule loadCapsule(JarFile capsule){
		SAXBuilder builder = new SAXBuilder();
		IHardwareCapsule cap = null;
		
		try{
			Document doc = builder.build(capsule.getInputStream(
					new ZipEntry("config.xml")));
			Element root = doc.getRootElement();
			
			//Add the capsule to the classpath
			ClassPathHacker.addFile(capsule.getName());
			
			/*Create the capsule object representation
			 * from the files previously readed*/
			cap = (IHardwareCapsule)loader.initHardwareCapsule("capsule", 
					root, loadedCapsules);
			
			/*A null capsule can be returned if that capsule is already
			 * loaded*/
			if(cap != null){
				cap.setIcon(ImageIO.read(capsule.getInputStream(new ZipEntry("icon.png"))));
				cap.setConfigElement(root);
				
				/*Capsule correctly loaded, save it*/
				Element hal = root.getChild("hal");
				Element ns = root.getChild("ns");
				
				if(hal != null){
					log.debug("Saving HAL capsule: "+hal.getAttributeValue("class"));
					saveCapsule(hal.getAttributeValue("class"));
				}
				else if(ns != null){
					log.debug("Saving NS capsule: "+ns.getAttributeValue("class"));
					saveCapsule(ns.getAttributeValue("class"));
				}
				else{
					XMLOutputter output = new XMLOutputter();
					output.setFormat(Format.getPrettyFormat());
					log.info(output.outputString(root));
					throw new InvalidCapsuleException("config.xml is not of HAL or NS type.");
				}
			}
			
		} catch (JDOMException jde) {
			log.error(jde.getMessage());
		} catch (IOException ioe) {
			log.error(ioe.getMessage());
		} catch (InvalidCapsuleException ice) {
			log.error(ice.getMessage());
		} catch (CapsuleInitException cie) {
			log.error(cie.getMessage());
		}
		
		return cap;
	}

	public static NodeAccessService get(){
		if(singleton == null){
			singleton = new NodeAccessService();
		}
		return singleton;
	}
}