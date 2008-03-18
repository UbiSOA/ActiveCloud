package ubisoa.activecloud.capsules;

import java.util.ArrayList;

public interface IHardwareCapsule extends ICapsule{
	/*Hardware specific*/
	public ArrayList<IAction> getActions();
	
}
