package ubisoa.activecloud.services;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

import bsh.EvalError;
import bsh.Interpreter;

public class QueryProcessorService {
	private static QueryProcessorService singleton;
	private static Logger log = Logger.getLogger(QueryProcessorService.class);
	private Interpreter i;
	
	public QueryProcessorService(){
		i = new Interpreter();
	}
	
	public void eval(String expression) throws EvalError{
		i.eval(expression);
	}
	
	public void eval(File script) throws IOException, EvalError{
		i.source(script.getAbsolutePath());
	}
	
	public static QueryProcessorService get(){
		if(singleton == null){
			log.debug("Instantiating "+QueryProcessorService.class.getName());
			singleton = new QueryProcessorService();
		}
		return singleton;
	}
}
