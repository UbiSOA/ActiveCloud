package com.ubisoa.activecloud.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.event.EventListenerList;

import org.apache.log4j.Logger;
import org.jdom.Element;

import com.ubisoa.activecloud.capsules.HardwareCapsule;
import com.ubisoa.activecloud.capsules.ICapsule;
import com.ubisoa.activecloud.capsules.NotificationCapsule;
import com.ubisoa.activecloud.exceptions.CapsuleInitException;
import com.ubisoa.activecloud.exceptions.ReceiveException;


public class DeliveryService{
	private static DeliveryService singleton;
	private static Logger log = Logger.getLogger(DeliveryService.class);
	private ArrayList<List<DeliveryLink>> observers;
	private HashMap<HardwareCapsule,Integer> publishers;
	private EventListenerList listeners = new EventListenerList();
	
	private DeliveryService(){
		observers = new ArrayList<List<DeliveryLink>>();
		publishers = new HashMap<HardwareCapsule,Integer>();
	}
	
	public void publish(Element payload){
		String capsuleName = payload.getAttributeValue("class");
		if(capsuleName == null){
			return;
		}
		
		try{
			int id = NodeAccessService.get().getHardwareCapsuleId(capsuleName);
			log.debug("Selecting observer index "+id);
			
			List<DeliveryLink> obs = null;
			if(!observers.isEmpty() && observers.size()>=id){
				obs = observers.get(id);
				
				doPublish(obs,payload);		
				
			}else{
				log.debug("No registered observer found for "+capsuleName);
			}
		}catch(CapsuleInitException cie){
			log.error("Could not deliver message, skipping");
		}
	}
	
	public void publish(HardwareCapsule p, Element payload){
		/*
		 * Get registered observers and execute its receive method
		 * */
		int id = getHardwareCapsuleId(p);
		log.debug("Selecting observer index "+id);
		List<DeliveryLink> obs = null;
		
		if(!observers.isEmpty() && observers.size()>=id){
			obs = observers.get(id);
			log.debug("Selected Observer: "+obs.getClass().getName());
			
			doPublish(obs,payload);		
		}
	}
	
	private static void doPublish(final List<DeliveryLink> obs, final Element payload){
		for(final DeliveryLink nc : obs){
			nc.getNotificationCapsule().setConfigElement(nc.getConfig());
			log.debug("doPublish, about to send payload to "+
					nc.getNotificationCapsule().getClass().getName());
			
			/*A new thread is created for each notificator, this way if one of them
			 * raises an exception, it doesn't interfere with the others*/
			new Thread(){
				public void run(){
					try{
						nc.getNotificationCapsule().receive(payload);	
					}catch(ReceiveException re){
						log.error(re.getMessage());
					}catch(Exception e){
						log.error(e.getMessage());
					}
				}
			}.run();
		}
	}
	
	private int getHardwareCapsuleId(HardwareCapsule p){
		int id = -1;
		if(publishers.containsKey(p)){
			id = publishers.get(p);
		} else {
			try{
				id = NodeAccessService.get().getHardwareCapsuleId(p.getClass().getName());	
			}catch(CapsuleInitException cie){
				log.error(cie.getMessage());
			}
		}
		return id;
	}
	
	public void registerObserver(DeliveryLink deliveryLink, HardwareCapsule p){
		/*
		 * If there are no observers registered for this hc, create a new list
		 * and add it in the corresponding slot
		 * */
		if(deliveryLink != null && p != null){
			log.debug("Registering "+deliveryLink.getClass().getCanonicalName()+" (DeliveryLink) " +
					"as observer for " + p.getClass().getCanonicalName()+ " messages");
			int id = getHardwareCapsuleId(p);
			log.debug("Got ID "+id+" of the HardwareCapsule");
			log.debug("Current Observer size: "+observers.size());
			
			if((observers.size() < id) || (observers.isEmpty())){
				observers.ensureCapacity(id+1);
				List<DeliveryLink> temp = new ArrayList<DeliveryLink>();
				temp.add(deliveryLink);
				observers.add(id, temp);
				log.debug("Done registering observer (DeliveryLink)");
			} else {
				/*
				 * If there are observers for that id, get the list and append the new
				 * observer
				 * */
				log.debug("The Observers array is big enough ("+observers.size()+"), " +
						"saving...");
				observers.get(id).add(deliveryLink);

			}	
		}
	}
	
