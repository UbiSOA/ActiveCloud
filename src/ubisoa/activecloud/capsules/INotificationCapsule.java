package ubisoa.activecloud.capsules;

import ubisoa.activecloud.exceptions.ReceiveException;

public interface INotificationCapsule extends ICapsule{
	/*Notification specific*/
	public void receive(byte[] payload) throws ReceiveException;
}
