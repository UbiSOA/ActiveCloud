package ubisoa.activecloud.services;

import hu.netmind.persistence.Store;

import java.io.File;

import org.apache.log4j.Logger;

/**
 * Database access service
 * */
public class StoreService{
	private static Logger log = Logger.getLogger(StoreService.class);
	/*org.apache.derby.jdbc.EmbeddedDriver is the embeeded derby Driver*/
	private Store store;
	private static StoreService singleton;
	
	private StoreService(){
		/*Clean the DB (by deleting its directory)*/
		deleteDir(new File("db"));
		store = new Store("org.apache.derby.jdbc.EmbeddedDriver",
		"jdbc:derby:db;create=true");
	}

	public boolean stop() {
		store.close();
		return true;
	}
	
    private boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
    
        // The directory is now empty so delete it
        return dir.delete();
    }
    
    public static StoreService get(){
    	if(singleton == null){
    		singleton = new StoreService();
    	}
    	return singleton;
    }
    
    public Store getStore(){
    	return store;
    }
}
