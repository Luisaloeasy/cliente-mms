package com.megasoft.merchant.mms.tarea.simple.envio;

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
public abstract class Envio extends Simple{

	private Logger logger = Logger.getLogger(Envio.class.getName());
	
	protected String metodoEnvio;
	protected int idCliente;
	protected LinkedHashMap confCliente;
	
	public Envio(){
		super();
	}
	
	public void run(Map parameters) throws InterruptedException, Exception {		
		super.run(parameters);
		metodoEnvio = getInitParameter("metodoenvio");
		
		idCliente = -1;
		try{
			while (idCliente == -1){
				LinkedHashMap confCliente = FachadaConf.
													cargarConfEntidadDeGeneral("ConfigFiles/GeneralConfig.xml","ConfCliente");
				
				if (confCliente.get("idCliente") == null ||
                        Integer.parseInt(confCliente.get("idCliente").toString()) == -1
                ){
					logger.error("No se puede realizar la operacion ya que " +
										"no se a registrado la Instancia del Cliente en el Servidor " +
										"se realizara una espera de aprox 10 min para volver a realizar la Tarea");
					
					
				if (confCliente.get("ServidorEnvio") == null)
				{
					logger.error("No se puede realizar la operacion ya que " +
							"no se obtuvo el Servidor de Envio");
				}
				
				if (confCliente.get("De") == null)
				{
					logger.error("No se puede realizar la operacion ya que " +
							"no se obtuvo el De 'de envio'");
				}
					
					try {
						Thread.sleep(60000);
					} catch (InterruptedException e) {
						logger.error("Error al detener el Thread por la espera de registro", e);
					}
				}
                idCliente = Integer.parseInt(confCliente.get("idCliente").toString());

            }
		} catch (EntityConfigurationLoadException e){
			logger.error("No se pudo cargar la Configuracion del Cliente del archivo de Configuracion General", e);
			return;
		}
	}
}
