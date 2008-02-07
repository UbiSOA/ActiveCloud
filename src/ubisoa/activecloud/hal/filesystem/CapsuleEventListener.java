package ubisoa.activecloud.hal.filesystem;

import java.util.EventListener;

public interface CapsuleEventListener extends EventListener{
	public void CapsuleEventOcurred(CapsuleEvent ce);
}
