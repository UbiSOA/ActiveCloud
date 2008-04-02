package com.ubisoa.activecloud.services;

import org.apache.log4j.Logger;

public class NetworkStateService {
	private static Logger log = Logger.getLogger(NetworkStateService.class);
	private static NetworkStateService singleton;
	
	private NetworkStateService(){
		
	}
	
	public void persist(){
		
	}
	
	public NetworkStateService get(){
		if(singleton == null){
			log.debug("Instantiating "+NetworkStateService.class.getName());
			singleton = new NetworkStateService();
		}
		return singleton;
	}

}
