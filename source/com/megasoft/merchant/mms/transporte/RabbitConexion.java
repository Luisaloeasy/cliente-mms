package com.megasoft.merchant.mms.transporte;

import com.megasoft.merchant.mms.configuracion.FachadaConf;
import com.megasoft.merchant.mms.configuracion.exception.EntityConfigurationLoadException;
import com.megasoft.merchant.mms.tarea.simple.envio.Envio;
import com.megasoft.merchant.mms.transporte.model.CredencialesRabbit;
import com.megasoft.merchant.mms.transporte.model.KeyStoreRabbit;
import com.rabbitmq.client.Address;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ShutdownSignalException;
import org.apache.log4j.Logger;
import ve.com.megasoft.clientegc.logica.ClienteGCImplREST;
import ve.com.megasoft.clientegc.modelo.CredencialesBaseDeDatos;
import ve.com.megasoft.clientegc.modelo.CredencialesKeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.util.*;
import java.util.concurrent.TimeoutException;


/**
 * @author Luis Aloisi & Michael Amariscua
 *
 */
public class RabbitConexion extends Envio {

	private static Logger logger = Logger.getLogger(RabbitConexion.class.getName());

	public static Connection connection;

	public static String virtualHost;
	public static String nodo1;
	public static String nodo2;
	public static String nodo3;

	public static int port;
	public static String userName;
	public static String password;
	public static String connectionName;
	public static String establishConnection;

	public static String algorithm;
	public static String keyStoreType;
	public static String keyStorePassword;


	public RabbitConexion() {

		try {

			LinkedHashMap configuracionEnvio = FachadaConf.cargarConfEntidadDeGeneral("ConfigFiles/GeneralConfig.xml", "RabbitEnvio");

			RabbitConexion.setEstablishConnection(configuracionEnvio.get("EstablishConnection").toString());
			boolean connect = establishConnection.equals("True");

			if(connect) {

				logger.info("MMS v2 -- establishConnection True, se establecera conexion con RabbitMQ");

				obtenerKeystore();

				logger.info("MMS v2 -- Cargando configuracion de RabbitMQ");

				LinkedHashMap confCliente = FachadaConf.cargarConfEntidadDeGeneral("ConfigFiles/GeneralConfig.xml","ConfCliente");
				RabbitConexion.nodo1 = configuracionEnvio.get("Nodo1") != null ? configuracionEnvio.get("Nodo1").toString() : "";
				RabbitConexion.nodo2 = configuracionEnvio.get("Nodo2") != null ? configuracionEnvio.get("Nodo2").toString() : "";
				RabbitConexion.nodo3 = configuracionEnvio.get("Nodo3") != null ? configuracionEnvio.get("Nodo3").toString() : "";
				RabbitConexion.port = Integer.parseInt(configuracionEnvio.get("Puerto").toString());
				RabbitConexion.connectionName = confCliente.get("nombreCliente") != null ? confCliente.get("nombreCliente").toString() : "";

				if (isNotEmpty(CredencialesRabbit.getId())
						&& isNotEmpty(CredencialesRabbit.getUsuario())
						&& isNotEmpty(CredencialesRabbit.getContrasena())
						&& isNotEmpty(CredencialesRabbit.getVirtualHost())) {

					RabbitConexion.password = CredencialesRabbit.getContrasena();
					RabbitConexion.userName = CredencialesRabbit.getUsuario();
					RabbitConexion.virtualHost = CredencialesRabbit.getVirtualHost();
					logger.info("MMS v2 -- Se setearon los valores de configuracion de las credenciales de RMQ con la clase CredencialesRabbit que ya tiene los valores almacenados.");

				}else{
					String usuario = configuracionEnvio.get("Usuario") != null ? configuracionEnvio.get("Usuario").toString() : "";
					String clave = configuracionEnvio.get("Clave") != null ? configuracionEnvio.get("Clave").toString() : "";
					String virtualHost = configuracionEnvio.get("VirtualHost") != null ? configuracionEnvio.get("VirtualHost").toString() : "";

					if (usuario.isEmpty() || clave.isEmpty()) {

						LinkedHashMap cred = FachadaConf.cargarConfEntidadDeGeneral("ConfigFiles/GeneralConfig.xml", "GestorCredenciales");

						String url = cred.get("url") != null ? cred.get("url").toString() : "";
						String jwt = cred.get("jwt") != null ? cred.get("jwt").toString() : "";
						String keyCredentialsRMQ = cred.get("gecre.cred.rmq") != null ? cred.get("gecre.cred.rmq").toString() : "";

						CredencialesBaseDeDatos gestorCredenciales = obtenerCredencialesDesdeREST(url, jwt, keyCredentialsRMQ);
						CredencialesRabbit.setId(gestorCredenciales.getId());
						CredencialesRabbit.setUsuario(gestorCredenciales.getUsuario());
						CredencialesRabbit.setContrasena(gestorCredenciales.getContrasena());
						CredencialesRabbit.setVirtualHost(gestorCredenciales.getNombreBaseDatos());

						RabbitConexion.password = gestorCredenciales.getContrasena();
						RabbitConexion.userName = gestorCredenciales.getUsuario();
						RabbitConexion.virtualHost = gestorCredenciales.getNombreBaseDatos();
						logger.info("MMS v2 -- Se setearon los valores de configuracion de credenciales de RMQ usando el gestor de credenciales.");

					}else{
						RabbitConexion.password = clave;
						RabbitConexion.userName = usuario;
						RabbitConexion.virtualHost = virtualHost;
						logger.info("MMS v2 -- Se setearon los valores de configuracion de RMQ usando RabbitMqEnvioConfig.");
					}
				}

			}else{
				logger.info("MMS v2 -- establishConnection False, no se establecera conexion con RabbitMQ");
			}

		} catch (EntityConfigurationLoadException e) {

			logger.error("MMS v2 -- Error Cargando la configuracion de RabbitMQ", e);

		}
	}

