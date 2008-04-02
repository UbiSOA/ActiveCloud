package com.ubisoa.activecloud.events;

import java.util.EventListener;


public interface FileSystemEventListener extends EventListener{
	public void fileSystemEventOcurred(FileSystemEvent ce);
}
