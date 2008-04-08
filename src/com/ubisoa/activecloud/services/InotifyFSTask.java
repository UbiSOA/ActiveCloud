package com.ubisoa.activecloud.services;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import net.contentobjects.jnotify.JNotify;
import net.contentobjects.jnotify.JNotifyException;
import net.contentobjects.jnotify.JNotifyListener;

import org.apache.log4j.Logger;

import com.ubisoa.activecloud.events.FileSystemEvent;

public class InotifyFSTask implements FileSystemTask, JNotifyListener {
	private static Logger log = Logger.getLogger(InotifyFSTask.class);
	private String[] paths;
	
	@Override
	public void run() {
		
		int mask =  JNotify.FILE_CREATED | JNotify.FILE_DELETED | 
			JNotify.FILE_MODIFIED | JNotify.FILE_RENAMED;
		
		boolean watchSubtree = false;
		
		for(String p : paths){
			try{
				log.debug("Watch ID: "+JNotify.addWatch(p, mask, watchSubtree, this));
			}catch(JNotifyException jne){
				log.error(jne.getMessage());
			}
		}
	}

	@Override
	public void fileCreated(int arg0, String arg1, String arg2) {
		log.debug(arg1 + " " + arg2);
		if(FileSystemService.isCapsule(new File(arg1+File.separator+arg2))){
			FileSystemEvent evt = new FileSystemEvent(this,new String[]{arg1+File.separator+arg2},null);
			FileSystemService.get().fireFileSystemEvent(evt);
		}
	}

	@Override
	public void fileDeleted(int arg0, String arg1, String arg2) {
		log.debug("Deleted "+arg2);
	}

	@Override
	public void fileModified(int arg0, String arg1, String arg2) {
		log.debug("Modified "+arg2);
	}

	@Override
	public void fileRenamed(int arg0, String arg1, String arg2, String arg3) {
		log.debug("Renamed "+arg2+" to "+arg3);
		if(FileSystemService.isCapsule(new File(arg1+File.separator+arg3))){
			FileSystemEvent evt = new FileSystemEvent(this,new String[]{arg1+File.separator+arg3},null);
			FileSystemService.get().fireFileSystemEvent(evt);
		}
	}

	@Override
	public String[] getPaths() {
		return paths;
	}

	@Override
	public void setPaths(String[] path) {
		this.paths = path;
	}
}
