package ubisoa.activecloud.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import ubisoa.activecloud.capsules.INotificationCapsule;

public class DeliveryService{
	private static DeliveryService singleton;
	private static Logger log = Logger.getLogger(DeliveryService.class);
	private ArrayList<List<INotificationCapsule>> observers;
	
	private DeliveryService(){
		observers = new ArrayList<List<INotificationCapsule>>();
	}
	
	public void publish(int id, byte[] payload){
		/*
		 * Get registered observers and execute its receive method
		 * */
		log.debug("Selecting observer index "+id+ " of "+ observers.size());
		List<INotificationCapsule> obs = observers.get(id);
		log.debug("Selected Observer: "+obs.getClass().getName());
		
		for(INotificationCapsule nc : obs){
			nc.receive(payload);
		}
	}
	
	public void registerObserver(INotificationCapsule nc, int id){
		/*
		 * If there are no observers registered for this hc, create a new list
		 * and add it in the corresponding slot
		 * */
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
	
	public static DeliveryService get(){
		if(singleton == null){
			log.debug("Creating DeliveryService instance");
			singleton = new DeliveryService();
		}
		return singleton;
	}
}
