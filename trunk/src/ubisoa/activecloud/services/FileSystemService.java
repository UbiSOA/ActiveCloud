package ubisoa.activecloud.services;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import javax.imageio.ImageIO;
import javax.swing.event.EventListenerList;

import org.apache.log4j.Logger;

import ubisoa.activecloud.events.FileSystemEvent;
import ubisoa.activecloud.events.FileSystemEventListener;

/**
 * Watch a directory for new files. A target directory is set and
 * a thread runs indefinitely, polling the filesystem for newly added files. If
 * a suitable file (e.g. JAR) is found, a NewFilesystemCapsule event is
 * raised.
 * */
public class FileSystemService{
	private static FileSystemService singleton;
    private static Logger log = Logger.getLogger(FileSystemService.class);
	private boolean running;
    private EventListenerList listeners = new EventListenerList();
	private Timer t;
	
	private FileSystemService(){
		super();
	}
	
	/**
	 * Start watching the filesystem for added or deleted files
	 * @param	timeInterval	The time interval for polling the filesystem
	 * */
	public void start(int timeInterval, String folderToWatch){
		
		t = new Timer();
		t.scheduleAtFixedRate(new WatchDirectoryTask(folderToWatch), 0, timeInterval);
		running = true;

	}
	
	/**
	 * Stop watching the filesystem
	 * */
	public boolean stop(){
		if(t != null){
			t.cancel();
			running = false;
			return true;
		}
		return false;
	}
	
	public boolean isRunning(){
		return running;
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
	 * 	<li>The class specified in its config.xml file must implement IHardwareCapsule</li>
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
	
	/**
	 * Registers the object as a FileSystemEventListener. The object will receive
	 * CapsuleEvents fired.
	 * */
	public void addFileSystemEventListener(FileSystemEventListener evt){
		listeners.add(FileSystemEventListener.class, evt);
	}
	
	/**Removes the object from the FileSystemEventListener list*/
	public void removeFileSystemEventListener(FileSystemEventListener evt){
		listeners.remove(FileSystemEventListener.class, evt);
	}
	
	/**Fires a FileSystemEvent event*/
	protected void fireFileSystemEvent(FileSystemEvent evt){
		Object[] registeredListeners = listeners.getListenerList();
		for(int i=0; i<registeredListeners.length; i+=2){
			if(registeredListeners[i] == FileSystemEventListener.class){
				((FileSystemEventListener)registeredListeners[i+1]).fileSystemEventOcurred(evt);
			}
		}
	}
	
	public static FileSystemService get(){
		if(singleton == null){
			log.debug("Instantiating "+FileSystemService.class.getName());
			singleton = new FileSystemService();
		}
		return singleton;
	}
	
	public static void main(String[] args){
		FileSystemService.get().start(1000, "/home/cesar/media");
		FileSystemService.get().start(3000, "/home/cesar/Desktop");
	}
}
