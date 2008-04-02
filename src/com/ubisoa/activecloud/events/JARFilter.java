package com.ubisoa.activecloud.events;

import java.io.File;
import java.io.FilenameFilter;

public class JARFilter implements FilenameFilter{
	public boolean accept(File dir, String name){
		if(name.endsWith(".jar") || name.endsWith(".JAR"))
			return true;
		else
			return false;
	}
}
