package com.megasoft.merchant.mms.persistence;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import org.apache.log4j.Logger;
import ve.com.megasoft.clientegc.logica.ClienteGCImplREST;
import ve.com.megasoft.clientegc.modelo.CredencialesBaseDeDatos;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Configuración de HikariCP con dos DataSources:
 * - `historyDataSource`: Para conexiones sin base de datos específica.
 * - `merchantDataSource`: Para conexiones a una base de datos específica.
 * Soporte para autenticación segura con GECRE.
 *
 * @author Luis Aloisi
 */
public class DataSourceConfig {

    private static final Logger logger = Logger.getLogger(DataSourceConfig.class);
    private static final String DATASOURCE_PROPERTIES_FILE = "ConfigFiles/datasource.properties";
    private static HikariDataSource historyDataSource;
    private static HikariDataSource merchantDataSource;
    private static final boolean SECURE;

    static {
        try {
            Properties properties = loadProperties();
            SECURE = Boolean.parseBoolean(properties.getProperty("secure", "false"));

            if (SECURE) {
                logger.info("Se utilizará el GECRE para obtener las credenciales de base de datos.");
                configureSecureDataSources(properties);
            } else {
                configureDataSources(properties);
            }

            if (historyDataSource == null) {
                throw new IllegalStateException("No se pudo inicializar el DataSource History.");
            }

            if (merchantDataSource == null) {
                throw new IllegalStateException("No se pudo inicializar el DataSource Merchant.");
            }
        }catch (Exception e) {
            logger.fatal("Error crítico al inicializar los DataSources. La aplicación no puede continuar.", e);
            throw new RuntimeException("Error al iniciar los DataSources.", e);
        }
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream(DATASOURCE_PROPERTIES_FILE)) {
            properties.load(input);
        } catch (IOException e) {
            logger.error("Error al cargar `datasource.properties`", e);
        }
        return properties;
    }

    private static void validateProperty(String value, String propertyName) {
        if (value == null || value.trim().isEmpty()) {
            logger.error("La propiedad `" + propertyName + "` no está configurada.");
            throw new IllegalArgumentException("⚠ La propiedad `" + propertyName + "` no está configurada.");
        }
    }

    private static String getDriverClass(String dbType) {
        return "postgresql".equalsIgnoreCase(dbType) ? "org.postgresql.Driver" : "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    }

    private static void configureDataSources(Properties properties) {
        historyDataSource = createDataSource(properties, "jdbc.history.");
        merchantDataSource = createDataSource(properties, "jdbc.merchant.");
    }

    private static HikariDataSource createDataSource(Properties properties, String prefix) {
        String jdbcUrl = properties.getProperty(prefix + "url");
        String username = properties.getProperty(prefix + "username");
        String password = properties.getProperty(prefix + "password");

        validateProperty(jdbcUrl, prefix + "url");

        if (!SECURE){
            validateProperty(username, prefix + "username");
            validateProperty(password, prefix + "password");
        }

        return createHikariDataSource(properties, prefix, jdbcUrl, username, password);
    }

    private static HikariDataSource createDataSource(Properties properties, String prefix, CredencialesBaseDeDatos credentials) {
        String jdbcUrl = properties.getProperty(prefix + "url");
        validateProperty(jdbcUrl, prefix + "url");

        return createHikariDataSource(properties, prefix, jdbcUrl, credentials.getUsuario(), credentials.getContrasena());
    }

    private static HikariDataSource createHikariDataSource(Properties properties, String prefix, String jdbcUrl, String username, String password) {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName(getDriverClass(properties.getProperty(prefix + "type")));

        setHikariPoolConfig(config, properties);
        logger.info("DataSource `" + prefix + "` configurado correctamente.");

        return new HikariDataSource(config);
    }

    public static synchronized CredencialesBaseDeDatos obtenerCredencialesDesdeREST(String urlBase, String jwt, String idToken, Integer timeout) {
        try {
            ClienteGCImplREST cliente = new ClienteGCImplREST(urlBase, timeout, jwt);
            return cliente.obtenerCredencialBaseDatos(idToken);
        } catch (Exception e) {
            logger.error("MMS v2 -- Error obteniendo las credeniales de BD del Gestor Credenciales ", e);
            return null;
        }
    }

    private static void configureSecureDataSources(Properties properties) {
        try {
            String gecreUrl = properties.getProperty("secure.url");
            String gecreToken = properties.getProperty("secure.encrypted");
            int gecreTimeout = Integer.parseInt(properties.getProperty("secure.timeout", "60000"));

            validateProperty(gecreUrl, "secure.url");
            validateProperty(gecreToken, "secure.encrypted");

            String historyDbId = properties.getProperty("jdbc.history.secure.bd.id");
            String merchantDbId = properties.getProperty("jdbc.merchant.secure.bd.id");

            validateProperty(historyDbId, "jdbc.history.secure.bd.id");
            validateProperty(merchantDbId, "jdbc.merchant.secure.bd.id");

            CredencialesBaseDeDatos historyCredentials = obtenerCredencialesDesdeREST(gecreUrl, gecreToken, historyDbId, gecreTimeout);
            CredencialesBaseDeDatos merchantCredentials = obtenerCredencialesDesdeREST(gecreUrl, gecreToken, merchantDbId, gecreTimeout);

            if (historyCredentials != null) {
                historyDataSource = createDataSource(properties, "jdbc.history.", historyCredentials);
                logger.info("`historyDataSource` configurado con GECRE.");
            } else {
                logger.error("No se pudo configurar `historyDataSource`.");
            }

            if (merchantCredentials != null) {
                merchantDataSource = createDataSource(properties, "jdbc.merchant.", merchantCredentials);
                logger.info("`merchantDataSource` configurado con GECRE.");
            } else {
                logger.error("No se pudo configurar `merchantDataSource`.");
            }
        } catch (IllegalArgumentException e) {
            logger.error("Error en la configuración segura de DataSources.", e);
        } catch (Exception e) {
            logger.fatal("Error inesperado al configurar los DataSources con GECRE", e);
        }
    }

    private static void setHikariPoolConfig(HikariConfig config, Properties properties) {
        config.setMaximumPoolSize(Integer.parseInt(properties.getProperty("maximumPoolSize", "100")));
        config.setMinimumIdle(Integer.parseInt(properties.getProperty("minimumIdle", "50")));
        config.setConnectionTimeout(Long.parseLong(properties.getProperty("connectionTimeout", "60000")));
        config.setIdleTimeout(Long.parseLong(properties.getProperty("idleTimeout", "1200000")));
        config.setMaxLifetime(Long.parseLong(properties.getProperty("maxLifetime", "1800000")));
        config.setLeakDetectionThreshold(Long.parseLong(properties.getProperty("leakDetectionThreshold", "600000")));
    }

    public static Connection getHistoryConnection() throws SQLException {
        return getConnection(historyDataSource, "History");
    }

    public static Connection getMerchantConnection() throws SQLException {
        return getConnection(merchantDataSource, "Merchant");
    }

    private static Connection getConnection(HikariDataSource dataSource, String name) throws SQLException {
        if (dataSource == null) {
            throw new SQLException(" `" + name + "` DataSource no está inicializado.");
        }
        getConnectionHealth(dataSource, name);
        return dataSource.getConnection();
    }

    public static void getConnectionHealth(HikariDataSource datasource, String dsName) {
        if (datasource == null) {
            logger.warn("No se puede obtener información de conexiones porque `" + dsName + "` no está inicializado.");
            return;
        }

        HikariPoolMXBean poolMXBean = datasource.getHikariPoolMXBean();

        int activeConnections = poolMXBean.getActiveConnections();
        int idleConnections = poolMXBean.getIdleConnections();
        int totalConnections = poolMXBean.getTotalConnections();
        int threadsAwaitingConnection = poolMXBean.getThreadsAwaitingConnection();

        logger.info(" Estado del pool de `" + dsName + "`: " + "Activas: " + activeConnections + " | " + "Inactivas: " + idleConnections + " | " + "Total: " + totalConnections + " | " + "Hilos en espera: " + threadsAwaitingConnection);
    }

    public static HikariDataSource getDataSource() {
        if (historyDataSource == null) {
            throw new IllegalStateException("El DataSource History no se ha inicializado correctamente.");
        }
        return historyDataSource;
    }

}
