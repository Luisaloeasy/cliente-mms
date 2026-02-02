package com.megasoft.merchant.mms.transporte;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;

import com.megasoft.merchant.mms.configuracion.FachadaConf;
import com.megasoft.merchant.mms.configuracion.exception.EntityConfigurationLoadException;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import com.rabbitmq.client.ShutdownSignalException;
import org.json.JSONObject;

/**
 * @author Luis Aloisi & Michael Amariscua
 *
 */
public class FachadaEnviarRabbit extends RabbitConexion {

	public FachadaEnviarRabbit() throws Exception {
		super();
		// TODO Auto-generated constructor stub
	}

	private static Logger logger = Logger.getLogger(FachadaEnviarRabbit.class.getName());

	public static Channel channel;

	public synchronized static void envioRabbitMQ(String exchangeName, String routingKey, String queueName, ArrayList<JSONObject> message)
			throws Exception {

		try {

			if (connection != null) {

				try {

					channel = connection.createChannel();
					channel.exchangeDeclare(exchangeName, BuiltinExchangeType.DIRECT, true, false, null);
					channel.queueDeclare(queueName, true, false, false, null);
					channel.queueBind(queueName, exchangeName, routingKey);
					channel.confirmSelect();
					channel.basicPublish(exchangeName, routingKey, MessageProperties.PERSISTENT_TEXT_PLAIN,
							message.toString().getBytes(StandardCharsets.UTF_8));
					channel.close();

				} catch (IOException | TimeoutException | ShutdownSignalException e) {
					
					logger.error("MMS v2 -- Se perdio la conexion con el Servidor RabbitMQ " ,e);

					connection.abort();

					connection = null;

					try {

						conexion();
						
						logger.info("MMS v2 -- Se ha reestablecido la conexi�n con el Servidor RabbitMQ ");


					} catch (EntityConfigurationLoadException | NumberFormatException | IOException
							| TimeoutException e2) {
						
						logger.error("MMS v2 -- Error reestableciendo la conexi�n con el Servidor RabbitMQ " ,e2);

						connection = null;

						envioRabbitMQ(exchangeName, routingKey, queueName, message);
					}

					if (connection != null) {

						envioRabbitMQ(exchangeName, routingKey, queueName, message);

					}
				}

			} else {

				connection = null;

				try {

					try {

							LinkedHashMap configuracionEnvio = FachadaConf.cargarConfEntidadDeGeneral("ConfigFiles/GeneralConfig.xml", "RabbitEnvio");
														
							RabbitConexion.setEstablishConnection(configuracionEnvio.get("EstablishConnection").toString());
										
							boolean connect = establishConnection.equalsIgnoreCase("True");

							if(connect) {
												
								logger.info("MMS v2 -- establishConnection True Fachada, Estableciendo Conexion contra RabbitMQ");
								
								conexion();
							}else {
								logger.info("MMS v2 -- Se han agregado las tareas de RabbitMQ al SchedulerConfigCliente y el parametro establishConnection se encuentra False");
							}
							

					} catch (EntityConfigurationLoadException | NumberFormatException | IOException
							| TimeoutException e2) {
						
						logger.error("MMS v2 -- Error Cargando la configuracion de RabbitMQ en Fachada", e2);
						logger.error("MMS v2 -- Error creando la nueva conexion fisica ",e2);

						connection = null;

						Thread.sleep(5000);

						envioRabbitMQ(exchangeName, routingKey, queueName, message);

					}

				} catch (Exception e) {

					logger.error("MMS v2 -- Error creando la nueva conexion fisica ",e);
				}

			}

		} catch (ShutdownSignalException e) {

			logger.error("MMS v2 -- Error Enviando el mensaje a la cola " ,e);
		}

	}

	public static void close() {
		try {
			channel.close();
		} catch (IOException | TimeoutException | ShutdownSignalException e) {
			logger.error("MMS v2 -- Error cerrando el canal '" + e.getMessage() + "'", e);
		}

	}

}
