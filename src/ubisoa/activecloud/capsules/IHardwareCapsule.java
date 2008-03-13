package ubisoa.activecloud.capsules;

import java.util.ArrayList;

import ubisoa.activecloud.exceptions.SendException;

public interface IHardwareCapsule extends ICapsule{
	/*Hardware specific*/
	public void send(byte[] payload) throws SendException;
	public ArrayList<IAction> getActions();
	
}
