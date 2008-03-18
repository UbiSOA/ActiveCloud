package ubisoa.activecloud.capsules;

import org.jdom.Element;

import ubisoa.activecloud.exceptions.ReceiveException;

public interface INotificationCapsule extends ICapsule{
	/*Notification specific*/
	public void receive(byte[] payload) throws ReceiveException;
	public void receive(Element payload) throws ReceiveException;
}
