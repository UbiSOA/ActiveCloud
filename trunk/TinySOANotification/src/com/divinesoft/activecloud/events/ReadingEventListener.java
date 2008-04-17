package com.divinesoft.activecloud.events;

import java.util.EventListener;

public interface ReadingEventListener extends EventListener{
	public void readingReceived(ReadingEvent evt);
}
