package com.ubisoa.activecloud.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.log4j.Logger;

import bsh.EvalError;
import bsh.Interpreter;

public class QueryProcessorService {
	private static QueryProcessorService singleton;
	private static Logger log = Logger.getLogger(QueryProcessorService.class);
	private static Interpreter interpreter;
	
	private QueryProcessorService(){
		interpreter = new Interpreter();
	}
	
	public void eval(String expression) throws EvalError{
		interpreter.eval(expression);
	}
	
	public void eval(File script) throws IOException, EvalError{
		interpreter.source(script.getAbsolutePath());
	}
	
	public Interpreter bootstrapInterpreter(Interpreter i){
		File bootstrap = new File("scripts/bootstrap.bsh");
		
		try{
			//import custom commands
			i.eval("addClassPath(\""+new File(".").getAbsolutePath()+"\")");
			i.eval("importCommands(\"/scripts\")");
			
			BufferedReader br = new BufferedReader(new FileReader(bootstrap));
			String line;
			
			while((line = br.readLine()) != null){
				i.eval(line);
			}
		
			br.close();
		}catch(EvalError ee){
			log.error("Error bootstraping ActiveCloud script system");
			log.error(ee.getMessage());
		}catch(FileNotFoundException fnfe){
			log.error("Bootstrap script not found");
			log.error(fnfe.getMessage());
		}catch(IOException ioe){
			log.error("Error reading from boostrap script file");
			log.error(ioe.getMessage());
		}
		return i;
	}
	
	public static QueryProcessorService get(){
		if(singleton == null){
			log.debug("Instantiating "+QueryProcessorService.class.getName());
			singleton = new QueryProcessorService();
		}
		return singleton;
	}
}
