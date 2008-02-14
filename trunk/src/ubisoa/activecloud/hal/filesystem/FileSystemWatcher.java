package ubisoa.activecloud.hal.filesystem;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import javax.imageio.ImageIO;
import javax.swing.event.EventListenerList;

import org.apache.log4j.Logger;

/**
 * Watch a directory for new files. A target directory is set and
 * a thread runs indefinitely, polling the filesystem for newly added files. If
 * a suitable file (e.g. JAR) is found, a NewFilesystemCapsule event is
 * raised.
 * */
public class FileSystemWatcher extends TimerTask{
    private static Logger log = Logger.getLogger(FileSystemWatcher.class);
    
    private EventListenerList listeners = new EventListenerList();
	private String folderToWatch;
	private File file;
	Map<String, File> listOfFiles = new HashMap<String, File>();
	Map<String, File> newListOfFiles = new HashMap<String, File>();
	Timer t;
	
	public FileSystemWatcher(){
		super();
	}
	
	/**Class constructor
	 * @param	folderToWatch	The folder that will be polled for added
	 * or deleted files*/
	public FileSystemWatcher(String folderToWatch){
		try{
			initFileSystem(folderToWatch);	
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}
	
	/**Start watching the directory*/
	public void run(){
		/*At this point, a second has passed since we first
		 * polled the folder for it's files, let's see if there
		 * are any new files*/
		Vector<String> toRemove = new Vector<String>();
		Vector<String> toAdd = new Vector<String>();
		
		if(file != null){
			
		}
		
		newListOfFiles = getFileMap(file.listFiles(new JARFilter()));
		System.gc();
		
		/*If listOfFiles DOES NOT contains everything from newListOfFiles
		 * there's a potential new File in there*/
		Set<String> newKeys = newListOfFiles.keySet();
		Iterator<String> iterator = newKeys.iterator();
		while(iterator.hasNext()){
			String theKey = iterator.next();
			
			/*If listOfFiles doesn't contains this key, it's a new file*/
			if(!listOfFiles.containsKey(theKey)){
				File newFile = newListOfFiles.get(theKey);
				listOfFiles.put(theKey, newFile);
				
				/*Fire NewJarAdded*/
				if(!newFile.isDirectory()){
					toAdd.add(theKey);
					log.info("New JAR: "+newFile.getName());
				}
			}
		} //end while
		
		Set<String> oldKeys = listOfFiles.keySet();
		iterator = oldKeys.iterator();
		while(iterator.hasNext()){
			String theKey = iterator.next();
			
			if(!newListOfFiles.containsKey(theKey)){
				File removedFile = listOfFiles.get(theKey);
				if(!(removedFile.exists() && removedFile.isDirectory())){
					/*Save the deleted files so when we are done iterating
					 * we'll remove them from the map*/
					toRemove.add(theKey);
					log.info("Deleted JAR: "+removedFile.getName());	
				}
			}
		} //end while
		
		/*Now remove those keys*/
		iterator = toRemove.iterator();
		while(iterator.hasNext()){
			listOfFiles.remove(iterator.next());
		}
		
		if(!(toAdd.isEmpty() && toRemove.isEmpty())){
			CapsuleEvent evt = new CapsuleEvent(this,(String[])toAdd.toArray(new String[toAdd.size()]), 
					(String[])toRemove.toArray(new String[toRemove.size()]));
			fireCapsuleEvent(evt);
		}
		
		System.gc();
	}
	
	/**
	 * Start watching the filesystem for added or deleted files
	 * @param	timeInterval	The time interval for polling the filesystem
	 * */
	public void startWatching(int timeInterval){
		
		t = new Timer();
		t.scheduleAtFixedRate(this, 0, timeInterval);
	}
	
	/**
	 * Stop watching the filesystem
	 * */
	public void stopWatching(){
		if(t != null)
			t.cancel();
	}
	
	/**
	 * Registers the object as a CapsuleEventListener. The object will receive
	 * CapsuleEvents fired.
	 * */
	public void addCapsuleEventListener(CapsuleEventListener evt){
		listeners.add(CapsuleEventListener.class, evt);
	}
	
	/**Removes the object from the CapsuleEventListener list*/
	public void removeCapsuleEventListener(CapsuleEventListener evt){
		listeners.remove(CapsuleEventListener.class, evt);
	}
	
	/**Fires a CapsuleEvent event*/
	protected void fireCapsuleEvent(CapsuleEvent evt){
		Object[] registeredListeners = listeners.getListenerList();
		for(int i=0; i<registeredListeners.length; i+=2){
			if(registeredListeners[i] == CapsuleEventListener.class){
				((CapsuleEventListener)registeredListeners[i+1]).CapsuleEventOcurred(evt);
			}
		}
	}
	
	/**
	 * Returns true if the file passed as parameter meets the requirements of a capsule.
	 * 
	 * In order for a file to be considered a capsule it must meet the following criteria:
	 * <ul>
	 * 	<li>Be a JAR file</li>
	 * 	<li>Have a config.xml file in its root folder</li>
	 * 	<li>Have an icon.png that represents the capsule in the GUI. This icon should
	 * 		measure 128x128</li>
	 * 	<li>The class specified in its config.xml file must implement ICapsule</li>
	 * </ul>
	 * @param	jarFile	The file to be verified as capsule
	 * @return	True if the file meets the criteria for a capsule
	 * */
	public static boolean isCapsule(File jarFile){
		try{
			//Check if this file is a JarFile
			JarFile capsule = new JarFile(jarFile);
			//check to see if there's a config.xml
			InputStream is = capsule.getInputStream(new ZipEntry("config.xml"));
			if(is != null){
				//check to see if there's a icon.png file that can be loaded
				BufferedImage bf = ImageIO.read(capsule.getInputStream(
						new ZipEntry("icon.png")));
				if(bf != null){
					return true;
				} else {
					//Couldn't read icon.png
					log.debug("Couldn't read icon.png from JAR");
					return false;
				}
			} else {
				//Couldn't read config.xml
				log.debug("Couldn't read config.xml from JAR");
				return false;
			}
		} catch (IOException ioe) {
			log.error(ioe.getMessage());
			return false;
		}
	}
	
	private Map<String, File> getFileMap(File[] files){
		Map<String, File> map = new HashMap<String, File>();
		for(File key: files){
			//Verify that the file is indeed a capsule, if not, don't bother
			if(FileSystemWatcher.isCapsule(key))
				map.put(key.getPath(), key);
		}
		return map;
	}
	
	public void initFileSystem(String path) throws Exception{
		/*This block gets the directory listing and adds every file
		 * to the listOfFiles map, using its path as the key and the
		 * file as the value*/
		this.folderToWatch = path;
		file = new File(this.folderToWatch);
		
		if(file.isDirectory()){
			listOfFiles = getFileMap(file.listFiles(new JARFilter()));
			/*This event correspond to those capsules already present in
			 * the filesystem*/
			CapsuleEvent evt = new CapsuleEvent(this,(String[])listOfFiles.keySet()
					.toArray(new String[listOfFiles.keySet().size()]),null);
			fireCapsuleEvent(evt);
		} else {
			throw new Exception("Given path is not a directory");
		}
	}
}
