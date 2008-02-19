package ubisoa.activecloud.services;

import hu.netmind.persistence.StoreException;

import java.util.List;

import org.apache.log4j.Logger;

import ubisoa.activecloud.hal.capsules.Capsule;

/**
 * The Node Access Service (NAS) maintains the hardware repository and keeps the services
 * available to the end user updated. NodeAccessService is a singleton class*/
public final class NodeAccessService{
	private static NodeAccessService singleton;
	private static Logger log = Logger.getLogger(NodeAccessService.class);
	
	private NodeAccessService(){}
	
	@SuppressWarnings("unchecked")
	public boolean isCapsuleLoaded(Capsule capsule){
		List capsules = StoreService.get().getStore().find("find capsule where className = ?", 
				new Object[]{capsule.getClassName()});
		if(capsules.isEmpty())
			return false;
		else
			return true;
	}
	
	public void saveCapsule(Capsule capsule){
		try{
			StoreService.get().getStore().save(capsule);
			log.info("Saved capsule "+capsule.getClassName());
		} catch (StoreException ste) {
			log.error(ste.getMessage());
		}
	}

	public static NodeAccessService get(){
		if(singleton == null){
			singleton = new NodeAccessService();
		}
		return singleton;
	}
}
