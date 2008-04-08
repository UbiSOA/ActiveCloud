package com.ubisoa.activecloud.services;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import javax.imageio.ImageIO;
import javax.swing.event.EventListenerList;

import org.apache.log4j.Logger;

import com.ubisoa.activecloud.events.FileSystemEvent;
import com.ubisoa.activecloud.events.FileSystemEventListener;
import com.ubisoa.activecloud.events.JARFilter;


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
	private FileSystemTask task;
	
	public void addFileSystemTask(FileSystemTask task){
		this.task = task;
	}
	
	/**
	 * Start watching the filesystem for added or deleted files
	 * @param	timeInterval	The time interval for polling the filesystem
	 * */
	public void start(String[] path){
		if(task != null){
			task.setPaths(path);
			task.run();	
			initialScan();
		}
	}
	
	/**Do an initial scan of the paths so previously added capsules that are
	 * already in those paths get loaded*/
	private void initialScan(){
		if(task.getPaths() != null){
			for(String p : task.getPaths()){
				/*This block gets the directory listing and adds every file
				 * to the listOfFiles map, using its path as the key and the
				 * file as the value*/
				File file = new File(p);
				
				if(file.isDirectory()){
					Map<String, File> listOfFiles = getFileMap(file.listFiles(
							new JARFilter()));
					/*This event correspond to those capsules already present in
					 * the filesystem*/
					FileSystemEvent evt = new FileSystemEvent(this,(String[])listOfFiles.keySet()
							.toArray(new String[listOfFiles.keySet().size()]),null);
					FileSystemService.get().fireFileSystemEvent(evt);
				} else {
					log.error(p+" is not a directory");
				}
			}	
		}
	}
	
	private Map<String, File> getFileMap(File[] files){
		Map<String, File> map = new HashMap<String, File>();
		for(File key: files){
			//Verify that the file is indeed a capsule, if not, don't bother
			if(FileSystemService.isCapsule(key))
				map.put(key.getPath(), key);
		}
		return map;
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
	 * 	<li>The class specified in its config.xml file must implement HardwareCapsule</li>
	 * </ul>
	 * @param	jarFile	The file to be verified as capsule
	 * @return	True if the file meets the criteria for a capsule
	 * */
	public static boolean isCapsule(File jarFile){
		try{
			//Check if this file is a JarFile
			log.debug("Trying to open "+jarFile.getAbsolutePath());
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
}
