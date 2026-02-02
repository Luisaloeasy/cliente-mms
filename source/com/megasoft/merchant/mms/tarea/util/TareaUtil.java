package com.megasoft.merchant.mms.tarea.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.megasoft.merchant.mms.configuracion.FachadaConf;
import com.megasoft.merchant.mms.configuracion.exception.EntityConfigurationLoadException;
import com.megasoft.merchant.mms.configuracion.exception.EntityConfigurationSaveException;
import com.megasoft.merchant.mms.configuracion.exception.FileNotFoundMSConfException;
import com.megasoft.merchant.mms.tarea.exception.ConfigurationErrorTareaException;
import com.megasoft.merchant.mms.tarea.exception.InvalidDataTareaException;
import com.megasoft.merchant.mms.transporte.exception.BadDataMSEmailException;
import com.megasoft.merchant.mms.transporte.exception.EmailLibraryMSEmailException;
import com.megasoft.merchant.mms.transporte.exception.SendFailedMSEmailException;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import ve.com.megasoft.clientegc.logica.ClienteGCImplREST;
import ve.com.megasoft.clientegc.modelo.CredencialesBaseDeDatos;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

/**
 * @author Ronald Perestrelo
 */

public class TareaUtil {

    private static Logger logger = Logger.getLogger(TareaUtil.class.getName());

    /**
     * Number of milliseconds in a second
     */
    public final static long SECOND = 1000L;
    /**
     * Number of milliseconds in a minute
     */
    public final static long MINUTE = 60 * SECOND;
    /**
     * Number of milliseconds in a hour
     */
    public final static long HOUR = 60 * MINUTE;
    /**
     * Number of milliseconds in a day
     */
    public final static long DAY = 24 * HOUR;
    /**
     * Map between time unit type and milliseconds
     */
    private final static Map TIME_UNIT_MAP = new HashMap();

    static {
        TIME_UNIT_MAP.put(new Character('s'), new Long(SECOND));
        TIME_UNIT_MAP.put(new Character('m'), new Long(MINUTE));
        TIME_UNIT_MAP.put(new Character('h'), new Long(HOUR));
        TIME_UNIT_MAP.put(new Character('d'), new Long(DAY));
    }

    /**
     * Parses a <code>String</code> represntation of time period or delay,
     * in milliseconds.
     * If the time only contains digits, it is simply converted to a long.
     * However, the value can be made of one or several numbers followed with
     * an alpha character representing a time unit, all merged together.  The
     * supported time units are:
     * <ul>
     *   <li><strong>d</strong>: a day
     *   <li><strong>h</strong>: an hour
     *   <li><strong>m</strong>: a minute
     *   <li><strong>s</strong>: a second
     * </ul>
     * No time unit for a number always means milliseconds.  Some examples are:
     * <blockquote>
     *   <strong>1d</strong>: 1 day (or 86,400,000 milliseconds)<br>
     *   <strong>2h30m</strong>: 2 hours and 30 minutes<br>
     *   <strong>30s500</strong>: 30 seconds and 500 milliseconds<br>
     * </blockquote>
     *
     * @param time period or delay to parse
     * @return a <code>long</code> representation of a delay or period in SECONDS
     * @throws InvalidDataTareaException
     */
    public int parseoTiempo(String time)
            throws InvalidDataTareaException {

        long longTime = 0L;
        StringBuffer number = new StringBuffer("0");
        for (int i = 0; i < time.length(); i++) {
            char ch = time.charAt(i);
            if (Character.isDigit(ch)) {
                number.append(ch);
            } else {
                Character unitType = new Character(ch);
                if (TIME_UNIT_MAP.containsKey(unitType)) {
                    longTime += Long.parseLong(number.toString())
                            * ((Long) TIME_UNIT_MAP.get(unitType)).longValue();
                    number = new StringBuffer("0");
                } else {
                    throw new InvalidDataTareaException(
                            "El dato de tiempo '" + ch + "' no es valido.");
                }
            }
        }
        // Add remaining milliseconds, if any
        longTime += Long.parseLong(number.toString());

        // Se pasa a Segundos
        longTime = longTime / 1000;
        return new Long(longTime).intValue();
    }

