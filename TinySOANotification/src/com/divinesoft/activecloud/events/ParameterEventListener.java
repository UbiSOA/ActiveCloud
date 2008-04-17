package com.divinesoft.activecloud.events;

import java.util.EventListener;

public interface ParameterEventListener extends EventListener{
	public void parameterListReceived(ParameterEvent evt);
}
