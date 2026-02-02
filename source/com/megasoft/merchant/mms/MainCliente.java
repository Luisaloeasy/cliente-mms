package com.megasoft.merchant.mms;

import b2b.util.scheduler.MSCScheduler;
import com.megasoft.merchant.mms.configuracion.FachadaConf;
import com.megasoft.merchant.mms.persistence.DataSourceConfig;
import com.megasoft.merchant.mms.transporte.MSServidorSocketMultiHilo;
import com.megasoft.merchant.mms.transporte.RabbitConexion;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import java.net.Socket;
import java.util.LinkedHashMap;

/**
 * Clase de Inicio de aplicacion del componente Cliente MMS
 * @author Christian De Sousa
 *
 */
public class MainCliente {
	
	private static int puertoAp;
	private static Logger log = Logger.getLogger(MainCliente.class.getName());
	
	public static void main(String[] args){
		MSCScheduler scheduler = null;
		try {

			DOMConfigurator.configure("ConfigFiles/log4j.xml");

			LinkedHashMap confCliente = FachadaConf.
					cargarConfEntidadDeGeneral("ConfigFiles/GeneralConfig.xml","ConfCliente");
			
			puertoAp = new Integer(confCliente.get("puerto").toString()).intValue();

		    if (!isInstanceActive(puertoAp)){

				log.info("Iniciando MainCliente");
				RabbitConexion rabbit = new RabbitConexion();
				rabbit.initialize();

				HikariDataSource ds = DataSourceConfig.getDataSource();
				log.info("Se inició el DataSource exitosamente en MainCliente");

				if (scheduler == null){
		    		scheduler = new MSCScheduler("ConfigFiles/mscscheduler.properties");
		    		scheduler.start();
		    	}

		    }else {
				System.err.println("La aplicacion ya se encuentra activa o alguna \naplicacion se encuentra usando el puerto "+puertoAp);
				System.exit(0);
		    }
		    
		} catch (Exception e) {
			System.err.println("Error iniciando el sistema "+e.getMessage());
			System.exit(1);
		}
	}
    
	/**
	 * Procedimiento que activa un servicio (en el puerto especifico), si este servicio no se activa el servidor 
	 * es que ya existe un servicio ocupando este puerto en el sistema, por lo que asume que una instancia de este componente 
	 * ya se encuentra activa. 
	 * @param puerto
	 * @return Si el servicio se pudo crear y esta activo.
	 */
	private synchronized static boolean isInstanceActive(int puerto) {
		MSServidorSocketMultiHilo server = new ServidorEsperaCliente(puerto);
		server.establishService();
		server.start();
		return !server.isActive;  
	}
	
	/**
	 * Termina la aplicacion
	 *
	 */
	public void ending() {
    }

}

/**
 * Servicio para identificar la actividad del componente
 * @author Christian De Sousa
 *
 */
class ServidorEsperaCliente extends MSServidorSocketMultiHilo{

	public ServidorEsperaCliente(int puerto) {
		super(puerto);
	}

	/**
	 * Este procedimiento es nulo ya que no se realiza ninguna actividad con la conexion
	 * solamente se acepta la conexion como procedimiento normal de un servidor, pero este servidor 
	 * es DUMMY ya que no debe realizar nada con las conexiones realizadas al mismo
	 */
	protected void activarServidor(Socket socket, int puerto) {
	}
}
