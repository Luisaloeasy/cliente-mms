package com.megasoft.merchant.mms.comunicacion;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeoutException;

import com.megasoft.merchant.mms.comunicacion.exception.ConfigurationLoadErrorMSComunicacionException;
import com.megasoft.merchant.mms.configuracion.FachadaConf;
import com.megasoft.merchant.mms.configuracion.exception.EntityConfigurationLoadException;
import org.apache.log4j.Logger;


import com.megasoft.merchant.mms.tarea.util.TareaUtil;
import com.megasoft.merchant.mms.transporte.FachadaEnviarRabbit;
import org.json.JSONObject;

/**
 * Esta clase se encarga de realizar todos los procesos de Comunicacion entre
 * las diferentes componentes del sistema MMS. En esta clase se realiza todas
 * las actividades de traduccion y transporte de la informacion
 *
 * @author Luis Alejandro Aloisi Millan
 * @author Michael Amariscua
 */
public class ComunicacionRabbitMQ {

    private static final Logger logger = Logger.getLogger(ComunicacionRabbitMQ.class.getName());
    private static int cantidadDeRegistros;
    private static final double MAX_ELEMENTS_PER_EVENT = 1000.00;

    public static TareaUtil tareaUtil = new TareaUtil();
	private static String exchangeName;
	private static String routingKey;
	private static String queueName;
	private static String metodoDeEnvio;

	public static void enviar(String metodoEnvio, TokenDatosMQ datosAEnviar) throws Exception {
		try {
			if (metodoEnvio.equalsIgnoreCase("Rabbit")) {

				LinkedHashMap configMQ = FachadaConf.cargarConfEntidadDeGeneral("ConfigFiles/GeneralConfig.xml",
						"RabbitEnvio");

				exchangeName = configMQ.get("ExchangeName").toString();
				routingKey = configMQ.get("RoutingKey").toString();
				queueName = configMQ.get("QueueName").toString();
				metodoDeEnvio = configMQ.get("metodoEnvio").toString();

				enviar(datosAEnviar);
			} else {
				logger.error("MMS v2 -- Ocurrio un error ya que no coincide el metodo de envio con Rabbit, metodoEnvio: " + metodoEnvio);
			}
		} catch (EntityConfigurationLoadException e) {
			throw new ConfigurationLoadErrorMSComunicacionException("MMS v2 -- No se pudo cargar los datos de la configuracion ", e);
		}
	}

    private static void sendRabbitMQ(ArrayList<JSONObject> jsonArray) {
        try {
            FachadaEnviarRabbit.envioRabbitMQ(exchangeName,
                    routingKey,
                    queueName,
                    jsonArray);
        } catch (Exception e) {
            logger.error("MMS v2 -- Error enviando el mensaje sendRabbitMQ exchangeName: " + exchangeName + " routingKey: " + routingKey + " queueName: "+ queueName, e);
        }
    }


    /**
     * Envía los datos utilizando el método de envío especificado en la configuración.
     *
     * @param datosAEnviar       Los datos a enviar.
     * @throws IOException       Si ocurre un error de entrada/salida.
     * @throws TimeoutException  Si ocurre un error de timeout.
     */

