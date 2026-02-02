package com.megasoft.merchant.mms.persistence.service;

import com.megasoft.merchant.mms.persistence.exception.InvalidDataMSBDException;
import com.megasoft.merchant.mms.persistence.exception.SQLMSBDException;
import com.megasoft.merchant.mms.persistence.response.QueryConfigResult;
import com.megasoft.merchant.mms.persistence.response.QueryMMSResult;
import com.megasoft.merchant.mms.persistence.DatabaseService;
import com.megasoft.merchant.mms.persistence.queries.Select;
import com.megasoft.merchant.mms.tarea.util.TareaUtil;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase que contiene métodos para obtener DTOs a partir de la base de datos.
 * @author Luis Aloisi
 */
public class OpGetBD {

    private static final Logger logger = Logger.getLogger(OpGetBD.class);
    private final DatabaseService dbService;

    private static final TareaUtil tareaUtil = new TareaUtil();

    public OpGetBD(DatabaseService dbService) {
        this.dbService = dbService;
    }

    /**
     * Ejecuta una consulta, mide tiempos, la convierte a ArrayList<JSONObject>,
     * elimina columnas no deseadas según configuración y guarda en un archivo Temp.
     *
     * @param query    Consulta SQL a ejecutar.
     * @param fileName Nombre "base" que se usará para:
     *                 (1) leer columnas a eliminar (archivo JSON),
     *                 (2) generar el archivo de salida con sufijo "Temp".
     *
     * @return Objeto con la data en JSON y tiempos de ejecución, o null si falla.
     */
    private QueryConfigResult executeQueryAndConvertToJSON(String query, String fileName) {
        long startQueryExecutionTime = System.nanoTime();
        try (ResultSet rs = dbService.ejecutarConsulta(query)) {
            long endQueryExecutionTime = System.nanoTime();
            double runtimeQuery = (endQueryExecutionTime - startQueryExecutionTime) / 1e6;
            long startJsonExecutionTime = System.nanoTime();

            ArrayList<JSONObject> resultadoFinal = resultSetToJsonList(rs, fileName);

            filterColumnsFromJsonAndCreateTempFile(resultadoFinal, fileName);

            long endJsonExecutionTime = System.nanoTime();
            double runtimeJson = (endJsonExecutionTime - startJsonExecutionTime) / 1e6;

            return new QueryConfigResult(resultadoFinal, runtimeQuery, runtimeJson);

        } catch (Exception e) {
            logger.error("MMS v2 -- Ocurrio un error en el query hacia la BD o transformando "
                    + "el ResultSet a JSON " + fileName, e);
            return null;
        }
    }

    /**
     * Convierte un ResultSet en una lista de objetos JSON.
     *
     * @param rs       ResultSet obtenido de la consulta.
     * @param fileName Para reflejar en el log de errores a quién corresponde.
     *
     * @return Lista de JSONObject con los datos.
     */
    private ArrayList<JSONObject> resultSetToJsonList(ResultSet rs, String fileName) throws SQLException {
        ArrayList<JSONObject> jsonList = new ArrayList<>();
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        List<String> columnNames = new ArrayList<>(columnCount);

        for (int i = 1; i <= columnCount; i++) {
            columnNames.add(metaData.getColumnName(i));
        }

        while (rs.next()) {

            JSONObject row = new JSONObject();

            for (String columnName : columnNames) {
                try {
                    Object columnValue = rs.getObject(columnName);
                    row.put(columnName, columnValue != null && !columnValue.toString().isEmpty() ? columnValue : "");
                } catch (JSONException | SQLException e) {
                    logger.error("MMS v2 -- Ocurrio un error parseando los registros de la BD "
                            + "al Array de JSON en OpGetBDold " + fileName
                            + ". Columna: " + columnName, e);
                }
            }
            jsonList.add(row);
        }
        return jsonList;
    }

    /**
     * Elimina columnas no deseadas según un archivo de configuración JSON
     * que se lee mediante TareaUtil.readNamesFromJSONFile(...).
     *
     * @param jsonList Lista de objetos JSON.
     * @param fileName Nombre del archivo base para leer columnas.
     */
    private void filterColumnsFromJsonAndCreateTempFile(ArrayList<JSONObject> jsonList, String fileName) throws IOException {

        List<String> lowercaseColumnsToRemove = TareaUtil.readNamesFromJSONFile(fileName);
        if (lowercaseColumnsToRemove.isEmpty()) {
            tareaUtil.resultSetToFileJSON(jsonList.toString(), fileName + "Temp");
            return;
        }

        JSONArray jsonArray = new JSONArray(jsonList);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject row = new JSONObject(jsonArray.get(i).toString());
            for (String key : row.keySet().toArray(new String[0])) {
                if (lowercaseColumnsToRemove.contains(key.toLowerCase())) {
                    row.remove(key);
                }
            }
            jsonArray.put(i, row);
        }

