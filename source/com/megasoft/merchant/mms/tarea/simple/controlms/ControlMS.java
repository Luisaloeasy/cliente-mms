package com.megasoft.merchant.mms.tarea.simple.controlms;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.megasoft.merchant.mms.configuracion.FachadaConf;
import com.megasoft.merchant.mms.configuracion.exception.EntityConfigurationLoadException;
import com.megasoft.merchant.mms.tarea.simple.Simple;

/**
 * 
 * @author Christian De Sousa
 *
 */
public abstract class ControlMS extends Simple{

	private Logger logger = Logger.getLogger(ControlMS.class.getName());
	
	protected String metodoEnvio;
	protected LinkedHashMap confCliente;
	protected int idCliente;
	protected static SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public ControlMS(){
		super();
	}
	
	public void run(Map parameters) throws InterruptedException, Exception {		
		super.run(parameters);
		metodoEnvio = getInitParameter("metodoenvio");
		
		idCliente = -1;
		try{
			while (idCliente == -1){
				LinkedHashMap confCliente = FachadaConf.
									cargarConfEntidadDeGeneral("ConfigFiles/GeneralConfig.xml",
															   "ConfCliente");
				
				if (confCliente.get("idCliente") == null || 
						new Integer(confCliente.get("idCliente").toString()).intValue() == -1){
					
					logger.error("No se puede realizar la operacion ya que no se a registrado la Instancia del Cliente en el Servidor se esperar 5seg para volver a intentar");
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						logger.error("Error al detener el Thread por la espera de registro", e);
					}
				}
				idCliente = new Integer(confCliente.get("idCliente").toString()).intValue();
			}
		} catch (EntityConfigurationLoadException e){
			logger.error("No se pudo cargar la Configuracion del Cliente del archivo de Configuracion General", e);
			return;
		}
	}
}