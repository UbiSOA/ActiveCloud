package ubisoa.activecloud.capsules;

import org.jdom.Element;

import ubisoa.activecloud.exceptions.ActionInvokeException;

public interface IAction {
	public String getName();
	public String getDescription();
	public void run(Element params) throws ActionInvokeException;
}
