package com.megasoft.merchant.mms.tarea.simple.controlms;

import com.megasoft.merchant.mms.persistence.service.OpGetBDMS;
import com.megasoft.merchant.mms.persistence.service.OpUpdateBDMS;
import com.megasoft.merchant.mms.persistence.exception.SQLMSBDException;
import com.megasoft.merchant.mms.negocio.Terminal;
import com.megasoft.merchant.mms.persistence.DatabaseService;
import com.megasoft.merchant.mms.persistence.DatabaseServiceImpl;
import org.apache.log4j.Logger;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.Vector;

/**
 * 
 * @author Christian De Sousa & Luis Aloisi
 *
 */
public class ControlMSTerminalCierreInsistente extends ControlMS{

	private Logger logger = Logger.getLogger(ControlMSTerminalCierreInsistente.class.getName());

	private DatabaseService dbServiceMerchant;
	private DatabaseService dbServiceHistory;

	private OpGetBDMS opGetBD;
	private OpUpdateBDMS opUpdateBD;


	public void run(Map parameters) throws InterruptedException, Exception{
		if (logger.isInfoEnabled()){
			logger.info("Inicio Tarea");
		}
		try {
			super.run(parameters);
			dbServiceMerchant = new DatabaseServiceImpl(true);
			dbServiceHistory = new DatabaseServiceImpl(false);

			opGetBD = new OpGetBDMS(dbServiceMerchant, dbServiceHistory);
			opUpdateBD = new OpUpdateBDMS(dbServiceMerchant);

			if (logger.isDebugEnabled()){
				logger.debug("Parametros cargados: " +
									" Recuperar por periodo: "+recuperarPorPeriodo+
									" Periodo (milisegundos) : "+periodo);
			}
			
			obtenerVTerminalCierreInsistentes();
		
		} catch (Exception e){
			logger.error("Error no identificado '"+e.getMessage()+"'",e);
		} finally{
			if (logger.isInfoEnabled()){
				logger.info("Termino Tarea");
			}
		}
	}

	private void obtenerVTerminalCierreInsistentes() {

			try {

				Timestamp horaActual = tareaUtil.horaActual(formato);
				Calendar calendar = new GregorianCalendar();
		        calendar.setTimeInMillis(horaActual.getTime());
				calendar.set(Calendar.SECOND, 0);
				Timestamp fechaFin = new Timestamp(calendar.getTimeInMillis());
				
				calendar.add(Calendar.MINUTE, -30);
				Timestamp fechaInicio = new Timestamp(calendar.getTimeInMillis());
				
				int numInsistencias = new Integer(getInitParameter("numinsistencias")).intValue();

				Vector terminalConCierreInsistente = opGetBD.getTerminalConCierreInsistentes(fechaInicio, fechaFin, numInsistencias);

				logger.info("MMS v2 -- Numero de Terminals que poseen Cierres Insistentes "+terminalConCierreInsistente.size());


				if (terminalConCierreInsistente.size() > 0){
					
					Vector terminalId = new Vector();
					for (int i = 0; i < terminalConCierreInsistente.size(); i++) {
						terminalId.add(((Terminal)terminalConCierreInsistente.get(i)).getId()+"");
					}

					int tuplasCambiadas = opUpdateBD.updateToStatusCierre0TerminalCierreInsistentes(terminalId);

					if (tuplasCambiadas == terminalConCierreInsistente.size()) {
						logger.info("MMS v2 -- Se cambio el estado del campo 'status cierre' a 0 exitosamente a todos los terminales");
					}else{
						logger.info("MMS v2 -- No se actualizaron todos los terminales a 'status cierre' = 0, numero de Terminals actualizados "+tuplasCambiadas);
					}

				}
				
			} catch (SQLMSBDException e) {
				logger.error("Error en el acceso a la BD",e);
			} catch (Exception e){
				logger.error("Error no identificado '"+e.getCause()+"'",e);
			}
	}
}
