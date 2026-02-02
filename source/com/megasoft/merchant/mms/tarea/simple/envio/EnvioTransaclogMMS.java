package com.megasoft.merchant.mms.tarea.simple.envio;

import com.megasoft.merchant.mms.persistence.service.OpGetBD;
import com.megasoft.merchant.mms.persistence.exception.SQLMSBDException;
import com.megasoft.merchant.mms.persistence.response.QueryMMSResult;
import com.megasoft.merchant.mms.comunicacion.ComunicacionRabbitMQ;
import com.megasoft.merchant.mms.comunicacion.TokenDatosMQ;
import com.megasoft.merchant.mms.comunicacion.exception.MSComunicacionException;
import com.megasoft.merchant.mms.configuracion.FachadaConf;
import com.megasoft.merchant.mms.configuracion.exception.EntityConfigurationLoadException;
import com.megasoft.merchant.mms.persistence.DatabaseService;
import com.megasoft.merchant.mms.persistence.DatabaseServiceImpl;
import com.megasoft.merchant.mms.tarea.exception.ConfigurationErrorTareaException;
import com.megasoft.merchant.mms.tarea.exception.InvalidDataTareaException;
import org.apache.log4j.Logger;

import java.sql.Timestamp;
import java.util.*;

/**
 * Clase encargada de extraer Transaclog de las bases de datos
 * 
 * @author Luis Alejandro Aloisi Millan & Michael Amariscua
 *
 */

public class EnvioTransaclogMMS extends Envio {

	private Logger logger = Logger.getLogger(EnvioTransaclogMMS.class.getName());

	public long startExecutionTime;
	public long endExecutionTime;
	public long startCalculateTimeStampExecutionTime;
	public long endCalculateTimeStampExecutionTime;
	public double timestampRuntime;
	
	private DatabaseService dbService;
	private OpGetBD opGetBD;
	
	public EnvioTransaclogMMS() {
		super();
	}

	public void run(Map parameters) throws Exception {

		startCalculateTimeStampExecutionTime = System.nanoTime();

		if (logger.isInfoEnabled()) {

			logger.info("MMS v2 -- Inicio Tarea EnvioTransaclogMMS");
		}

		try {

			super.run(parameters);

			dbService = new DatabaseServiceImpl(false);
			opGetBD = new OpGetBD(dbService);

			if (logger.isDebugEnabled()) {
				logger.debug("MMS v2 -- Parametros cargados: Metodo de envio = " + metodoEnvio + "," + " Recuperar por periodo: " + recuperarPorPeriodo + " Periodo (milisegundos) = " + periodo);
			}

			intervalodetiempo();

		} catch (Exception e) {
			logger.error("MMS v2 -- Error no identificado '" + e.getMessage() + "'", e);
		} finally {
			if (logger.isInfoEnabled()) {
				logger.info("MMS v2 -- Termino Tarea EnvioTransaclogMMS");
			}
		}
	}

