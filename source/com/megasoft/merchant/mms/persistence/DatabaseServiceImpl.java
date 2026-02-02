package com.megasoft.merchant.mms.persistence;

import com.megasoft.merchant.mms.persistence.exception.SQLMSBDException;
import org.apache.log4j.Logger;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Clase que maneja la conexión utilizando HikariCP con dos DataSources.
 */
public class DatabaseServiceImpl implements DatabaseService {

    static private final Logger logger = Logger.getLogger(DatabaseServiceImpl.class.getName());
    private final ConnectionManager connectionManager;

    public DatabaseServiceImpl(boolean useMerchantDatabase) {
        this.connectionManager = new ConnectionManager(useMerchantDatabase);
    }

    /**
     * Ejecuta una consulta SQL y devuelve un ResultSet.
     *
     * @param query Consulta SQL a ejecutar.
     * @return ResultSet con los resultados de la consulta.
     * @throws SQLMSBDException Si ocurre un error durante la ejecución.
     */
    @Override
    public ResultSet ejecutarConsulta(String query) throws SQLMSBDException {
        CachedRowSet crs = null;
        try {
            probarConexion();
            RowSetFactory aFactory = RowSetProvider.newFactory();
            crs = aFactory.createCachedRowSet();
            logger.info("Script SQL: "+query);
            try(Connection conn = connectionManager.getConnection()) {

                conn.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);

                try(PreparedStatement stmt = conn.prepareStatement(query);
                    ResultSet rset = stmt.executeQuery()) {
                    crs.populate(rset);
                }
            }
            return crs;
        } catch (SQLException e) {
            throw new SQLMSBDException("Error ejecutando consulta: " + query, e);
        } catch (Exception e) {
            logger.error("Error en la consulta ejecutarConsulta ",e);
        }
        return crs;
    }

    /**
     * Ejecuta una operación SQL de inserción, actualización o eliminación.
     *
     * @param query Consulta SQL de tipo DML (INSERT, UPDATE, DELETE).
     * @return Número de filas afectadas.
     * @throws SQLMSBDException Si ocurre un error durante la ejecución.
     */
    @Override
    public int ejecutarOperacion(String query) throws SQLMSBDException {
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            int rows = stmt.executeUpdate();
            conn.commit();
            return rows;

        } catch (SQLException e) {
            throw new SQLMSBDException("Error ejecutando operación: " + query, e);
        }
    }


    /**
     * Prueba la conexión a la base de datos ejecutando `SELECT 1`.
     *
     * @throws SQLMSBDException Si ocurre un problema al conectar con la BD.
     */
    @Override
    public void probarConexion() throws SQLMSBDException {
        long timeStart = System.currentTimeMillis();

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT 1")) {

            stmt.executeQuery();
            long timeEnd = System.currentTimeMillis();
            logger.info("Conexión probada correctamente en " + (timeEnd - timeStart) + " ms.");

        } catch (SQLException e) {
            throw new SQLMSBDException("⚠ Error probando la conexión", e);
        }
    }
}

