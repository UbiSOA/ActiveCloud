package com.ubisoa.activecloud.services.frontends;

import org.apache.log4j.Logger;

import bsh.Interpreter;

import com.ubisoa.activecloud.services.Frontend;


/**Implements a Web Service Frontend. A wrapper around TinySOAServer*/
public class WebServiceFrontend implements Frontend{
	private static Logger log = Logger.getLogger(WebServiceFrontend.class);
	private Interpreter interpreter = new Interpreter();
	
	public void start() {

	}

	public void stop() {

	}
}
