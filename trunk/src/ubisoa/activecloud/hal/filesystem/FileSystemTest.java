package ubisoa.activecloud.hal.filesystem;

import org.apache.log4j.Logger;

public class FileSystemTest implements CapsuleEventListener{
	public static Logger log = Logger.getLogger(FileSystemTest.class);
	
	public void CapsuleEventOcurred(CapsuleEvent evt){
		if(evt.jarsAdded()){
			log.info("New JAR added");
			for(String path : evt.getAddedJars()){
				log.info(path);
			}
		} else {
			log.info("No new JARs added");
		}
		
		if(evt.jarsDeleted()){
			log.info("JAR deleted");
			for(String path : evt.getDeletedJars()){
				log.info(path);
			}
		} else {
			log.info("No JARs deleted");
		}
	}
}