	private void intervalodetiempo() throws Exception {
		try {
			int contadorEventos = 0;
			boolean debeContinuar = true;
			boolean exito = true;
			int tiempoEsperaSegundos = 300;
			int cantidadArchivosPorLote = 30;

			Timestamp fechaInicio = tareaUtil.obtenerUltimaVezRealizada(formato, "MMS_v2_LastExecutionDateTimeTransaclog.xml");

			Calendar calendar = new GregorianCalendar();
			calendar.setTimeInMillis(fechaInicio.getTime());
			calendar.add(Calendar.SECOND, 1);

			fechaInicio = new Timestamp(calendar.getTimeInMillis());

			Timestamp fechaFin = tareaUtil.obtenerFechaFin(formato, recuperarPorPeriodo, fechaInicio, periodo);

			Calendar calendar2 = new GregorianCalendar();
			calendar2.setTimeInMillis(fechaFin.getTime());
			calendar2.add(Calendar.SECOND, -1);

			fechaFin = new Timestamp(calendar2.getTimeInMillis());

			try {
				String configFilePath = "ConfigFiles/GeneralConfig.xml";
				String rabbitConfigKey = "RabbitEnvio";
				String loteEventosKey = "LoteEventos";
				String tiempoEsperaLoteSegKey = "TiempoEsperaLoteSeg";

				LinkedHashMap configuracionRabbitMQ = FachadaConf.cargarConfEntidadDeGeneral(configFilePath, rabbitConfigKey);
				cantidadArchivosPorLote = Integer.parseInt(configuracionRabbitMQ.get(loteEventosKey).toString());
				tiempoEsperaSegundos = Integer.parseInt(configuracionRabbitMQ.get(tiempoEsperaLoteSegKey).toString());
			} catch (NumberFormatException e) {
				logger.error("MMS v2 -- Ocurrio un error formateando el TiempoEsperaLoteSeg");
			} catch (EntityConfigurationLoadException e) {
				logger.error("MMS v2 -- Error Cargando la configuracion de RabbitMQ para TiempoEsperaLoteSeg", e);
			}

			logger.info("MMS v2 -- Se calculó el Intervalo de Tiempo, los valores de extraccion son, fecha inicio: " + fechaInicio + " fecha fin: " + fechaFin + ". El tiempo de espera de envio por lotes es: " + tiempoEsperaSegundos + " segundos.");
			endCalculateTimeStampExecutionTime = System.nanoTime();
			timestampRuntime = ((endCalculateTimeStampExecutionTime - startCalculateTimeStampExecutionTime) / 1e6);
			do {
				Calendar calendar3 = new GregorianCalendar();
				calendar3.getTime();
				calendar3.add(Calendar.MINUTE, -2);
				Timestamp fechaActual = new Timestamp(calendar3.getTimeInMillis());
				logger.info("MMS v2 -- Inicia do-while con los valores de, fecha actual: " + fechaActual + ", fecha inicio: " + fechaInicio + ", fecha fin: " + fechaFin + ", contador de eventos previamente enviados: " + contadorEventos + ". ¿El proceso debe continuar?: " + debeContinuar + ". ¿La extraccion anterior fue ejecutada exitosamente?: " + exito);
				if (fechaFin.before(fechaActual)) {
					logger.info("MMS v2 -- La fechaFin esta antes de la fecha actual, se procede a ejecutar la tarea envioTransaclog");
					exito = envioTransaclog(fechaInicio, fechaFin);
					if (exito) {
						contadorEventos++;
					} else {
						logger.info("MMS v2 -- Ocurrio un error en la clase EnvioTransaclog, y no se actualiza el contadorEventos");
					}

					if (contadorEventos == cantidadArchivosPorLote) {
						try {
							logger.info("MMS v2 -- Se ha alcanzado el maximo de envio de eventos por lotes, " + cantidadArchivosPorLote + ". Fecha actual " + fechaActual + " fecha inicio: " + fechaInicio + " fecha fin: " + fechaFin + " contador de eventos previamente enviados: " + contadorEventos + ". ¿El proceso debe continuar?: " + debeContinuar + ". ¿La extraccion anterior fue ejecutada exitosamente?: " + exito);
							logger.info("MMS v2 -- Se pausara el envio de eventos por " + tiempoEsperaSegundos + " segundos. Fecha actual: " + new Date());
							Thread.sleep((long) tiempoEsperaSegundos * 1000L);
						} catch (InterruptedException e) {
							logger.error("MMS v2 -- Ocurrio un error durmiendo el hilo TransaclogMMS", e);
						}
						contadorEventos = 0;
						logger.info("MMS v2 -- Envío de eventos reanudado. Fecha de reanudación: " + new Date());
					}
					startCalculateTimeStampExecutionTime = System.nanoTime();
					if (recuperarPorPeriodo && exito) {
						fechaInicio = tareaUtil.obtenerUltimaVezRealizada(formato, "MMS_v2_LastExecutionDateTimeTransaclog.xml");

						calendar.setTimeInMillis(fechaInicio.getTime());
						calendar.add(Calendar.SECOND, 1);
						fechaInicio = new Timestamp(calendar.getTimeInMillis());

						fechaFin = tareaUtil.obtenerFechaFin(formato, recuperarPorPeriodo, fechaInicio, periodo);

						calendar2.setTimeInMillis(fechaFin.getTime());
						calendar2.add(Calendar.SECOND, -1);
						fechaFin = new Timestamp(calendar2.getTimeInMillis());
						logger.info("MMS v2 -- Se actualizan las fechas para la siguiente extraccion");
					}
				} else {
					debeContinuar = false;
					logger.info("MMS v2 -- La fechaFin esta despues de la fechaActual. Se pausa la tarea TransaclogMMS.");
				}
				endCalculateTimeStampExecutionTime = System.nanoTime();
				timestampRuntime = ((endCalculateTimeStampExecutionTime - startCalculateTimeStampExecutionTime) / 1e6);
			} while (debeContinuar && exito);
			logger.debug("MMS v2 -- Sale del do-while con los valores de fecha inicio: " + fechaInicio + " fecha fin: " + fechaFin + " contador de eventos previamente enviados: " + contadorEventos + ". ¿El proceso debe continuar?: " + debeContinuar + ". ¿La extraccion anterior fue ejecutada exitosamente?: " + exito);
		} catch (ConfigurationErrorTareaException e) {
			logger.error("MMS v2 -- No se pudo cargar la informacion de Control de la Tarea (ConfigFiles/MMS_v2_LastExecutionDateTimeTransaclog.xml", e);
		}
		logger.info("MMS v2 -- Saliendo del ciclo. Se pausa el calculo de los intervalos.");
	}

