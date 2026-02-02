
/**
 * Copyright Mega Soft Computaci�n C.A.
 */

package com.megasoft.merchant.mms.tarea.simple.envio;


import com.megasoft.merchant.mms.persistence.service.OpGetBD;
import com.megasoft.merchant.mms.persistence.exception.SQLMSBDException;
import com.megasoft.merchant.mms.persistence.response.QueryConfigResult;
import com.megasoft.merchant.mms.comunicacion.ComunicacionRabbitMQ;
import com.megasoft.merchant.mms.comunicacion.TokenDatosMQ;
import com.megasoft.merchant.mms.comunicacion.exception.MSComunicacionException;
import com.megasoft.merchant.mms.configuracion.FachadaConf;
import com.megasoft.merchant.mms.configuracion.exception.EntityConfigurationLoadException;
import com.megasoft.merchant.mms.persistence.DatabaseService;
import com.megasoft.merchant.mms.persistence.DatabaseServiceImpl;
import com.megasoft.merchant.mms.tarea.exception.InvalidDataTareaException;
import org.apache.log4j.Logger;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Clase encargada de extraer los VTerminals de las bases de datos Merchant
 * Server
 * 
 * @author Luis Alejandro Aloisi Millan
 *
 */

public class EnvioVterminals extends Envio {

	private final Logger logger = Logger.getLogger(EnvioVterminals.class.getName());

	public long startExecutionTime;
	public long endExecutionTime;
	
	private DatabaseService dbService;
	private OpGetBD opGetBD;
	
	public EnvioVterminals() {
		super();
	}

	public void run(Map parameters) throws Exception {

		startExecutionTime = System.nanoTime();

		if (logger.isInfoEnabled()) {

			logger.info("MMS v2 -- Inicio Tarea EnvioVterminals");
		}

		try {

			super.run(parameters);

			dbService = new DatabaseServiceImpl(true);
			opGetBD = new OpGetBD(dbService);

			if (logger.isDebugEnabled()) {
				logger.debug("MMS v2 -- Parametros cargados: Metodo de envio = " + metodoEnvio + "," + " Recuperar por periodo: " + recuperarPorPeriodo + " Periodo (milisegundos) = " + periodo);
			}

			envioVTerminals();

		} catch (Exception e) {
			logger.error("MMS v2 -- Error no identificado '" + e.getMessage() + "'", e);
		} finally {
			if (logger.isInfoEnabled()) {
				logger.info("MMS v2 -- Termino Tarea EnvioVterminals");
			}
		}
	}

	private boolean envioVTerminals() throws Exception {

		logger.info("MMS v2 -- Inicio Tarea EnvioVterminals");


		QueryConfigResult vTerminals;

		try {
			vTerminals = opGetBD.getVTerminalsMQ();
			if (vTerminals == null) {
				logger.error("MMS v2 -- No hay datos disponibles en getVTerminalsMQ()");
				return false;
			}
		} catch (SQLMSBDException e) {
			logger.error("MMS v2 -- Error realizando conexión con la BD", e);
			return false;
		} catch (Exception e1) {
			logger.error("MMS v2 -- Error inesperado VTerminals con la consulta a la BD", e1);
			return false;
		}

		try {
			LinkedHashMap confCliente;

			boolean dataEqualToPreviousExtraction = tareaUtil.compareJsonToResulSet("vTerminals");

			// Se tiene el nombre, email, id y puerto del cliente MMS
			confCliente = FachadaConf.cargarConfEntidadDeGeneral("ConfigFiles/GeneralConfig.xml", "ConfCliente");

			int idCliente = Integer.parseInt(confCliente.get("idCliente").toString());

			String nombreCliente = confCliente.get("nombreCliente").toString();

			int puerto = Integer.parseInt(confCliente.get("puerto").toString());

			String nombreProcesamientoServerMMS = "VTerminals";

			String nombreTabla = "VTerminals";

			int cantidadDeRegistros = vTerminals.getSize();

			String checkSum = "0";

			int cantidadMensajesEnviar = 1;

			boolean excludingColumns = true;

			endExecutionTime = System.nanoTime();

			double runtime = ((endExecutionTime - startExecutionTime) / 1e6);

			if (dataEqualToPreviousExtraction) {
				cantidadDeRegistros = 0;
				vTerminals.clear();
			}

			TokenDatosMQ datosAEnviar = new TokenDatosMQ(nombreCliente, idCliente, puerto, nombreProcesamientoServerMMS, cantidadDeRegistros, checkSum, formatoParaArchivo.format(new Date()), nombreTabla, nombreProcesamientoServerMMS, cantidadMensajesEnviar, runtime, vTerminals.getQueryExecutionTime(), vTerminals.getJsonExecutionTime(), 0, excludingColumns, vTerminals.getResultadoFinal());

			if (logger.isDebugEnabled()) {
				logger.debug("MMS v2 -- La cantidad de vTerminals a enviar son: " + vTerminals.getSize());
			}

			ComunicacionRabbitMQ.enviar(metodoEnvio, datosAEnviar);

			tareaUtil.renameJsons("vTerminals", dataEqualToPreviousExtraction);

			if (logger.isInfoEnabled()) {
				logger.info("MMS v2 -- Se enviaron correctamente los vTerminals, cantidad: " + vTerminals.getSize() + " a las " + formatoParaArchivo.format(new Date()) + ". Runtime: " + runtime + " ms");
			}

		} catch (SQLMSBDException e) {
			logger.error("MMS v2 -- No se puede acceder a la BD del Sistema MS", e);
		} catch (MSComunicacionException e) {
			logger.error("MMS v2 -- No se puede enviar la informacion del MS al Servidor", e);
		} catch (InvalidDataTareaException e) {
			logger.error("MMS v2 -- Los discos a analizar ingresados no siguen el patron esperado", e);
		} catch (EntityConfigurationLoadException e) {
			logger.error("MMS v2 -- No se puede la configuracion del Cliente", e);
		}
		return false;
	}
}