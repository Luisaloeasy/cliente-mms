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
 * Clase encargada de extraer los Acquirers de las bases de datos Merchant Server
 *
 * @author Luis Alejandro Aloisi Millan
 */
public class EnvioAcquirers extends Envio {

    private Logger logger = Logger.getLogger(EnvioAcquirers.class.getName());

    public long startExecutionTime;
    public long endExecutionTime;

    private DatabaseService dbService;
    private OpGetBD opGetBD;
    
    public EnvioAcquirers() {
        super();
    }

    public void run(Map parameters) throws Exception {

        startExecutionTime = System.nanoTime();

        if (logger.isInfoEnabled()) {

            logger.info("MMS v2 -- Inicio Tarea EnvioAcquirers");
        }

        try {

            super.run(parameters);

            dbService = new DatabaseServiceImpl(true);
            opGetBD = new OpGetBD(dbService);

            if (logger.isDebugEnabled()) {
                logger.debug("MMS v2 -- Parametros cargados: Metodo de envio = " + metodoEnvio + "," + " Recuperar por periodo: " + recuperarPorPeriodo + " Periodo (milisegundos) = " + periodo);
            }

            envioAcquirers();

        } catch (Exception e) {
            logger.error("MMS v2 -- Error no identificado '" + e.getMessage() + "'", e);
        } finally {
            if (logger.isInfoEnabled()) {
                logger.info("MMS v2 -- Termino Tarea Acquirers");
            }
        }
    }

    private boolean envioAcquirers() throws Exception {

        logger.info("MMS v2 -- Inicio Tarea EnvioAcquirers");

        QueryConfigResult acquirers;

        try {
            acquirers = opGetBD.getAcquirersMQ();
            if (acquirers == null) {
                logger.error("MMS v2 -- No hay datos disponibles en getAcquirersMQ()");
                return false;
            }
        } catch (SQLMSBDException e) {
            logger.error("MMS v2 -- Error realizando conexión con la BD", e);
            return false;
        } catch (Exception e1) {
            logger.error("MMS v2 -- Error inesperado Acquirers con la consulta a la BD", e1);
            return false;
        }

        try {
            LinkedHashMap confCliente;

            boolean dataEqualToPreviousExtraction = tareaUtil.compareJsonToResulSet("acquirers");

            // Se tiene el nombre, email, id y puerto del cliente MMS
            confCliente = FachadaConf.cargarConfEntidadDeGeneral("ConfigFiles/GeneralConfig.xml", "ConfCliente");

            int idCliente = Integer.parseInt(confCliente.get("idCliente").toString());

            String nombreCliente = confCliente.get("nombreCliente").toString();

            int puerto = Integer.parseInt(confCliente.get("puerto").toString());

            String nombreProcesamientoServerMMS = "Acquirers";

            String nombreTabla = "Acquirers";

            int cantidadDeRegistros = acquirers.getSize();

            String checkSum = "0";

            int cantidadMensajesEnviar = 1;

            boolean excludingColumns = false;

            endExecutionTime = System.nanoTime();

            double runtime = ((endExecutionTime - startExecutionTime) / 1e6);

            if (dataEqualToPreviousExtraction) {
                cantidadDeRegistros = 0;
                acquirers.clear();
            }

            TokenDatosMQ datosAEnviar = new TokenDatosMQ(nombreCliente, idCliente, puerto, nombreProcesamientoServerMMS, cantidadDeRegistros, checkSum, formatoParaArchivo.format(new Date()), nombreTabla, nombreProcesamientoServerMMS, cantidadMensajesEnviar, runtime, acquirers.getQueryExecutionTime(), acquirers.getJsonExecutionTime(), 0, excludingColumns, acquirers.getResultadoFinal());

            if (logger.isDebugEnabled()) {
                logger.debug("MMS v2 -- La cantidad de Acquirers a enviar son: " + acquirers.getSize());
            }

            ComunicacionRabbitMQ.enviar(metodoEnvio, datosAEnviar);

            tareaUtil.renameJsons("acquirers", dataEqualToPreviousExtraction);

            if (logger.isInfoEnabled()) {
                logger.info("MMS v2 -- Se enviaron correctamente los Acquirers, cantidad: " + acquirers.getSize() + " a las " + formatoParaArchivo.format(new Date()) + ". Runtime: " + runtime + " ms");
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