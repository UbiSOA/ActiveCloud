package ubisoa.activecloud.hal.capsuleloader;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.jdom.Attribute;
import org.jdom.Element;

import ubisoa.activecloud.exceptions.CapsuleInitException;
import ubisoa.activecloud.exceptions.InvalidCapsuleException;
import ubisoa.activecloud.hal.capsules.ICapsule;

public class CapsuleLoader {
    private static Logger log = Logger.getLogger(CapsuleLoader.class);
	
	public Object initClass(String elementName, Class<ICapsule> theClass, Element root)
	throws InvalidCapsuleException, CapsuleInitException{
		if(root == null){
			log.error("No '"+elementName+"' element in parameter file");
			throw new InvalidCapsuleException("No '"+elementName+"' element in parameter file");
		}

		Object o = null;
		if(!root.getChildren().isEmpty()){
			o = doInstance((Element)root.getChildren().get(0));
		}
		
		//Check for correct instance
		if(o != null){
			if(!theClass.isInstance(o)){
				log.error("Not an "+theClass.getName()+" class: "+o.getClass().getName());
				throw new InvalidCapsuleException("Not an "+theClass.getName()+" class: "+o.getClass().getName());
			}
			/*The capsule got correctly loaded, we'll save it to the 
			 * db of running capsules so we don't load it again.*/
			((ICapsule)o).init(root);
		}

		return o;
	}
	
	private Object doInstance(Element e) throws InvalidCapsuleException{
		//Get class attribute from XML
		Attribute classAtt = e.getAttribute("class");
		if(classAtt == null){
			log.error("No 'class' Attribute found in element");
			throw new InvalidCapsuleException("No 'class' Attribute");
		}

		//Make the instance
		String className = classAtt.getValue();
		Object o = null;
		
		try{
			o = Class.forName(className).newInstance();	
		} catch (ClassNotFoundException cnfe){
			log.error(cnfe.getMessage());
		} catch (IllegalAccessException iae){
			log.error(iae.getMessage());
		} catch (InstantiationException ie){
			log.error(ie.getMessage());
		}

		return o;
	}
	
	public static void main(String args[]){
		//Create a JDOM and instantiate a new plugin

		Element root = new Element("root");
		Element plugin = new Element("capsule");
		plugin.setAttribute("class", "com.divinesoft.activecloud.capsules.TinyOS1xCapsule");
		root.addContent(plugin);
		
		CapsuleLoader loader = new CapsuleLoader();
		try{
			//This should fail because the jar is not there
			ICapsule capsule = (ICapsule)loader.initClass("capsule",ICapsule.class,plugin);
			capsule.start();
			capsule.stop();
		}catch (Exception e){
			System.out.println("Error loading: "+e.getMessage());
		}
		
		try{
			//This should NOT fail
			ClassPathHacker.addFile(new File("/home/cesar/Desktop/tinyos1x.jar"));
			ClassPathHacker.addFile(new File("/home/cesar/jars/HCSim.jar"));
			ICapsule capsule = (ICapsule)loader.initClass("capsule", ICapsule.class, plugin);
			capsule.start();
			capsule.stop();
		} catch(IOException ioe) {
			System.err.println(ioe);
		} catch (Exception e){
			System.err.println(e);
		}	
	}

}