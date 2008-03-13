package ubisoa.activecloud.events;

import java.util.EventListener;

public interface HardwareCapsuleEventListener extends EventListener{
	public void hardwareCapsuleEventOcurred(HardwareCapsuleInitEvent hce);
}
