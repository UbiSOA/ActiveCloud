package com.divinesoft.activecloud.events;

import java.util.EventListener;

public interface NetworkEventListener extends EventListener{
	public void networkListReceived(NetworkEvent evt);

}
