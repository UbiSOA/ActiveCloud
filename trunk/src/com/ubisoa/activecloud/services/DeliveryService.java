package com.ubisoa.activecloud.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Element;

import com.ubisoa.activecloud.capsules.HardwareCapsule;
import com.ubisoa.activecloud.capsules.IRule;
import com.ubisoa.activecloud.capsules.NotificationCapsule;
import com.ubisoa.activecloud.exceptions.CapsuleInitException;


public class DeliveryService{
	private static DeliveryService singleton;
	private static Logger log = Logger.getLogger(DeliveryService.class);
	private ArrayList<List<DeliveryLink>> observers;
	private HashMap<HardwareCapsule,Integer> publishers;
	
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
	
	private static void doPublish(List<DeliveryLink> obs, Element payload){
		for(DeliveryLink nc : obs){
			nc.getNotificationCapsule().setConfigElement(nc.getConfig());
			nc.getNotificationCapsule().receive(payload);
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
	
	public void registerObserver(DeliveryLink deliveryLink, HardwareCapsule p,
			List<IRule> rules){
		for(IRule r : rules){
			deliveryLink.addRule(r);
		}
		registerObserver(deliveryLink, p);
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
	
	public void clearObservers(){
		log.debug("Clearing all registered observers");
		observers.clear();
	}
	
	public void clearObservers(HardwareCapsule p){
		log.debug("Unregistering all observers for "+p.getClass().getCanonicalName());
		getObservers(p).clear();
	}
	
	public void removeObserver(HardwareCapsule p, DeliveryLink d){
		getObservers(p).remove(d);
	}
	
	public void removeObserver(HardwareCapsule p, int observerId){
		getObservers(p).remove(observerId);
	}
	
	public void addRule(NotificationCapsule nc, HardwareCapsule hc, IRule rule){
		//See if there's a DeliveryLink for that combo
		List<DeliveryLink> deliveryLinks = getObservers(hc);
		log.debug("Iterating DeliveryLinks...");
		for(DeliveryLink d : deliveryLinks){
			if(d.getRules().contains(rule)){
				log.debug("DeliveryLink found, adding rule");
				d.addRule(rule);
			}
		}
	}
	
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
	
	public List<DeliveryLink> getObservers(String capsule){
		try{
			if(NodeAccessService.get().isCapsuleLoaded(capsule)){
				int id = NodeAccessService.get().getHardwareCapsuleId(capsule);
				if(!observers.isEmpty() && observers.size() >= id){
					return observers.get(id);
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