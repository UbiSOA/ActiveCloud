package ubisoa.activecloud.capsules;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.jdom.Attribute;
import org.jdom.Element;

import ubisoa.activecloud.exceptions.CapsuleInitException;
import ubisoa.activecloud.exceptions.InvalidCapsuleException;

public class CapsuleLoader {
    private static Logger log = Logger.getLogger(CapsuleLoader.class);
    private ArrayList<IHardwareCapsule> hardwareCapsules = 
    	new ArrayList<IHardwareCapsule>();
    private ArrayList<INotificationCapsule> notificationCapsules =
    	new ArrayList<INotificationCapsule>();
    private ArrayList<List<IAction>> actions = new ArrayList<List<IAction>>();
    
	public ICapsule initHardwareCapsule(String elementName, Element root, int id, JarFile j)
	throws InvalidCapsuleException, CapsuleInitException{
		Object o = null;
		
		try{
			log.debug("Trying to init "+elementName);
			if(root == null){
				log.error("No '"+elementName+"' element in parameter file");
				throw new InvalidCapsuleException("No '"+elementName+"' element in parameter file");
			}

			if(!root.getChildren().isEmpty()){
				try{
					o = doInstance((Element)root.getChildren().get(0));
				} catch (ClassNotFoundException cnfe) {
					throw new InvalidCapsuleException("The class "+cnfe.getMessage()+
					" could not be found");
				}

			}

			//Check for correct instance
			Class<IHardwareCapsule> theHardwareClass = IHardwareCapsule.class;
			Class<INotificationCapsule> theNotificationClass = INotificationCapsule.class;
			
			if(o != null){
				//See if it's a hc or nc
				if(theHardwareClass.isInstance(o)){
					log.debug("Dealing with a hardware capsule...");
					log.debug("Making the call to the capsule's init method");
					((IHardwareCapsule)o).init((Element)root.getChildren().get(0));
					((IHardwareCapsule)o).setIcon(ImageIO.read(j.getInputStream(new ZipEntry("icon.png"))));
					((IHardwareCapsule)o).setConfigElement(root);
					log.debug("Adding the initialized capsule to the pool");
					hardwareCapsules.add(id, ((IHardwareCapsule)o));
					addActions(id, (IHardwareCapsule)o);
					return ((IHardwareCapsule)o);
				}else if(theNotificationClass.isInstance(o)){
					log.debug("Dealing with a notification capsule...");
					log.debug("Making the call to the capsule's init method");
					((INotificationCapsule)o).init((Element)root.getChildren().get(0));
					((INotificationCapsule)o).setIcon(ImageIO.read(j.getInputStream(new ZipEntry("icon.png"))));
					((INotificationCapsule)o).setConfigElement(root);
					log.debug("Adding the initialized capsule to the pool");
					notificationCapsules.add(id, ((INotificationCapsule)o));
					return ((INotificationCapsule)o);
				}else{
					log.error("Not a HC nor a NC, I don't know what to do with this");
					throw new InvalidCapsuleException("Invalid capsule configuration, "+o.getClass().getName() +
							" is not of IHardwareCapsule or INotificationCapsule type.");
				}
			}			
		}catch(Exception e){
			throw new CapsuleInitException(e.getMessage(), o.getClass().getName());
		}
		return null;
	}
	
	public ArrayList<List<IAction>> getActions(){
		return actions;
	}
	
	public ArrayList<IHardwareCapsule> getHardwareCapsules(){
		return hardwareCapsules;
	}
	
	public ArrayList<INotificationCapsule> getNotificationCapsules(){
		return notificationCapsules;
	}
	
	public INotificationCapsule getNotificationCapsuleAtIndex(int id){
		return notificationCapsules.get(id);
	}
	
	public int getNotifcationCapsulesCount(){
		return notificationCapsules.size();
	}
	
	/**Get the number of notification capsules and hardware capsules loaded.
	 * This method is a shortcut for calling getNotificationCapsulesCount() and
	 * adding the result to getHardwareCapsulesCount().*/
	public int getCapsulesCount(){
		return notificationCapsules.size()+hardwareCapsules.size();
	}
	
	/**Gets the hardware capsule with the specified id
	 * 
	 * @param	id	The id of the capsule to obtain
	 * @return		The hardware capsule with that id*/
	public IHardwareCapsule getHardwareCapsuleAtIndex(int id){
		return hardwareCapsules.get(id);
	}
	
	/**Gets the number of hardware capsules that are ready to use (initialized)*/
	public int getHardwareCapsulesCount(){
		return hardwareCapsules.size();
	}
	
	private void addActions(int id, IHardwareCapsule c){
		if(actions.size() < id){
			actions.ensureCapacity(id);
		}
		actions.set(id, c.getActions());
	}
	
	private Object doInstance(Element e) throws InvalidCapsuleException, 
		ClassNotFoundException{
		//Get class attribute from XML
		Attribute classAtt = e.getAttribute("class");
		if(classAtt == null){
			log.error("No 'class' Attribute found in element");
			throw new InvalidCapsuleException("No 'class' Attribute");
		}

		//Make the instance
		String className = classAtt.getValue();
		log.debug("Got classname: "+className);
		Object o = null;
		
		try{
			o = Class.forName(className).newInstance();
		} catch (IllegalAccessException iae){
			log.error(iae.getMessage());
		} catch (InstantiationException ie){
			log.error(ie.getMessage());
		}

		return o;
	}
}
