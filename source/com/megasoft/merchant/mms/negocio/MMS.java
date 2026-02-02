package com.megasoft.merchant.mms.negocio;

import java.util.Date;
import java.io.Serializable;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * 
 * @author Christian De Sousa
 *
 */
public abstract class MMS  implements Serializable{
	
	protected Timestamp normalizarTiempo(Timestamp tiempo){
		SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		if (tiempo == null){
			return null;
		}
		try {
			return new Timestamp(formato.parse(formato.format(new Date(tiempo.getTime()))).getTime());
		} catch (ParseException e) {
			return null;
		} 
	}
	
	protected int diferenciaEnMinutos(Timestamp initialTime, Timestamp endTime){
		long initialMilis = initialTime.getTime();
		long endMilis = endTime.getTime();
		long minutes = ( endMilis - initialMilis ) / 60000;
		Long longTemp = new Long(minutes);
		return new Integer(longTemp.toString()).intValue();
	}

	
}
