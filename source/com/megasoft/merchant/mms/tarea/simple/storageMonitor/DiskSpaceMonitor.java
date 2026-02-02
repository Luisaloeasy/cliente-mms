package com.megasoft.merchant.mms.tarea.simple.storageMonitor;

import com.megasoft.merchant.mms.configuracion.FachadaConf;
import com.megasoft.merchant.mms.configuracion.exception.EntityConfigurationLoadException;
import com.megasoft.merchant.mms.tarea.simple.envio.Envio;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Clase que revisa el espacio en disco del Servidor.
 *
 * @author Luis Alejandro Aloisi Millan
 *
 */
public class DiskSpaceMonitor extends Envio {
    private static final double MB = 1024.0 * 1024.0;
    private static final String CONFIG_FILE_PATH = "ConfigFiles/GeneralConfig.xml";
    private static final String STORAGE_CONFIG_KEY = "Storage";
    private static final String MIN_REQ_STORAGE_MB = "MinReqStorageMB";
    private static final double DEFAULT_MINIMUM_SPACE_MB = 500.0;

    private double requiredStorageMB;
    private double availableSpaceMB;
    private static final Logger logger = Logger.getLogger(DiskSpaceMonitor.class.getName());

    public DiskSpaceMonitor() {
        super();
    }

    public void run(Map parameters) throws Exception {

        long startExecutionTime = System.nanoTime();

        try {

            logger.info("MMS v2 -- Inicio Tarea DiskSpaceMonitor");

            super.run(parameters);

            logger.debug("MMS v2 -- Parametros cargados: Periodo (milisegundos) = " + periodo);

            checkDiskSpace();

        } catch (Exception e) {
            logger.error("MMS v2 -- Error no identificado '" + e.getMessage() + "'", e);
        } finally {
            long endExecutionTime = System.nanoTime();
            double runtime = ((endExecutionTime - startExecutionTime) / 1e6);
            logger.info("MMS v2 -- La tarea DiskSpaceMonitor se ejecutó en: " +runtime+ " ms");
            logger.info("MMS v2 -- Termino Tarea DiskSpaceMonitor");
        }
    }

    private void checkDiskSpace() throws Exception {
        String currentDir = System.getProperty("user.dir");
        availableSpaceMB = getDiskSpace(currentDir);
        requiredStorageMB = getRequiredSpace();

        if (availableSpaceMB < 0) {
            logger.error("MMS v2 -- Error al obtener el espacio disponible en el disco");
            return;
        }

        if (availableSpaceMB > requiredStorageMB) {
            logger.info(String.format("MMS v2 -- Hay suficiente espacio en disco para el funcionamiento de las tareas, espacio: %.2f MB", availableSpaceMB));
        } else {
            handleInsufficientSpace(currentDir, availableSpaceMB);
        }
    }

    private double getDiskSpace(String path) {
        try {
            double space = new File(path).getUsableSpace() / MB;
            return BigDecimal.valueOf(space).setScale(2, RoundingMode.HALF_UP).doubleValue();
        } catch (Exception e) {
            logger.error("MMS v2 -- Error al obtener el espacio en disco: " + e.getMessage(), e);
            return -1;
        }
    }

    private double getRequiredSpace() {
        try {
            LinkedHashMap storageConfig = FachadaConf.cargarConfEntidadDeGeneral(CONFIG_FILE_PATH, STORAGE_CONFIG_KEY);
            return Integer.parseInt(storageConfig.get(MIN_REQ_STORAGE_MB).toString());
        } catch (NumberFormatException e) {
            logger.error("MMS v2 -- Error al formatear el minReqStorageMB", e);
        } catch (EntityConfigurationLoadException e) {
            logger.error("MMS v2 -- Error al cargar la configuración de almacenamiento", e);
        } catch (Exception e) {
            logger.error("MMS v2 -- Error al cargar la configuración del almacenamiento", e);
        }
        // Si hay algún error al obtener la configuración, se usa el valor predeterminado
        return DEFAULT_MINIMUM_SPACE_MB;
    }

    private String formatNumber(double number) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator('.');
        String pattern = (number == Math.floor(number)) ? "#,##0" : "#,##0.00";
        DecimalFormat df = new DecimalFormat(pattern, symbols);

        return df.format(number);
    }
    private void handleInsufficientSpace(String currentDir, double size) throws Exception {
        String requiredStorageMBFormatted = formatNumber(requiredStorageMB);
        String availableSpaceMBFormatted = formatNumber(availableSpaceMB);
        logger.info("MMS v2 -- NO Hay suficiente espacio en disco para el funcionamiento de las tareas, espacio: " + size + " MB");
        logger.error("MMS v2 -- Urgente: Espacio insuficiente en disco - Cliente MMS");
        tareaUtil.enviarStorageMail("Urgente: Espacio insuficiente en disco",
                "El ClienteMMS no puede funcionar debido a la falta de espacio en disco. La capacidad de almacenamiento necesaria es de " + requiredStorageMBFormatted+ " MB, mientras que actualmente solo hay "+ availableSpaceMBFormatted+" MB disponibles en el disco. Por favor, solucionen este problema de manera urgente.");
        try {
            logger.info("MMS v2 -- Se llevará a cabo la interrupción del servicio debido a la insuficiencia de espacio en disco.");
            Process p = Runtime.getRuntime().exec("cmd /c start " + currentDir + "\\Stop.bat");
            p.waitFor();
        } catch (IOException ex) {
            logger.error("MMS v2 -- Error Permisos Insuficientes");
            throw ex;
        } catch (InterruptedException ex) {
            logger.error("MMS v2 -- Error Proceso Interrumpido");
            throw ex;
        }
    }

}