	private boolean envioTransaclog(Timestamp fechaInicio, Timestamp fechaFin) throws Exception {

		startExecutionTime = System.nanoTime();

		if (logger.isDebugEnabled()) {
			logger.debug("MMS v2 -- Extraccion de Transaclog desde" + fechaInicio + " hasta " + fechaFin);
		}

		logger.info("MMS v2 -- Inicia proceso de extraccion TransaclogMMS");


		QueryMMSResult transaclog;

		try {
			transaclog = opGetBD.getTransaclog(fechaInicio, fechaFin);
			if (transaclog == null) {
				logger.error("MMS v2 -- No hay datos disponibles en getTransaclog(), ocurrio un problema null");
				return false;
			}
		} catch (SQLMSBDException e) {
			logger.error("MMS v2 -- Error realizando conexión con la BD", e);
			return false;
		} catch (Exception e1) {
			logger.error("MMS v2 -- Error inesperado Transaclog con la consulta a la BD", e1);
			return false;
		}

		try {
			LinkedHashMap confCliente;

			// Se tiene el nombre, email, id y puerto del cliente MMS
			confCliente = FachadaConf.cargarConfEntidadDeGeneral("ConfigFiles/GeneralConfig.xml", "ConfCliente");

			int idCliente = Integer.parseInt(confCliente.get("idCliente").toString());

			String nombreCliente = confCliente.get("nombreCliente").toString();

			int puerto = Integer.parseInt(confCliente.get("puerto").toString());

			String nombreProcesamientoServerMMS = "MMS";

			String nombreTuplaProceso = "Transaclog";

			String nombreTabla = "Transaclog";

			int cantidadDeRegistros = transaclog.getSize();

			String checkSum = "0";

			int cantidadMensajesEnviar = 1;

			boolean excludingColumns = false;

			endExecutionTime = System.nanoTime();

			double runtime = ((endExecutionTime - startExecutionTime) / 1e6);

			TokenDatosMQ datosAEnviar = new TokenDatosMQ(nombreCliente, idCliente, puerto, nombreProcesamientoServerMMS, cantidadDeRegistros, checkSum, formatoParaArchivo.format(new Date(fechaInicio.getTime())), formatoParaArchivo.format(new Date(fechaFin.getTime())), nombreTabla, nombreTuplaProceso, cantidadMensajesEnviar, runtime, transaclog.getQueryExecutionTime(), transaclog.getJsonExecutionTime(), timestampRuntime, excludingColumns, transaclog.getResultadoFinal());


			logger.info("MMS v2 -- La cantidad de transacciones de Transaclog a enviar son: " + transaclog.getSize() + ". Inicio del envio en la fecha: " + new Date());


			ComunicacionRabbitMQ.enviar(metodoEnvio, datosAEnviar);


			logger.info("MMS v2 -- Se enviaron correctamente las transacciones de Transaclog, cantidad: " + transaclog.getSize() + " desde " + fechaInicio + " hasta " + fechaFin + ". Runtime: " + runtime + " ms. Fin del envio de evento en la fecha: " + new Date());


			return tareaUtil.guardarUltimaVezRealizada(formato, "MMS_v2_LastExecutionDateTimeTransaclog.xml", "MMS_v2_LastExecutionDateTimeTransaclog", fechaFin);

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
