package com.megasoft.merchant.mms.tarea.simple;

import java.util.*;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;

import com.megasoft.merchant.mms.tarea.Tarea;

/**
 * 
 * @author Christian De Sousa
 *
 */
public abstract class Simple extends Tarea{
	
	private Logger logger = Logger.getLogger(Simple.class.getName());

	protected int periodo;
	protected boolean recuperarPorPeriodo;
	
	/* Se creo este formato ya que se usa para crear los 
	 * nombres de los archivos y en windows no se permite ':' 
	 * */
	protected SimpleDateFormat formatoParaArchivo = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
	protected SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public Simple(){
		super();
	}
	
	public void run(Map parameters) 
					throws InterruptedException, Exception {
		
		super.run(parameters);
		
		if (logger.isInfoEnabled()){
			logger.info("Cargando Informacion y Datos de entrada Iniciales");
		}
			
		if (getInitParameter("recuperarporperiodo") == null || 
				getInitParameter("recuperarporperiodo").equalsIgnoreCase("false")){
			recuperarPorPeriodo = false;
		}
		else{ 
			recuperarPorPeriodo = true;
		}
		if ( getInitParameter("period") != null ){
				periodo = tareaUtil.parseoTiempo(getInitParameter("period"));
		}else {
			periodo = -1;
		}
		
		if (logger.isDebugEnabled()){
			logger.debug("Parametros obtenido : " +
							" Recuperar por periodo: " +recuperarPorPeriodo+
							" Periodo (milisegundos) = "+periodo);
		}
		
	}
}
