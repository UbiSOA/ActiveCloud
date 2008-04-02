package com.ubisoa.activecloud.capsules;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import org.apache.log4j.Logger;

/**
 * Modifies the classpath at runtime
 * */
public class ClassPathHacker {
    private static Logger log = Logger.getLogger(ClassPathHacker.class);
    
	@SuppressWarnings("unchecked")
	private static final Class[] parameters = new Class[]{URL.class};
	
	/**
	 * Add a new file to the running application classpath
	 * @param	s	String representing the path of the file to be added to the classpath
	 * */
	public static void addFile(String s) throws IOException{
		File f = new File(s);
		addFile(f);
	}
	
	/**
	 * Add a new file to the running application classpath
	 * @param	f	File to be added to the classpath
	 * */
	public static void addFile(File f) throws IOException{
		addURL(f.toURI().toURL());
	}
	
	/**
	 * Add a new object to the running application classpath
	 * @param 	u	The objects URL
	 * */
	public static void addURL(URL u) throws IOException{
		URLClassLoader sysloader = (URLClassLoader)ClassLoader.getSystemClassLoader();
		Class<URLClassLoader> sysclass = URLClassLoader.class;
		
		try{
			Method method = sysclass.getDeclaredMethod("addURL", parameters);
			method.setAccessible(true);
			method.invoke(sysloader, new Object[]{u});
			log.debug("Added "+u.toExternalForm()+" to the running classpath");
		} catch (Throwable t){
			log.error(t);
			throw new IOException("Error, could not add URL to system classloader");
		}
	}
}