	public void initialize() throws Exception {
		try {
			if (logger.isInfoEnabled()) {
				logger.info("MMS v2 -- Iniciando proceso en el Main de conexion a RabbitMQ ");
			}

			conexion();

			if (logger.isInfoEnabled()) {
				logger.info("MMS v2 -- Termino proceso de conexion a RabbitMQ en el Main");
			}
		} catch (Exception e) {
			logger.error("MMS v2 -- Error durante la inicialización de la conexion de Rabbit", e);
		}
	}

	public static void conexion() throws Exception {

		if (logger.isInfoEnabled()) {

			logger.info("MMS v2 -- Inicia el proceso de creacion de la conexion al servidor RabbitMQ");
		}

		boolean flag = true;

		while (flag) {
			
			try {

				if (connection == null) {

					SSLContext sslContext = createSSLContext();

                    logger.info("MMS v2 -- Se han cargado en memoria los certificados TLS 1.3 ");

					ConnectionFactory factory = new ConnectionFactory();
					factory.setVirtualHost(virtualHost);
					factory.setPort(port);
					factory.setUsername(userName);
					factory.setPassword(password);
					factory.setAutomaticRecoveryEnabled(true);
					factory.setNetworkRecoveryInterval(10000);
					factory.setTopologyRecoveryEnabled(false);
					factory.useSslProtocol(sslContext);
					factory.enableHostnameVerification();

					List<Address> addressList = new ArrayList<>(Arrays.asList(
							new Address(nodo1, port),
							new Address(nodo2, port),
							new Address(nodo3, port)
					));

					// Shuffle the list to randomize the order
					Collections.shuffle(addressList);

					// Convert the shuffled list back to an Address array
					Address[] addresses = addressList.toArray(new Address[0]);

					connection = factory.newConnection(addresses, connectionName);

					if (logger.isInfoEnabled()) {

						logger.info("MMS v2 -- Se creo exitosamente la conexion al servidor RabbitMQ");
					}

					flag = false;

				} else {

					logger.info("MMS v2 -- (Flag) True en RabbitConexion pero (connection) diferente de NULL");

					connection = null;

				}

			} catch (ShutdownSignalException e) {

				Thread.sleep(5000);

				logger.error("MMS v2 -- Error ShutdownSignalException", e);

			} catch (IOException e) {

				Thread.sleep(5000);

				logger.error("MMS v2 -- Error IOException", e);

			} catch (TimeoutException e) {

				Thread.sleep(5000);

				logger.error("MMS v2 -- Error TimeoutException", e);

			}
		}
	}

	private void obtenerKeystore(){
		try {

			LinkedHashMap configCertificado = FachadaConf.cargarConfEntidadDeGeneral("ConfigFiles/GeneralConfig.xml","RabbitMQCertificate");

			if (isNotEmpty(KeyStoreRabbit.getAlgoritmo())
					&& isNotEmpty(KeyStoreRabbit.getTipo())
					&& isNotEmpty(KeyStoreRabbit.getClave())) {

				RabbitConexion.algorithm = KeyStoreRabbit.getAlgoritmo();
				RabbitConexion.keyStorePassword = KeyStoreRabbit.getClave();
				RabbitConexion.keyStoreType = KeyStoreRabbit.getTipo();
				logger.info("MMS v2 -- Se setearon los valores de configuracion de keystore RMQ con la clase KeyStoreRabbit que ya tiene los valores almacenados.");

			} else {
				String algorithm = configCertificado.get("algoritmo") != null ? configCertificado.get("algoritmo").toString() : "";
				String keyStoreType = configCertificado.get("tipo") != null ? configCertificado.get("tipo").toString() : "";
				String keyStorePassword = configCertificado.get("clave") != null ? configCertificado.get("clave").toString() : "";

				if(algorithm.isEmpty() || keyStoreType.isEmpty() || keyStorePassword.isEmpty()){

					LinkedHashMap cred = FachadaConf.cargarConfEntidadDeGeneral("ConfigFiles/GeneralConfig.xml", "GestorCredenciales");

					String url = cred.get("url") != null ? cred.get("url").toString() : "";
					String jwt = cred.get("jwt") != null ? cred.get("jwt").toString() : "";
					String keyStore = cred.get("gecre.keystore.rmq") != null ? cred.get("gecre.keystore.rmq").toString() : "";

					CredencialesKeyStore keyStoreCredenciales = obtenerKeyStoreDesdeREST(url,jwt,keyStore);
					KeyStoreRabbit.setId(keyStoreCredenciales.getId());
					KeyStoreRabbit.setAlgoritmo(keyStoreCredenciales.getAlgoritmo());
					KeyStoreRabbit.setClave(keyStoreCredenciales.getContrasena());
					KeyStoreRabbit.setTipo(keyStoreCredenciales.getTipo());
					KeyStoreRabbit.setAlias(keyStoreCredenciales.getAlias());
					KeyStoreRabbit.setProveedor(keyStoreCredenciales.getProveedor());

					RabbitConexion.algorithm = keyStoreCredenciales.getAlgoritmo();
					RabbitConexion.keyStorePassword = keyStoreCredenciales.getContrasena();
					RabbitConexion.keyStoreType = keyStoreCredenciales.getTipo();
					logger.info("MMS v2 -- Se setearon los valores de configuracion de keystore RMQ usando el gestor de credenciales.");

				} else {
					RabbitConexion.algorithm = algorithm;
					RabbitConexion.keyStorePassword = keyStorePassword;
					RabbitConexion.keyStoreType = keyStoreType;
					logger.info("MMS v2 -- Se setearon los valores de configuracion usando RabbitMQCertificate.");
				}
			}
		} catch (EntityConfigurationLoadException e) {

			logger.error("MMS v2 -- Error Cargando la configuracion de RabbitMQCertificate de RabbitMQ", e);

		}
	}