        tareaUtil.resultSetToFileJSON(jsonArray.toString(), fileName + "Temp");
    }

    /**
     * Convierte un ResultSet en una lista de objetos JSON mientras elimina columnas no deseadas.
     * Este metodo se encarga de leer los datos de la base de datos, transformarlos en formato JSON
     * y eliminar aquellas columnas que han sido configuradas como innecesarias a través de un archivo JSON.
     *
     * @param rs       ResultSet obtenido de la consulta SQL.
     * @param fileName Nombre base del archivo JSON usado para la configuración de columnas a eliminar.
     * @return Lista de objetos JSON con los datos procesados y filtrados.
     * @throws SQLException Si ocurre un error con la lectura del ResultSet.
     */
    private ArrayList<JSONObject> resultSetToJsonListWithFilter(ResultSet rs, String fileName) throws SQLException {

        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        List<String> columnNames = new ArrayList<>(columnCount);
        ArrayList<JSONObject> jsonList = new ArrayList<>();


        for (int i = 1; i <= columnCount; i++) {
            columnNames.add(metaData.getColumnName(i));
        }

        List<String> lowercaseColumnsToRemove = TareaUtil.readNamesFromQueryColumnsIgnoredJSONFile(fileName);

        while (rs.next()) {
            JSONObject row = new JSONObject();

            for (String columnName : columnNames) {
                if (!lowercaseColumnsToRemove.contains(columnName.toLowerCase())) {
                    try {
                        Object columnValue = rs.getObject(columnName);
                        row.put(columnName, columnValue != null && !columnValue.toString().isEmpty() ? columnValue : "");
                    } catch (JSONException | SQLException e) {
                        logger.error("MMS v2 -- Error pasando registros de la BD a JSON en OpGetBDold "
                                + fileName + ". Columna: " + columnName, e);
                    }
                }
            }
            jsonList.add(row);
        }

        return jsonList;
    }

    /**
     * Ejecuta una consulta SQL, mide los tiempos de ejecución, convierte los resultados a JSON
     * y filtra las columnas no deseadas según la configuración establecida.
     * Este metodo es utilizado principalmente para consultas que involucran un rango de fechas (timestamps).
     *
     * @param query    Consulta SQL completa ya construida.
     * @param fileName Nombre base para la configuración y el archivo JSON de salida.
     * @return Objeto `QueryMMSResult` con la data JSON filtrada y los tiempos de ejecución.
     */
    private QueryMMSResult executeQueryWithTimestampAndConvertToJSON(String query, String fileName) {
        long startQueryExecutionTime = System.nanoTime();
        try (ResultSet rs = dbService.ejecutarConsulta(query)) {
            long endQueryExecutionTime = System.nanoTime();
            double runtimeQuery = (endQueryExecutionTime - startQueryExecutionTime) / 1e6;
            long startJsonExecutionTime = System.nanoTime();

            ArrayList<JSONObject> resultadoFinal = resultSetToJsonListWithFilter(rs, fileName);

            long endJsonExecutionTime = System.nanoTime();
            double runtimeJson = (endJsonExecutionTime - startJsonExecutionTime) / 1e6;

            return new QueryMMSResult(resultadoFinal, runtimeQuery, runtimeJson);
        } catch (Exception e) {
            logger.error("MMS v2 -- Ocurrió un error en el query hacia la BD o transformando "
                    + "el ResultSet a JSON " + fileName, e);
            return null;
        }
    }


    public QueryConfigResult getRetailerMQ() throws Exception {
        return executeQueryAndConvertToJSON(Select.retailerMQ(), "retailers");
    }

    public QueryConfigResult getMerchantsMQ() throws Exception {
        return executeQueryAndConvertToJSON(Select.merchantsMQ(), "merchants");
    }

    public QueryConfigResult getBanksMQ() throws Exception {
        return executeQueryAndConvertToJSON(Select.banksMQ(), "banks");
    }

    public QueryConfigResult getTransactionsMQ() throws Exception {
        return executeQueryAndConvertToJSON(Select.transactionsMQ(), "transactions");
    }

    public QueryConfigResult getVTerminalsMQ() throws Exception {
        return executeQueryAndConvertToJSON(Select.vTerminalsMQ(), "vTerminals");
    }

    public QueryConfigResult getGroupsMQ() throws Exception {
        return executeQueryAndConvertToJSON(Select.groupsMQ(), "groups");
    }

    public QueryConfigResult getMerchantPaysMQ() throws Exception {
        return executeQueryAndConvertToJSON(Select.merchantPaysMQ(), "merchantPays");
    }

    public QueryConfigResult getTerminalsMQ() throws Exception {
        return executeQueryAndConvertToJSON(Select.terminalsMQ(), "terminals");
    }

    public QueryConfigResult getVTerminalTerminalsMQ() throws Exception {
        return executeQueryAndConvertToJSON(Select.vterminalTerminalsMQ(), "vTerminalTerminals");
    }

    public QueryConfigResult getPaymentsMQ() throws Exception {
        return executeQueryAndConvertToJSON(Select.paymentsMQ(), "payments");
    }

    public QueryConfigResult getAcquirersMQ() throws Exception {
        return executeQueryAndConvertToJSON(Select.acquirersMQ(), "acquirers");
    }

    public QueryConfigResult getCardsMQ() throws Exception {
        return executeQueryAndConvertToJSON(Select.cardsMQ(), "cards");
    }

    public QueryConfigResult getAccountsMQ() throws Exception {
        return executeQueryAndConvertToJSON(Select.accountsMQ(), "accounts");
    }

    public QueryConfigResult getRespCodeMQ() throws Exception {
        return executeQueryAndConvertToJSON(Select.respCodeMQ(), "respCodes");
    }

    public QueryConfigResult getPaymentRespCodeMQ() throws Exception {
        return executeQueryAndConvertToJSON(Select.paymentRespCodeMQ(), "paymentRespCodes");
    }

    public QueryConfigResult getNodosMQ() throws Exception {
        return executeQueryAndConvertToJSON(Select.nodosMQ(), "nodos");
    }

    public QueryConfigResult getNodosAcquirersMQ() throws Exception {
        return executeQueryAndConvertToJSON(Select.nodosAcquirerMQ(), "nodosAcquirers");
    }

    public QueryConfigResult getMPaysCardsMQ() throws Exception {
        return executeQueryAndConvertToJSON(Select.mPaysCardsMQ(), "mPaysCards");
    }

    public QueryConfigResult getMedioPagoMQ() throws Exception {
        return executeQueryAndConvertToJSON(Select.medioPagoMQ(), "medioPagos");
    }

    public QueryConfigResult getCaptureModeMQ() throws Exception {
        return executeQueryAndConvertToJSON(Select.captureModeMQ(), "captureMode");
    }

    public QueryConfigResult getTransPaysMQ() throws Exception{
        return executeQueryAndConvertToJSON(Select.transPaysMQ(), "transPays");
    }

    public QueryConfigResult getSecurityKeysMQ() throws Exception{
        return executeQueryAndConvertToJSON(Select.securityKeysMQ(), "securityKeys");
    }

    public QueryConfigResult getBinsMQ() throws Exception{
        return executeQueryAndConvertToJSON(Select.binsMQ(), "bins");
    }

    public QueryConfigResult getBinTablesMQ() throws Exception{
        return executeQueryAndConvertToJSON(Select.binTablesMQ(), "binTables");
    }

    public QueryConfigResult getBinTablesBinsMQ() throws Exception{
        return executeQueryAndConvertToJSON(Select.binTablesBinsMQ(), "binTablesBins");
    }

    public QueryConfigResult getRetailerPaysMQ() throws Exception{
        return executeQueryAndConvertToJSON(Select.retailerPaysMQ(), "retailerPays");
    }

    public QueryConfigResult getMPaysIssuerSwMQ() throws Exception{
        return executeQueryAndConvertToJSON(Select.mPaysIssuerSwMQ(), "mPaysIssuerSw");
    }

    public QueryConfigResult getUniversalBinsMQ() throws Exception{
        return executeQueryAndConvertToJSON(Select.universalBinsMQ(), "universalBins");
    }

    public QueryConfigResult getRPaysIssuerSwMQ() throws Exception{
        return executeQueryAndConvertToJSON(Select.rPaysIssuerSwMQ(), "rPaysIssuerSw");
    }

    public QueryConfigResult getRPaysCardsMQ() throws Exception{
        return executeQueryAndConvertToJSON(Select.rPaysCardsMQ(), "rPaysCards");
    }

    public QueryMMSResult getHistory(Timestamp fechaInicio, Timestamp fechaFin) throws InvalidDataMSBDException, SQLMSBDException, SQLException {
        return executeQueryWithTimestampAndConvertToJSON(Select.history(fechaInicio, fechaFin), "history");
    }

    public QueryMMSResult getBatch(Timestamp fechaInicio, Timestamp fechaFin) throws InvalidDataMSBDException, SQLMSBDException, SQLException {
        return executeQueryWithTimestampAndConvertToJSON(Select.batch(fechaInicio, fechaFin), "batch");
    }

    public QueryMMSResult getTransaclog(Timestamp fechaInicio, Timestamp fechaFin) throws InvalidDataMSBDException, SQLMSBDException, SQLException {
        return executeQueryWithTimestampAndConvertToJSON(Select.transacLog(fechaInicio, fechaFin), "transaclog");
    }

}
