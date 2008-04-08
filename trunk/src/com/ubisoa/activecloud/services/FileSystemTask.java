package com.ubisoa.activecloud.services;

public interface FileSystemTask {
	public void run();
	public String[] getPaths();
	public void setPaths(String[] path);
}
