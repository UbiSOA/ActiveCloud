package com.divinesoft.activecloud.events;

import java.util.EventObject;
import java.util.Vector;

import net.tinyos.tinysoa.common.Network;

public class NetworkEvent extends EventObject{
	private Vector<Network> networks;
	
	public NetworkEvent(Object source, Vector<Network> networks) {
		super(source);
		this.networks = networks;
	}
	
	public Vector<Network> getNetworks(){
		return networks;
	}

}
