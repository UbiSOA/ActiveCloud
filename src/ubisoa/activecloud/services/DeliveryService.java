package ubisoa.activecloud.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Element;

import ubisoa.activecloud.capsules.HardwareCapsule;
import ubisoa.activecloud.capsules.NotificationCapsule;
import ubisoa.activecloud.exceptions.CapsuleInitException;

public class DeliveryService{
	private static DeliveryService singleton;
	private static Logger log = Logger.getLogger(DeliveryService.class);
	private ArrayList<List<NotificationCapsule>> observers;
	private HashMap<HardwareCapsule,Integer> publishers;
	
	private DeliveryService(){
		observers = new ArrayList<List<NotificationCapsule>>();
		publishers = new HashMap<HardwareCapsule,Integer>();
	}
	
	public void publish(Element payload){
		String capsuleName = payload.getAttributeValue("class");
		if(capsuleName == null)
			return;
		try{
			int id = NodeAccessService.get().getHardwareCapsuleId(capsuleName);
			log.debug("Selecting observer index "+id);
			
			List<NotificationCapsule> obs;
			if(!observers.isEmpty() && observers.size()>=id){
				obs = observers.get(id);
				
				for(NotificationCapsule nc : obs){
					nc.receive(payload);
				}
			}else{
				log.debug("No registered observer found for "+capsuleName);
			}
		}catch(CapsuleInitException cie){
			log.error("Could not deliver message, skipping");
		}
	}
	
	public void publish(HardwareCapsule p, byte[] payload){
		/*
		 * Get registered observers and execute its receive method
		 * */
		int id = getHardwareCapsuleId(p);
		log.debug("Selecting observer index "+id);
		List<NotificationCapsule> obs;
		
		if(!observers.isEmpty() && observers.size()>=id){
			obs = observers.get(id);
			log.debug("Selected Observer: "+obs.getClass().getName());
			
			for(NotificationCapsule nc : obs){
				nc.receive(payload);
			}
		}
	}
	
	public void publish(HardwareCapsule p, Element payload){
		/*
		 * Get registered observers and execute its receive method
		 * */
		int id = getHardwareCapsuleId(p);
		log.debug("Selecting observer index "+id);
		List<NotificationCapsule> obs;
		
		if(!observers.isEmpty() && observers.size()>=id){
			obs = observers.get(id);
			log.debug("Selected Observer: "+obs.getClass().getName());
			
			for(NotificationCapsule nc : obs){
				nc.receive(payload);
			}
		}
	}
	
	private int getHardwareCapsuleId(HardwareCapsule p){
		int id = -1;
		if(publishers.containsKey(p))
			id = publishers.get(p);
		else{
			try{
				id = NodeAccessService.get().getHardwareCapsuleId(p.getClass().getName());	
			}catch(CapsuleInitException cie){
				log.error(cie.getMessage());
			}
		}
		return id;
	}
	
	public void registerObserver(NotificationCapsule nc, HardwareCapsule p){
		/*
		 * If there are no observers registered for this hc, create a new list
		 * and add it in the corresponding slot
		 * */
		log.debug("Registering "+nc.getClass().getCanonicalName()+" as observer for " +
				p.getClass().getCanonicalName()+ " messages");
		int id = getHardwareCapsuleId(p);
		log.debug("Got ID "+id+" of the HardwareCapsule");
		log.debug("Current Observer size: "+observers.size());
		
		if((observers.size() < id) || (observers.isEmpty())){
			observers.ensureCapacity(id+1);
			List<NotificationCapsule> temp = new ArrayList<NotificationCapsule>();
			temp.add(nc);
			//observers.set(id, temp);
			observers.add(id, temp);
			log.debug("Done registering observer");
		} else {
			/*
			 * If there are observers for that id, get the list and append the new
			 * observer
			 * */
			log.debug("The Observers array is big enough ("+observers.size()+"), saving...");
			observers.get(id).add(nc);
			
			/*
			List<NotificationCapsule> temp = observers.get(id);
			temp.add(nc);
			observers.set(id, temp);*/
		}
	}
	
	public ArrayList<List<NotificationCapsule>> getObservers() {
		return observers;
	}
	
	public List<NotificationCapsule> getObservers(int id){
		if(!observers.isEmpty() && observers.size()>=id){
			return observers.get(id);	
		}
		return new ArrayList<NotificationCapsule>();
	}
	
	public List<NotificationCapsule> getObservers(HardwareCapsule p){
		int id = getHardwareCapsuleId(p);
		if(!observers.isEmpty() && observers.size()>=id){
			return observers.get(id);	
		}
		return new ArrayList<NotificationCapsule>();
	}
	
	public List<NotificationCapsule> getObservers(String capsule){
		try{
			if(NodeAccessService.get().isCapsuleLoaded(capsule)){
				int id = NodeAccessService.get().getHardwareCapsuleId(capsule);
				if(!observers.isEmpty() && observers.size() >= id)
					return observers.get(id);
			}			
		}catch(CapsuleInitException cie){
			log.error(cie.getMessage());
		}
		return new ArrayList<NotificationCapsule>();
	}
	
	public static DeliveryService get(){
		if(singleton == null){
			log.debug("Instantiating "+DeliveryService.class.getName());
			singleton = new DeliveryService();
		}
		return singleton;
	}
}
