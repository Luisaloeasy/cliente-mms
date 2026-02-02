package com.megasoft.merchant.mms.persistence.service;

import com.megasoft.merchant.mms.negocio.Terminal;
import com.megasoft.merchant.mms.persistence.DatabaseService;
import com.megasoft.merchant.mms.persistence.exception.SQLMSBDException;
import com.megasoft.merchant.mms.persistence.queries.Select;
import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * @author Luis Aloisi
 */
public class OpGetBDMS {

	private static final Logger logger = Logger.getLogger(OpGetBDMS.class.getName());
    private final DatabaseService dbServiceMerchant;
    private final DatabaseService dbServiceHistory;


    public OpGetBDMS(DatabaseService dbServiceMerchant, DatabaseService dbServiceHistory) {
        this.dbServiceMerchant = dbServiceMerchant;
        this.dbServiceHistory = dbServiceHistory;

    }

    public Vector getTerminalConCierreInsistentes(Timestamp fechaInicio, Timestamp fechaFin, int numInsistencias) throws SQLMSBDException {

        // Consulta para obtener datos de transaclog en MS_History
        String queryTransaclog = Select.terminalCierreInsistenteTransaclog(fechaInicio, fechaFin, numInsistencias);
        try (ResultSet rsTransaclog = dbServiceHistory.ejecutarConsulta(queryTransaclog)) {

            // Obtener los vtid y paymentid de la primera consulta
            List<String> vtids = new ArrayList<>();
            List<String> paymentids = new ArrayList<>();

            while (rsTransaclog.next()) {
                vtids.add(rsTransaclog.getString("vtid"));
                paymentids.add(rsTransaclog.getString("paymentid"));
            }

            if (vtids.isEmpty() || paymentids.isEmpty()) {
                logger.warn("No se encontraron registros en la consulta de transaclog.");
                return new Vector<>();  // Si no hay datos, retornar un vector vacío.
            }
            // Consulta para combinar datos en Merchant1 usando los vtid y paymentid obtenidos
            String queryMerchant = Select.terminalCierreInsistenteMerchant(vtids, paymentids);

            try (ResultSet rsMerchant = dbServiceMerchant.ejecutarConsulta(queryMerchant)) {

                Vector terCierreInsis = new Vector();

                while (rsMerchant.next()) {
                    Terminal ter = new Terminal(rsMerchant.getInt("terminalid"), rsMerchant.getString("tid"), rsMerchant.getString("description"));
                    terCierreInsis.add(ter);
                }

                return terCierreInsis;
            } catch (SQLException e) {
                logger.error("Error en la consulta de terminales en Merchant1", e);
                throw new SQLMSBDException("Error al ejecutar la consulta en Merchant1", e);
            }
        } catch (SQLException e) {
            logger.error("Error al ejecutar la consulta de transaclog en MS_History", e);
            throw new SQLMSBDException("No se pudo cargar los datos del ResultSet en MS_History", e);
        }
    }
}