package ubisoa.activecloud.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Element;

import ubisoa.activecloud.capsules.IHardwareCapsule;
import ubisoa.activecloud.capsules.INotificationCapsule;
import ubisoa.activecloud.exceptions.CapsuleInitException;

public class DeliveryService{
	private static DeliveryService singleton;
	private static Logger log = Logger.getLogger(DeliveryService.class);
	private ArrayList<List<INotificationCapsule>> observers;
	private HashMap<IHardwareCapsule,Integer> publishers;
	
	private DeliveryService(){
		observers = new ArrayList<List<INotificationCapsule>>();
		publishers = new HashMap<IHardwareCapsule,Integer>();
	}
	
	public void publish(Element payload){
		String capsuleName = payload.getAttributeValue("class");
		try{
			int id = NodeAccessService.get().getHardwareCapsuleId(capsuleName);
			log.debug("Selecting observer index "+id+ " of "+ observers.size());
			
			List<INotificationCapsule> obs;
			if(!observers.isEmpty() && observers.size()>=id){
				obs = observers.get(id);
				log.debug("Selected Observer: "+obs.getClass().getName());
				
				for(INotificationCapsule nc : obs){
					nc.receive(payload);
				}
			}
		}catch(CapsuleInitException cie){
			log.error("Could not deliver message, skipping");
		}
	}
	
	public void publish(IHardwareCapsule p, byte[] payload){
		/*
		 * Get registered observers and execute its receive method
		 * */
		int id = getHardwareCapsuleId(p);
		log.debug("Selecting observer index "+id+ " of "+ observers.size());
		List<INotificationCapsule> obs;
		
		if(!observers.isEmpty() && observers.size()>=id){
			obs = observers.get(id);
			log.debug("Selected Observer: "+obs.getClass().getName());
			
			for(INotificationCapsule nc : obs){
				nc.receive(payload);
			}
		}
	}
	
	public void publish(IHardwareCapsule p, Element payload){
		/*
		 * Get registered observers and execute its receive method
		 * */
		int id = getHardwareCapsuleId(p);
		log.debug("Selecting observer index "+id+ " of "+ observers.size());
		List<INotificationCapsule> obs;
		
		if(!observers.isEmpty() && observers.size()>=id){
			obs = observers.get(id);
			log.debug("Selected Observer: "+obs.getClass().getName());
			
			for(INotificationCapsule nc : obs){
				nc.receive(payload);
			}
		}
	}
	
	private int getHardwareCapsuleId(IHardwareCapsule p){
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
	
	public void registerObserver(INotificationCapsule nc, IHardwareCapsule p){
		/*
		 * If there are no observers registered for this hc, create a new list
		 * and add it in the corresponding slot
		 * */
		int id = getHardwareCapsuleId(p);
		if(observers.size() < id){
			observers.ensureCapacity(id);
			List<INotificationCapsule> temp = new ArrayList<INotificationCapsule>();
			temp.add(nc);
			observers.set(id, temp);
		} else {
			/*
			 * If there are observers for that id, get the list and append the new
			 * observer
			 * */
			
			observers.get(id).add(nc);
			
			/*
			List<INotificationCapsule> temp = observers.get(id);
			temp.add(nc);
			observers.set(id, temp);*/
		}
	}
	
	public ArrayList<List<INotificationCapsule>> getObservers() {
		return observers;
	}
	
	public List<INotificationCapsule> getObservers(int id){
		if(!observers.isEmpty() && observers.size()>=id){
			return observers.get(id);	
		}
		return null;
	}
	
	public List<INotificationCapsule> getObservers(IHardwareCapsule p){
		int id = getHardwareCapsuleId(p);
		if(!observers.isEmpty() && observers.size()>=id){
			return observers.get(id);	
		}
		return null;
	}
	
	public List<INotificationCapsule> getObservers(String capsule){
		try{
			if(NodeAccessService.get().isCapsuleLoaded(capsule)){
				int id = NodeAccessService.get().getHardwareCapsuleId(capsule);
				if(!observers.isEmpty() && observers.size() >= id)
					return observers.get(id);
			}			
		}catch(CapsuleInitException cie){
			log.error(cie.getMessage());
		}
		return null;
	}
	
	public static DeliveryService get(){
		if(singleton == null){
			log.debug("Instantiating "+DeliveryService.class.getName());
			singleton = new DeliveryService();
		}
		return singleton;
	}
}