	private static SSLContext createSSLContext() throws Exception {

		char[] keyPassphrase = keyStorePassword.toCharArray();

		KeyStore ks = KeyStore.getInstance("PKCS12");

		try (InputStream is = Files.newInputStream(Paths.get("keys/client_certificate.p12"))) {
			ks.load(is, keyPassphrase);
		}catch (IOException e) {
			logger.error("MMS v2 -- Error al cargar el archivo keys/client_certificate.p12: " + e.getMessage());
			System.err.println("MMS v2 -- Error al cargar el archivo keys/client_certificate.p12: " + e.getMessage());
			System.exit(1);
		} catch (Exception e) {
			logger.error("MMS v2 -- Error inesperado keys/client_certificate.p12: " + e.getMessage());
			System.err.println("MMS v2 -- Error inesperado keys/client_certificate.p12: " + e.getMessage());
			System.exit(1);
		}

		KeyManagerFactory kmf = KeyManagerFactory.getInstance(algorithm);
		kmf.init(ks, keyPassphrase);

		char[] trustPassphrase = keyStorePassword.toCharArray();
		KeyStore tks = KeyStore.getInstance(keyStoreType);

		try (InputStream is = Files.newInputStream(Paths.get("keys/rabbitstore"))) {
			tks.load(is, trustPassphrase);
		}catch (IOException e) {
			logger.error("MMS v2 -- Error al cargar el archivo keys/rabbitstore: " + e.getMessage());
			System.err.println("MMS v2 -- Error al cargar el archivo keys/rabbitstore: " + e.getMessage());
			System.exit(1);
		} catch (Exception e) {
			logger.error("MMS v2 -- Error inesperado keys/rabbitstore: " + e.getMessage());
			System.err.println("MMS v2 -- Error inesperado keys/rabbitstore: " + e.getMessage());
			System.exit(1);
		}

		TrustManagerFactory tmf = TrustManagerFactory.getInstance(algorithm);
		tmf.init(tks);

		SSLContext c = SSLContext.getInstance("TLSv1.3");
		c.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

		return c;
	}

	private boolean isNotEmpty(String str) {
		return str != null && !str.trim().isEmpty();
	}

	public CredencialesBaseDeDatos obtenerCredencialesDesdeREST(String urlBase, String jwt, String idToken) {
		try {
			ClienteGCImplREST cliente = new ClienteGCImplREST(urlBase, 60000, jwt);
			return cliente.obtenerCredencialBaseDatos(idToken);
		} catch (Exception e) {
			logger.error("MMS v2 -- Error obteniendo las credeniales de RabbitMQ del Gestor Credenciales ",e);
			return null;
		}
	}

	public CredencialesKeyStore obtenerKeyStoreDesdeREST(String urlBase, String jwt, String idToken) {
		try {
			ClienteGCImplREST cliente = new ClienteGCImplREST(urlBase, 60000, jwt);
			return cliente.obtenerCredencialKeyStore(idToken);
		} catch (Exception e) {
			logger.error("MMS v2 -- Error obteniendo el keyStore de RabbitMQ del Gestor Credenciales ",e);
			return null;
		}
	}

	public static Logger getLogger() {
		return logger;
	}

	public static void setLogger(Logger logger) {
		RabbitConexion.logger = logger;
	}

	public static Connection getConnection() {
		return connection;
	}

	public static void setConnection(Connection connection) {
		RabbitConexion.connection = connection;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}


	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}


	public static void setEstablishConnection(String establishConnection) {
		RabbitConexion.establishConnection = establishConnection;
	}
	
	
}