    /**
     * Se ingresa al archivo de control de actividad y obtiene la fecha especificada en el
     *
     * @param nombArchivo
     * @return
     * @throws ConfigurationErrorTareaException
     */
    public Timestamp obtenerUltimaVezRealizada(
            SimpleDateFormat formato, String nombArchivo)
            throws ConfigurationErrorTareaException {

        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Inicia el procedimiento para obtener la Ultima Vez Realizada " +
                        "Parametros. formato :" + formato.toPattern() + " nombArchivo :" + nombArchivo);
            }

            LinkedHashMap config = FachadaConf.cargarEntidad("ControlFiles", nombArchivo);

            if (logger.isDebugEnabled()) {
                logger.debug("Se cargo Ultima realizacion del Archivo = " + config.get("UltimaRealizacion").toString());
            }

            Date date = formato.parse(config.get("UltimaRealizacion").toString());

            Timestamp fecha = new Timestamp(date.getTime());

            if (logger.isDebugEnabled()) {
                logger.debug("TimeStamp de Salida  = " + fecha);
            }

            return fecha;

        } catch (EntityConfigurationLoadException e) {
            logger.error("No se pudo cargar el Archivo de Control  " +
                    "de Actividad '" + nombArchivo + "'", e);
            throw new ConfigurationErrorTareaException("No se pudo cargar el Archivo de " +
                    "Control de Actividad '" + nombArchivo + "'", e);

        } catch (ParseException e) {
            logger.error("Mal formato para la fecha en el Archivo de Control " +
                    "de Actividad '" + nombArchivo + "'", e);
            throw new ConfigurationErrorTareaException("Mal formato para la fecha en el Archivo " +
                    "de Control de Actividad '" + nombArchivo + "'", e);
        }
    }

    //TODO revisar que se use el false y el true
    public synchronized boolean guardarUltimaVezRealizada(SimpleDateFormat formato,
                                                          String nombArchivo, String nombEntidad, Timestamp fecha) {

        try {

            if (logger.isDebugEnabled()) {
                logger.debug("Inicia el procedimiento para guardar ultima vez que se realizo la Tarea " +
                        "Parametros. formato :" + formato.toPattern() + " nombArchivo :" + nombArchivo +
                        " nombEntidad " + nombEntidad + " Fecha " + fecha);
            }

            LinkedHashMap config = FachadaConf.cargarEntidad("ControlFiles", nombArchivo);
            config.put("UltimaRealizacion", formato.format(new Date(fecha.getTime())));

            if (logger.isDebugEnabled()) {
                logger.debug("Se guardara UltimaRealizacion = " + config.get("UltimaRealizacion").toString());
            }

            LinkedHashMap archivo = new LinkedHashMap();
            archivo.put(nombEntidad, config);

            FachadaConf.guardarConfGeneral(archivo, "ControlFiles", nombArchivo);

            if (logger.isDebugEnabled()) {
                logger.debug("Termino el procedimiento para guardar ultima vez que se realizo la Tarea");
            }
            return true;

        } catch (EntityConfigurationLoadException e) {
            logger.error("No se pudo cargar la configuracion de la Entidad " + nombEntidad, e);
            if (logger.isDebugEnabled()) {
                logger.debug("NombreEntidad:" + nombEntidad +
                        " NombreArchivo:" + nombArchivo + " Fecha:" + fecha);
            }
            return false;
        } catch (EntityConfigurationSaveException e) {
            logger.error("No se pudo guardar la configuracion de la Entidad " + nombEntidad, e);
            if (logger.isDebugEnabled()) {
                logger.debug("NombreEntidad:" + nombEntidad +
                        " NombreArchivo:" + nombArchivo + " Fecha:" + fecha);
            }
            return false;
        } catch (FileNotFoundMSConfException e) {
            logger.error("No se pudo cargar la configuracion de la Entidad " + nombEntidad, e);
            if (logger.isDebugEnabled()) {
                logger.debug("NombreEntidad:" + nombEntidad +
                        " NombreArchivo:" + nombArchivo + " Fecha:" + fecha);
            }
            return false;
        }
    }

    public java.sql.Timestamp obtenerFechaFin(
            SimpleDateFormat formato, boolean recuperarPorPeriodo,
            Timestamp fechaInicio, int period) {

        if (logger.isDebugEnabled()) {
            logger.debug("Inicia el procedimiento para calcular Fecha Fin" +
                    " Parametros. FechaInicio " + fechaInicio + " period " + period +
                    " recuperarPorPeriodo " + recuperarPorPeriodo + " formato " + formato.toPattern());
        }

        Timestamp fechaFin;
        if (!recuperarPorPeriodo) {
            fechaFin = horaActual(formato);
        } else {
            Calendar calendar = new GregorianCalendar();
            calendar.setTimeInMillis(fechaInicio.getTime());
            calendar.add(Calendar.SECOND, period);
            fechaFin = new java.sql.Timestamp(calendar.getTimeInMillis());
        }

        if (logger.isDebugEnabled()) {
            logger.debug("La hora fin calculada es " + fechaFin);
        }
        return fechaFin;
    }

    //TODO que lanza la excepcion de parseo para arriba
    public Timestamp horaActual(SimpleDateFormat formato) {

        if (logger.isInfoEnabled()) {
            logger.info("Iniciado el procedimiento para determinar hora actual");
        }

        Date date = new Date();
        try {
            date = formato.parse(formato.format(date));
        } catch (ParseException e) {
            logger.error("Error en el parseo de la Fecha Actual");
        }
        Timestamp hora = new java.sql.Timestamp(date.getTime());

        if (logger.isDebugEnabled()) {
            logger.debug("Hora actual obtenida " + hora);
        }

        return hora;
    }

    public Vector obtenerListaSeparadaPorPuntoComa(String listaEmails) {
        Pattern splitter = Pattern.compile("\\;");
        String[] words = splitter.split(listaEmails);
        words[0] = words[0].substring(1, words[0].length());
        words[words.length - 1] = words[words.length - 1].substring(0, words[words.length - 1].length() - 1);

        Vector emails = new Vector();
        for (int i = 0; i < words.length; i++) {
            String email = words[i];
            if (logger.isDebugEnabled()) {
                logger.debug("Email Obtenido del Conjunto: " + email);
            }
            emails.add(email);
        }
        return emails;
    }

    /**
     * replace string
     *
     * @param string: string that contains the text to be replaced
     * @param from:   text to replace
     * @param to:     replacement
     **/
    public static String replace(String string, String from, String to) {
        if (from.equals(""))
            return string;
        StringBuffer buf = new StringBuffer(2 * string.length());

        int previndex = 0;
        int index = 0;
        int flen = from.length();
        while (true) {
            index = string.indexOf(from, previndex);
            if (index == -1) {
                buf.append(string.substring(previndex));
                break;
            }
            buf.append(string.substring(previndex, index) + to);
            previndex = index + flen;
        }
        return buf.toString();
    }

    /**
     * Recibe un vector y le calcula el checksum.
     *
     * @param jsonArray
     * @return Checksum
     * @throws Exception
     * @author Luis Alejandro Aloisi Millan
     */

    public synchronized String calcularCheckSum(ArrayList<JSONObject> jsonArray) throws Exception {

        try {
            StringBuilder builder = new StringBuilder();
            for (JSONObject object : jsonArray) {
                builder.append(object.toString());
            }
            byte[] bytesJsonArrayString = builder.toString().getBytes();


            Checksum checksum = new CRC32();
            checksum.reset();
            checksum.update(bytesJsonArrayString, 0, bytesJsonArrayString.length);

            return String.valueOf(checksum.getValue());

        } catch (Exception e) {

            logger.error("MMS v2 -- Error calculando checksum ", e);

            return "0";
        }

    }

    public synchronized void resultSetToFileJSON(String resulset,String entidad) {
        FileWriter fileWriter = null;
        try{

            String currentDir = System.getProperty("user.dir");
            File path = new File(currentDir+"/ControlFiles/JSONTemp/" + entidad +".json");

            fileWriter = new FileWriter(path);
            fileWriter.write(resulset);

            logger.info("MMS v2 -- Se ha convertido exitosamente el ResulSet a JSONTemp " + entidad);

        }catch (Exception e){
            logger.error("MMS v2 -- Ocurrio un error convirtiendo el ResulSet a JSONTemp: " + entidad,e);

        }finally {
            try {
                if (fileWriter != null) {
                    fileWriter.flush();
                    fileWriter.close();
                }
                logger.debug("MMS v2 -- Se ha liberado correctamente el archivo " + entidad);
            } catch (IOException e) {
                logger.error("MMS v2 -- Ocurrio un error liberando el archivo y cerrandolo " + entidad,e);
            }

        }
    }

    public synchronized boolean compareJsonToResulSet(String entidad) throws Exception {

        try {

            String currentDir = System.getProperty("user.dir");
            File pathJson = new File(currentDir+"/ControlFiles/JSONTemp/" + entidad + ".json");
            File pathJsonTemp = new File(currentDir+"/ControlFiles/JSONTemp/" + entidad + "Temp.json");

            boolean flag = false;

            if (pathJson.exists() && pathJsonTemp.exists() && pathJsonTemp.length()>0 && pathJson.length()>0) {

                ObjectMapper mapperJson = new ObjectMapper();
                ObjectMapper mapperJsonTemp = new ObjectMapper();

                String j1 = new String(Files.readAllBytes(Paths.get(pathJson.toURI())), StandardCharsets.UTF_8);
                String j2 = new String(Files.readAllBytes(Paths.get(pathJsonTemp.toURI())), StandardCharsets.UTF_8);

                JsonNode jsonTemp = mapperJson.readTree(j1);
                JsonNode json = mapperJsonTemp.readTree(j2);

                ObjectMapper mapper = new ObjectMapper();

                flag = assertEquals(mapper.readTree(json.traverse()), mapper.readTree(jsonTemp.traverse()));

                if (flag){
                    logger.info("MMS v2 -- La extraccion actual de datos de "+entidad+ " es igual a la extraccion anterior");
                }else{
                    logger.info("MMS v2 -- La extraccion actual de datos de "+entidad+ " es diferente a la extraccion anterior");
                }
            }else{

                boolean writeNewFile = pathJson.createNewFile();
                logger.info("MMS v2 -- Se ha creado el archivo vacio " +pathJson + " correctamente --> " + writeNewFile);
                if(!pathJson.canRead() || !pathJson.canWrite()){
                    boolean a = pathJson.setReadable(true);
                    boolean b = pathJson.setWritable(true);
                    logger.debug("MMS v2 -- Se seteo el permiso de lectura a "+pathJson +" " + a );
                    logger.debug("MMS v2 -- Se seteo el permiso de escritura a "+pathJson +" " + b );
                }
            }
            return flag;

        } catch (Exception e) {
            logger.error("MMS v2 -- Ocurrio un error comparando el JSON con el Resulset de la tabla: " + entidad,e);

            return false;
        }

    }

    private synchronized boolean assertEquals(JsonNode jsonTemp, JsonNode json) throws Exception {
        return jsonTemp.equals(json);
    }

    public synchronized void renameJsons(String entidad, boolean flag) throws Exception {

        String currentDir = System.getProperty("user.dir");
        File pathEntidad = new File(currentDir + "/ControlFiles/JSONTemp/" + entidad);
        try {
            if (!flag) {
                File oldFile = new File(pathEntidad+ ".json");
                boolean deleteF = oldFile.delete();
                if (deleteF){
                    logger.info("MMS v2 -- Se ha eliminado exitosamente el archivo " +oldFile);
                }
                File newFile = new File(pathEntidad+ "Temp" + ".json");
                File rename = new File(pathEntidad+ ".json");
                boolean success = newFile.renameTo(rename);
                if (success){
                    logger.info("MMS v2 -- Se ha renombrado exitosamente el archivo " +newFile + " a " + rename);
                }
            }else{
                File oldFile = new File(pathEntidad+ "Temp.json");
                boolean deleteF = oldFile.delete();
                if (deleteF){
                    logger.info("MMS v2 -- Se ha eliminado exitosamente el archivo " +oldFile);
                }
            }

        } catch (Exception e) {
            logger.error("MMS v2 -- Ocurrio un error Renombrando o eliminando el archivo: " + entidad,e);
        }
    }

    public synchronized void enviarStorageMail(String subject, String text) throws SendFailedMSEmailException,
            BadDataMSEmailException, EmailLibraryMSEmailException, EntityConfigurationLoadException {

        try {

            if (logger.isDebugEnabled()) {
                logger.debug("MMS v2 -- Se Inicio el proceso para enviar un correo ");
            }

            LinkedHashMap confCliente = FachadaConf.cargarConfEntidadDeGeneral("ConfigFiles/GeneralConfig.xml",
                    "ConfCliente");

            String idCliente = confCliente.get("idCliente").toString();

            String nombreCliente = confCliente.get("nombreCliente").toString();

            LinkedHashMap storageConfig = FachadaConf.cargarConfEntidadDeGeneral("ConfigFiles/GeneralConfig.xml",
                    "Storage");

            String host = storageConfig.get("Host").toString();

            String port = storageConfig.get("Port").toString();

            String recipient = storageConfig.get("Para").toString();

            Vector destinatarios = obtenerListaSeparadaPorPuntoComa(recipient);

            String sender = storageConfig.get("De").toString();

            String allsub = subject + " Cliente MMS " + nombreCliente + " " + idCliente;

            if (logger.isDebugEnabled()) {
                logger.debug("MMS v2 -- Se enviara el correo a los  " + "destinatarios:" + recipient
                        + " De:" + sender + " Asunto:" + allsub);
            }

            Properties props = new Properties();
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", port);
            Session session = Session.getInstance(props);
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(sender));
            msg.setSubject(allsub);
            msg.setSentDate(new Date());
            msg.setText(text);

            InternetAddress[] address = new InternetAddress[destinatarios.size()];
            for (int i = 0; i < destinatarios.size(); i++) {
                address[i] = new InternetAddress((String) destinatarios.get(i));
            }
            msg.setRecipients(Message.RecipientType.TO, address);

            Transport.send(msg);

            logger.info("MMS v2 -- Se envio el correo exitosamente a: " + destinatarios
                    + " De: " + sender + " Asunto:" + allsub);

        } catch (AddressException e) {
            throw new BadDataMSEmailException("La direccion de envio es invalida", e);
        } catch (EntityConfigurationLoadException e) {
            throw new EntityConfigurationLoadException("Se cargaron mal los parametros de la configuracion", e);
        } catch (MessagingException e) {
            if (e instanceof SendFailedException)
                throw new SendFailedMSEmailException("El envio del mail no se pudo realizar", e);
            else {
                throw new EmailLibraryMSEmailException("No se puede cambiar el atributo", e);

            }

        }
    }

    public static synchronized List<String> readNamesFromJSONFile(String entity) throws IOException {

        String currentDir = System.getProperty("user.dir");
        Path filePath = Paths.get(currentDir, "ControlFiles", "ColumnsIgnored", "JSON", entity + ".json");
        List<String> columnNames = new ArrayList<>();

        try {
            if (!Files.exists(filePath)) {
                logger.debug("MMS v2 -- No existe el archivo " + filePath + " en readNamesFromJSONFile." +
                        " No hay columnas que ignorar para la comparacion de tablas, de la entidad " +entity);
                return columnNames;
            }

            String fileContent = new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8);
            JSONObject jsonObject = new JSONObject(fileContent);
            JSONArray columnsArray = jsonObject.getJSONArray("columns");

            for (int i = 0; i < columnsArray.length(); i++) {
                columnNames.add(columnsArray.getString(i).toLowerCase());
            }

        } catch (IOException | RuntimeException e) {
            logger.error("MMS v2 -- readNamesFromJSONFile Error processing file: " + filePath, e);
        }
        return columnNames;
    }

    private boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }

    public synchronized CredencialesBaseDeDatos obtenerCredencialesDesdeREST(String urlBase, String jwt, String idToken) {
        try {
            ClienteGCImplREST cliente = new ClienteGCImplREST(urlBase, 60000, jwt);
            return cliente.obtenerCredencialBaseDatos(idToken);
        } catch (Exception e) {
            logger.error("MMS v2 -- Error obteniendo las credeniales de BD del Gestor Credenciales ", e);
            return null;
        }
    }

    public static synchronized List<String> readNamesFromQueryColumnsIgnoredJSONFile(String entity){

        String currentDir = System.getProperty("user.dir");
        Path filePath = Paths.get(currentDir, "ControlFiles", "QueryColumnsIgnored", entity + ".json");
        List<String> columnNames = new ArrayList<>();

        try {
            if (!Files.exists(filePath)) {
                logger.debug("MMS v2 -- No existe el archivo " + filePath + " en readNamesFromQueryColumnsIgnoredJSONFile." +
                        " No hay columnas que ignorar para la extraccion, de la entidad " +entity);
                return columnNames;
            }

            String fileContent = new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8);
            JSONObject jsonObject = new JSONObject(fileContent);
            JSONArray columnsArray = jsonObject.getJSONArray("columns");

            for (int i = 0; i < columnsArray.length(); i++) {
                columnNames.add(columnsArray.getString(i).toLowerCase());
            }

        } catch (IOException | RuntimeException e) {
            logger.error("MMS v2 -- readNamesFromQueryColumnsIgnoredJSONFile Error processing file: " + filePath, e);
        }
        return columnNames;
    }


}
