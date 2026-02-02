package com.megasoft.merchant.mms.persistence.queries;

import org.apache.log4j.Logger;

import java.util.Vector;

/**
 * 
 * @author Luis Aloisi
 *
 */
public class Update {

	static private Logger logger = Logger.getLogger(Update.class.getName());

	public static String setStatusCierre0Terminal(Vector terminalId) {
		
		String conjuntoTerminalId = "(";
		for (int i = 0; i < terminalId.size(); i++) {
			if(i != terminalId.size() - 1){
				conjuntoTerminalId = conjuntoTerminalId+" "+((String)terminalId.get(i))+",";	
			}else {
				conjuntoTerminalId = conjuntoTerminalId+" "+((String)terminalId.get(i));
				conjuntoTerminalId = conjuntoTerminalId+") ";
			}
		}
		
		String query = 
				"UPDATE terminals SET "+
						"statusCierre = 0 " +
				"WHERE terminalId in " +
						conjuntoTerminalId;
				
		if (logger.isDebugEnabled()){
			logger.debug("Query generado "+query);
		}
		return query;
	}

}
