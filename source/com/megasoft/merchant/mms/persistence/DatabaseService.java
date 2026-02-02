package com.megasoft.merchant.mms.persistence;

import com.megasoft.merchant.mms.persistence.exception.SQLMSBDException;

import java.sql.ResultSet;

public interface DatabaseService {


    /**
     * Ejecuta una consulta SQL y devuelve un ResultSet.
     *
     * @param query Consulta SQL a ejecutar.
     * @return ResultSet con los resultados de la consulta.
     * @throws SQLMSBDException Si ocurre un error durante la ejecución.
     */
    ResultSet ejecutarConsulta(String query) throws SQLMSBDException;

    /**
     * Ejecuta una operación SQL de inserción, actualización o eliminación.
     *
     * @param query Consulta SQL de tipo DML (INSERT, UPDATE, DELETE).
     * @return Número de filas afectadas.
     * @throws SQLMSBDException Si ocurre un error durante la ejecución.
     */
    int ejecutarOperacion(String query) throws SQLMSBDException;

    /**
     * Prueba la conexión a la base de datos ejecutando `SELECT 1`.
     *
     * @throws SQLMSBDException Si ocurre un problema al conectar con la BD.
     */
    void probarConexion() throws SQLMSBDException;

}
