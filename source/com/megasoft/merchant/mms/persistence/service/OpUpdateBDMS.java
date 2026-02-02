package com.megasoft.merchant.mms.persistence.service;

import com.megasoft.merchant.mms.persistence.exception.SQLMSBDException;
import com.megasoft.merchant.mms.persistence.DatabaseService;
import com.megasoft.merchant.mms.persistence.queries.Update;
import org.apache.log4j.Logger;

import java.util.Vector;

/**
 * @author Luis Aloisi
 */
public class OpUpdateBDMS {

    static private final Logger logger = Logger.getLogger(OpUpdateBDMS.class.getName());

    private final DatabaseService dbService;

    public OpUpdateBDMS(DatabaseService dbService) {
        this.dbService = dbService;
    }

    public int updateToStatusCierre0TerminalCierreInsistentes(Vector terminalIds) throws SQLMSBDException {

        if (terminalIds == null || terminalIds.isEmpty()) {
            logger.warn("No se proporcionaron terminales para actualizar.");
            return 0;
        }

        String query = Update.setStatusCierre0Terminal(terminalIds);

        try {
            int resultado = dbService.ejecutarOperacion(query);

            if (resultado > 0 && logger.isInfoEnabled()) {
                logger.info("Se actualizaron " + resultado + " tuplas de la tabla Terminals");
            }

            return resultado;

        } catch (SQLMSBDException e) {
            logger.error("Error al ejecutar la operación de actualización para los terminales.", e);
            throw new SQLMSBDException("Error ejecutando la actualización de estado de terminales a cierre 0.", e);
        }
    }


}
