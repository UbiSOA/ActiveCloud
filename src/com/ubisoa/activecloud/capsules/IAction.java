package com.ubisoa.activecloud.capsules;

import com.ubisoa.activecloud.exceptions.ActionInvokeException;


public interface IAction {
	public String getName();
	public String getDescription();
	public void run() throws ActionInvokeException;
}
