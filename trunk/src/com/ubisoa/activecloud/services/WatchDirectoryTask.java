package com.ubisoa.activecloud.services;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TimerTask;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.ubisoa.activecloud.events.FileSystemEvent;
import com.ubisoa.activecloud.events.JARFilter;


public class WatchDirectoryTask extends TimerTask{
	private Map<String, File> listOfFiles = new HashMap<String, File>();
	private Map<String, File> newListOfFiles = new HashMap<String, File>();

    private static Logger log = Logger.getLogger(WatchDirectoryTask.class);
	private File file;
	
	public WatchDirectoryTask(String path){
		super();
		try{
			initFileSystem(path);	
		}catch(Exception e){
			log.error(e.getMessage());
		}
	}
	
	/**Start watching the directory*/
	@Override
	public void run(){
		/*At this point, a second has passed since we first
		 * polled the folder for it's files, let's see if there
		 * are any new files*/
		Vector<String> toRemove = new Vector<String>();
		Vector<String> toAdd = new Vector<String>();
		
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
			FileSystemEvent evt = new FileSystemEvent(this,(String[])toAdd.toArray(new String[toAdd.size()]), 
					(String[])toRemove.toArray(new String[toRemove.size()]));
			FileSystemService.get().fireFileSystemEvent(evt);
		}
		
		System.gc();
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
	
	private void initFileSystem(String path) throws Exception{
		/*This block gets the directory listing and adds every file
		 * to the listOfFiles map, using its path as the key and the
		 * file as the value*/
		file = new File(path);
		
		if(file.isDirectory()){
			listOfFiles = getFileMap(file.listFiles(new JARFilter()));
			/*This event correspond to those capsules already present in
			 * the filesystem*/
			FileSystemEvent evt = new FileSystemEvent(this,(String[])listOfFiles.keySet()
					.toArray(new String[listOfFiles.keySet().size()]),null);
			FileSystemService.get().fireFileSystemEvent(evt);
		} else {
			throw new Exception("Given path is not a directory");
		}
	}
}