    public static void enviar(TokenDatosMQ datosAEnviar) throws IOException, TimeoutException {

        try {
            ArrayList<JSONObject> jsonArray;

            int totalElements = datosAEnviar.getElementsJsonArray().size();

            if (totalElements <= MAX_ELEMENTS_PER_EVENT) {

                jsonArray = Traductor.tokenDatosMQToJsonArray(datosAEnviar);

                if (metodoDeEnvio.equalsIgnoreCase("Rabbit")) {
                    sendRabbitMQ(jsonArray);
                } else {
                    logger.error("MMS v2 -- Ocurrio un error ya que no coincide el metodo de envio con Rabbit");
                }

            } else {

                int cantidadMensajesEnviar = (int) Math.ceil(totalElements / MAX_ELEMENTS_PER_EVENT);
                int cantidadAenviar = totalElements / cantidadMensajesEnviar;

                String nombreCliente = datosAEnviar.clientName;
                int idCliente = datosAEnviar.clientId;
                int puerto = datosAEnviar.port;
                String nombreProcesamientoServerMMS = datosAEnviar.processingName;
                String identificador = datosAEnviar.messageId;
                String fechaInicio = datosAEnviar.startDate;
                String fechaFin = datosAEnviar.endDate;
                String nombreTabla = datosAEnviar.tableName;
                double runtime = datosAEnviar.runtime;
                double queryRuntime = datosAEnviar.queryRuntime;
                double arrayJsonRuntime = datosAEnviar.arrayJsonRuntime;
                double intervalRuntime = datosAEnviar.intervalRuntime;
                boolean excludingColumns = datosAEnviar.excludingColumns;
                long startExecutionTime;
                long endExecutionTime;

                for (int i = 0; i < cantidadMensajesEnviar; i++) {

                    startExecutionTime = System.nanoTime();

                    TokenDatosMQ parteDatos = new TokenDatosMQ();

                    jsonArray = getSplitData(i, cantidadAenviar, datosAEnviar, cantidadMensajesEnviar);

                    try {
                        parteDatos.setChecksum("0");
//                        parteDatos.setChecksum(tareaUtil.calcularCheckSum(jsonArray));
                    } catch (Exception e1) {
                        logger.error("MMS v2 -- Ocurrio un error calculando el checksum en el split del vector");
                        parteDatos.setChecksum("0");
                    }

                    parteDatos.setElementsJsonArray(jsonArray);
                    parteDatos.setMessageId(identificador + "_V_" + (i + 1));
                    parteDatos.setClientName(nombreCliente);
                    parteDatos.setProcessingName(nombreProcesamientoServerMMS);
                    parteDatos.setClientId(idCliente);
                    parteDatos.setPort(puerto);
                    parteDatos.setStartDate(fechaInicio);
                    parteDatos.setEndDate(fechaFin);
                    parteDatos.setTotalMessages(cantidadMensajesEnviar);
                    parteDatos.setTableName(nombreTabla);
                    parteDatos.setExcludingColumns(excludingColumns);
                    parteDatos.setTotalElements(cantidadDeRegistros);
                    endExecutionTime = System.nanoTime();

                    if (i == 0) {
                        parteDatos.setRuntime(((endExecutionTime - startExecutionTime) / 1e6) + runtime);
                        parteDatos.setQueryRuntime(queryRuntime);
                        parteDatos.setArrayJsonRuntime(arrayJsonRuntime);
                        parteDatos.setIntervalRuntime(intervalRuntime);
                    } else {
                        parteDatos.setRuntime((endExecutionTime - startExecutionTime) / 1e6);
                        parteDatos.setQueryRuntime(0);
                        parteDatos.setArrayJsonRuntime(0);
                        parteDatos.setIntervalRuntime(0);
                    }

                    jsonArray = Traductor.tokenDatosMQToJsonArray(parteDatos);

                    if (metodoDeEnvio.equalsIgnoreCase("Rabbit")) {
                        sendRabbitMQ(jsonArray);
                    } else {
                        logger.error("MMS v2 -- Ocurrio un error ya que no coincide el metodo de envio con Rabbit");
                    }
                }
            }
        } catch (Exception e) {
            logger.error("MMS v2 -- Ocurrio un error en Comunicacion History " + e.getMessage());
        }
    }

    public static ArrayList<JSONObject> getSplitData(int currentPart, int elementsPerPart, TokenDatosMQ dataToSend, int totalParts) {
        try {
            int startIndex = currentPart * elementsPerPart;
            int endIndex = Math.min(startIndex + elementsPerPart, dataToSend.getElementsJsonArray().size());
            ArrayList<JSONObject> jsonArray = new ArrayList<>(dataToSend.getElementsJsonArray().subList(startIndex, endIndex));
            if (currentPart == totalParts - 1 && endIndex < dataToSend.getElementsJsonArray().size()) {
                jsonArray.addAll(dataToSend.getElementsJsonArray().subList(endIndex, dataToSend.getElementsJsonArray().size()));
            }
            cantidadDeRegistros = jsonArray.size();

            return jsonArray;

        } catch (Exception e) {
            logger.error("MMS v2 -- Ocurrio un error getSplitData ", e);
            return null;
        }
    }


}