	public void registerObserver(NotificationCapsule nc, HardwareCapsule p){
		/*
		 * If there are no observers registered for this hc, create a new list
		 * and add it in the corresponding slot
		 * */
		if(nc != null && p != null){
			log.debug("Registering "+nc.getClass().getCanonicalName()+" as observer for " +
					p.getClass().getCanonicalName()+ " messages");
			int id = getHardwareCapsuleId(p);
			log.debug("Got ID "+id+" of the HardwareCapsule");
			log.debug("Current Observer size: "+observers.size());
			
			if((observers.size() < id) || (observers.isEmpty())){
				observers.ensureCapacity(id+1);
				List<DeliveryLink> temp = new ArrayList<DeliveryLink>();
				temp.add(new DeliveryLink(nc));
				observers.add(id, temp);
				log.debug("Done registering observer");
			} else {
				/*
				 * If there are observers for that id, get the list and append the new
				 * observer
				 * */
				log.debug("The Observers array is big enough ("+observers.size()+"), saving...");
				observers.get(id).add(new DeliveryLink(nc));
			}	
		}
	}
	
	public void clearObservers(){
		log.debug("Clearing all registered observers");
		observers.clear();
	}
	
	public void clearObservers(HardwareCapsule p){
		log.debug("Unregistering all observers for "+p.getClass().getCanonicalName());
		getObservers(p).clear();
	}
	
	public void removeObserver(HardwareCapsule p, DeliveryLink d){
		if(p != null && d != null){
			getObservers(p).remove(d);	
		}
	}
	
	public void removeObserver(HardwareCapsule p, int observerId){
		if(p != null){
			getObservers(p).remove(observerId);	
		}
	}
	
	public void removeObserver(NotificationCapsule n, HardwareCapsule p){
		if(n != null && p != null){
			List<DeliveryLink> observers = getObservers(p); 
			int i = 0;
			boolean toDelete = false;
			for(DeliveryLink d : observers){
				if(d.getNotificationCapsule() == n){
					toDelete = true;
					break;
				}
				i++;
			}
			if(toDelete){
				getObservers(p).remove(i);
			}	
		}
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<List<DeliveryLink>> getObservers() {
		return (ArrayList<List<DeliveryLink>>) observers.clone();
	}
	
	public List<DeliveryLink> getObservers(int id){
		if(!observers.isEmpty() && observers.size()>=id){
			return observers.get(id);	
		}
		return new ArrayList<DeliveryLink>();
	}
	
	public List<DeliveryLink> getObservers(HardwareCapsule p){
		int id = getHardwareCapsuleId(p);
		if(!observers.isEmpty() && observers.size()>=id){
			return observers.get(id);	
		}
		return new ArrayList<DeliveryLink>();
	}
	
	public List<DeliveryLink> getObservers(String name){
		try{
			if(name.indexOf('.') == -1){
				ICapsule c = NodeAccessService.get().getHardwareCapsule(name);
				if(c != null){
					int id = NodeAccessService.get().getHardwareCapsuleId(c.getClass().getCanonicalName());
					if(!observers.isEmpty() && observers.size() >= id){
						return observers.get(id);
					}
				}
			}else{
				if(NodeAccessService.get().isCapsuleLoaded(name)){
					int id = NodeAccessService.get().getHardwareCapsuleId(name);
					if(!observers.isEmpty() && observers.size() >= id){
						return observers.get(id);
					}
				}
			}
		
		}catch(CapsuleInitException cie){
			log.error(cie.getMessage());
		}
		return new ArrayList<DeliveryLink>();
	}
	
	public static DeliveryService get(){
		if(singleton == null){
			log.debug("Instantiating "+DeliveryService.class.getName());
			singleton = new DeliveryService();
		}
		return singleton;
	}
}