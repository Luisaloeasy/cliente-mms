package com.megasoft.merchant.mms.tarea;

import java.util.Hashtable;
import java.util.Map;

import org.apache.log4j.Logger;

import com.megasoft.merchant.mms.tarea.util.TareaUtil;

import b2b.util.scheduler.SchedulerBusyInterface;

/**
 * 
 * @author Christian De Sousa
 *
 */
public abstract class Tarea implements SchedulerBusyInterface {

	private Logger logger = Logger.getLogger(Tarea.class.getName());
	
	private Map initParameters = new Hashtable();
	protected TareaUtil tareaUtil;
	
	public void run(Map parameters) 
						throws Exception, InterruptedException {
		
		if (logger.isInfoEnabled()){
			logger.info("Seteando Parametro Iniciales");
		}
		initParameters = parameters;
		
		if (logger.isInfoEnabled()){
			logger.info("Inicializando TareaUtil");
		}
		tareaUtil = new TareaUtil();
	}
    
    public String getInitParameter(String paramName) {
    	Object resultado = initParameters.get(paramName);
    	if (resultado != null){
    		return (String) resultado; 
    	}else{
    		return null;
    	}
    }
}
