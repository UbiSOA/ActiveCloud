package ubisoa.activecloud.capsules;

import java.util.ArrayList;
import java.util.HashMap;
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
    private ArrayList<HardwareCapsule> hardwareCapsules = 
    	new ArrayList<HardwareCapsule>();
    private ArrayList<NotificationCapsule> notificationCapsules =
    	new ArrayList<NotificationCapsule>();
    //private ArrayList<List<IAction>> actions = new ArrayList<List<IAction>>();
    private HashMap<String,int[]> actionToId = new HashMap<String,int[]>();
    
	public synchronized ICapsule initCapsule(String elementName, Element root, JarFile j)
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
			Class<HardwareCapsule> theHardwareClass = HardwareCapsule.class;
			Class<NotificationCapsule> theNotificationClass = NotificationCapsule.class;
			
			if(o != null){
				//See if it's a hc or nc
				if(theHardwareClass.isInstance(o)){
					log.debug("Dealing with a hardware capsule...");
					log.debug("Making the call to the capsule's init method");
					((HardwareCapsule)o).init((Element)root.getChildren().get(0));
					((HardwareCapsule)o).setIcon(ImageIO.read(j.getInputStream(new ZipEntry("icon.png"))));
					((HardwareCapsule)o).setConfigElement(root);
					log.debug("Adding the initialized capsule to the pool");
					hardwareCapsules.add(((HardwareCapsule)o));
					log.debug("Added Hardware capsule at index "+(hardwareCapsules.size()-1));
					addActions(hardwareCapsules.size()-1, (HardwareCapsule)o);
					return ((HardwareCapsule)o);
				}else if(theNotificationClass.isInstance(o)){
					log.debug("Dealing with a notification capsule...");
					log.debug("Making the call to the capsule's init method");
					((NotificationCapsule)o).init((Element)root.getChildren().get(0));
					((NotificationCapsule)o).setIcon(ImageIO.read(j.getInputStream(new ZipEntry("icon.png"))));
					((NotificationCapsule)o).setConfigElement(root);
					notificationCapsules.add(((NotificationCapsule)o));
					log.debug("Added the initialized capsule to the pool");
					return ((NotificationCapsule)o);
				}else{
					log.error("Not a HC nor a NC, I don't know what to do with this");
					throw new InvalidCapsuleException("Invalid capsule configuration, "+o.getClass().getName() +
							" is not of HardwareCapsule or NotificationCapsule type.");
				}
			}			
		}catch(Exception e){
			throw new CapsuleInitException(e.getMessage(), o.getClass().getName());
		}
		return null;
	}
	
	public ArrayList<HardwareCapsule> getHardwareCapsules(){
		return hardwareCapsules;
	}
	
	public ArrayList<NotificationCapsule> getNotificationCapsules(){
		return notificationCapsules;
	}
	
	public NotificationCapsule getNotificationCapsuleAtIndex(int id){
		return notificationCapsules.get(id);
	}
	
	public int getNotifcationCapsulesCount(){
		return notificationCapsules.size();
	}
	
	/**Search the Actions hash and returns the ID of the capsule containing that action
	 * and the position of the action in it's action array
	 * @param	action	The action string to search for
	 * @return			A two position array containing the capsule ID and the action
	 * position inside the actions array of that capsule, or a two position array
	 * with values -1,-1 if the action was not found.*/
	public int[] actionToId(String action){
		if(actionToId.containsKey(action))
			return actionToId.get(action);
		else
			return new int[]{-1,-1};
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
	public HardwareCapsule getHardwareCapsuleAtIndex(int id){
		return hardwareCapsules.get(id);
	}
	
	/**Gets the number of hardware capsules that are ready to use (initialized)*/
	public int getHardwareCapsulesCount(){
		return hardwareCapsules.size();
	}
	
	private void addActions(int id, HardwareCapsule c){
		ArrayList<IAction> actSize = c.getActions();
		for(int i=0; i<actSize.size(); i++){
			IAction a = c.getActions().get(i);
			if(!(actionToId.containsKey(a.getName())))
				/*The actionToId hash uses the action name as key. The value
				 * is a two position array with the capsule id in the first
				 * position and the action position in the capsule's action array
				 * as second value.*/
				actionToId.put(a.getName(), new int[]{id,i});
			else
				log.debug("Action key "+a.getName()+" already present, not adding it!");
		}
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
