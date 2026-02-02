package com.megasoft.merchant.mms.persistence.queries;

import com.megasoft.merchant.mms.persistence.exception.InvalidDataMSBDException;
import org.apache.log4j.Logger;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Clase padre de los procedimientos SQL de Select. 
 * En esta clase se definen los procedimientos comunes para cada una de las especializaciones (SelectMMS y SelectMS)
 * @author Luis Aloisi
 *
 */
public class Select {

	static private Logger logger = Logger.getLogger(Select.class.getName());

	/**
	 * Query para extraer todas las transacciones financieras cerradas en un intervalo de tiempo
	 * @param fechaInicio
	 * @param fechaFin
	 * @return Todas las transacciones de la tabla History en ese intervalo de tiempo
	 * @throws InvalidDataMSBDException
	 * @author Luis Alejandro Aloisi Millan
	 */
	public static String history(Timestamp fechaInicio, Timestamp fechaFin) throws InvalidDataMSBDException{
		
		if (fechaInicio.after(fechaFin) || fechaInicio == null || fechaFin == null)
			throw new InvalidDataMSBDException("Las fechas ingresadas son nulas o la fecha de inicio es despues de la de fin");
		
		SimpleDateFormat formatoTrx_date = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat formatoTrx_time = new SimpleDateFormat("HH:mm:ss");
		String fDiaInicio = formatoTrx_date.format(new Date(fechaInicio.getTime()));
		String fHoraInicio = formatoTrx_time.format(new Date(fechaInicio.getTime()));
		String fHoraFin = formatoTrx_time.format(new Date(fechaFin.getTime()));
		
		String query = "SELECT * FROM History "
				+ "WHERE SettlementDate = '"+fDiaInicio+"'"
				+ "AND   SettlementHour between '"+fHoraInicio+"' and '"+fHoraFin+"'";
		
		if (logger.isDebugEnabled()){
			logger.debug("Query generado "+query);
		}
		return query;		
	}

	/**
	 * Query para extraer todos los Retailers
	 * @return Todas los Retailers
	 * @author Luis Alejandro Aloisi Millan
	 */
	public static String retailerMQ() {
			
		String query = "SELECT * FROM Retailers";
		
		if (logger.isDebugEnabled()){
			logger.debug("Query generado "+query);
		}
		return query;		
	}
	
	/**
	 * Query para extraer todos los Merchants
	 * @return Todas los Merchants
	 * @author Luis Alejandro Aloisi Millan
	 */
	public static String merchantsMQ() {
			
		String query = "SELECT * FROM Merchants";
		
		if (logger.isDebugEnabled()){
			logger.debug("Query generado "+query);
		}
		return query;		
	}
	
	/**
	 * Query para extraer todos los Banks
	 * @return Todos los Banks
	 * @author Luis Alejandro Aloisi Millan
	 */
	public static String banksMQ() {
			
		String query = "SELECT * FROM Banks";
		
		if (logger.isDebugEnabled()){
			logger.debug("Query generado "+query);
		}
		return query;		
	}
	
	/**
	 * Query para extraer todos los Transactions
	 * @return Todos los Transactions
	 * @author Luis Alejandro Aloisi Millan
	 */
	public static String transactionsMQ() {
			
		String query = "SELECT * FROM Transactions";
		
		if (logger.isDebugEnabled()){
			logger.debug("Query generado "+query);
		}
		return query;		
	}
	

	/**
	 * Query para extraer todos los VTerminals
	 * @return Todos los VTerminals
	 * @author Luis Alejandro Aloisi Millan
	 */
	public static String vTerminalsMQ() {
			
		String query = "SELECT * FROM VTerminals";
		
		if (logger.isDebugEnabled()){
			logger.debug("Query generado "+query);
		}
		return query;		
	}
	
	/**
	 * Query para extraer todos los Groups
	 * @return Todos los Groups
	 * @author Luis Alejandro Aloisi Millan
	 */
	public static String groupsMQ() {
			
		String query = "SELECT * FROM Groups";
		
		if (logger.isDebugEnabled()){
			logger.debug("Query generado "+query);
		}
		return query;		
	}
	
	/**
	 * Query para extraer todos los merchantPays
	 * @return Todos los merchantPays
	 * @author Luis Alejandro Aloisi Millan
	 */
	public static String merchantPaysMQ() {
			
		String query = "SELECT * FROM merchantPays";
		
		if (logger.isDebugEnabled()){
			logger.debug("Query generado "+query);
		}
		return query;		
	}
	
	/**
	 * Query para extraer todos los terminals
	 * @return Todos los terminals
	 * @author Luis Alejandro Aloisi Millan
	 */
	public static String terminalsMQ() {
			
		String query = "SELECT * FROM terminals";
		
		if (logger.isDebugEnabled()){
			logger.debug("Query generado "+query);
		}
		return query;		
	}
	
	/**
	 * Query para extraer todos los vterminalTerminals
	 * @return Todos los vterminalTerminals
	 * @author Luis Alejandro Aloisi Millan
	 */
	public static String vterminalTerminalsMQ() {
			
		String query = "SELECT * FROM vterminalterminals";
		
		if (logger.isDebugEnabled()){
			logger.debug("Query generado "+query);
		}
		return query;		
	}
	
	/**
	 * Query para extraer todos los Payments
	 * @return Todos los Payments
	 * @author Luis Alejandro Aloisi Millan
	 */
	public static String paymentsMQ() {
			
		String query = "SELECT * FROM Payments";
		
		if (logger.isDebugEnabled()){
			logger.debug("Query generado "+query);
		}
		return query;		
	}
	
	/**
	 * Query para extraer todos los Acquirers
	 * @return Todos los Acquirers
	 * @author Luis Alejandro Aloisi Millan
	 */
	public static String acquirersMQ() {
			
		String query = "SELECT * FROM Acquirers";
		
		if (logger.isDebugEnabled()){
			logger.debug("Query generado "+query);
		}
		return query;		
	}
	
	/**
	 * Query para extraer todos los Cards
	 * @return Todos los Cards
	 * @author Luis Alejandro Aloisi Millan
	 */
	public static String cardsMQ() {
			
		String query = "SELECT * FROM Cards";
		
		if (logger.isDebugEnabled()){
			logger.debug("Query generado "+query);
		}
		return query;		
	}
	
	/**
	 * Query para extraer todos los Accounts
	 * @return Todos los Accounts
	 * @author Luis Alejandro Aloisi Millan
	 */
	public static String accountsMQ() {
			
		String query = "SELECT * FROM accounts";
		
		if (logger.isDebugEnabled()){
			logger.debug("Query generado "+query);
		}
		return query;		
	}
	
	/**
	 * Query para extraer todos los RespCodes
	 * @return Todos los RespCode
	 * @author Luis Alejandro Aloisi Millan
	 */
	public static String respCodeMQ() {
			
		String query = "SELECT * FROM RespCode";
		
		if (logger.isDebugEnabled()){
			logger.debug("Query generado "+query);
		}
		return query;		
	}
	
	/**
	 * Query para extraer todos los paymentRespCode
	 * @return Todos los paymentRespCode
	 * @author Luis Alejandro Aloisi Millan
	 */
	public static String paymentRespCodeMQ() {
			
		String query = "SELECT * FROM PaymentRespCode";
		
		if (logger.isDebugEnabled()){
			logger.debug("Query generado "+query);
		}
		return query;		
	}

	/**
	 * Query para extraer todos los Nodos
	 * @return Todos los Nodos
	 * @author Luis Alejandro Aloisi Millan
	 */
	public static String nodosMQ() {

		String query = "SELECT * FROM Nodo";

		if (logger.isDebugEnabled()){
			logger.debug("Query generado "+query);
		}
		return query;
	}

	/**
	 * Query para extraer todos los NodosAcquirer
	 * @return Todos los NodosAcquirer
	 * @author Luis Alejandro Aloisi Millan
	 */
	public static String nodosAcquirerMQ() {

		String query = "SELECT * FROM NodoAcquirer";

		if (logger.isDebugEnabled()){
			logger.debug("Query generado "+query);
		}
		return query;
	}
	
	/**
	 * Query para Transaciones de Batch
	 * @return Todos los Batch
	 * @throws InvalidDataMSBDException
	 * @author Michael Amariscua
	 */
	public static String batch(Timestamp fechaInicio, Timestamp fechaFin) throws InvalidDataMSBDException{
		
		if (fechaInicio.after(fechaFin) || fechaInicio == null || fechaFin == null)
			throw new InvalidDataMSBDException("Las fechas ingresadas son nulas o la fecha de inicio es despues de la de fin");
		
		SimpleDateFormat formatoTrx_date = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat formatoTrx_time = new SimpleDateFormat("HHmmss");
		String fDiaInicio = formatoTrx_date.format(new Date(fechaInicio.getTime()));
		String fHoraInicio = formatoTrx_time.format(new Date(fechaInicio.getTime()));
		String fHoraFin = formatoTrx_time.format(new Date(fechaFin.getTime()));
		
		String query = "SELECT * FROM Batch "
				+ "WHERE RegisterDate = '"+fDiaInicio+"'"
				+ "AND   RegisterTime between '"+fHoraInicio+"' and '"+fHoraFin+"'";
		
		if (logger.isDebugEnabled()){
			logger.debug("Query generado "+query);
		}
		return query;		
	}

	/**
	 * Query para transacciones de Transaclog
	 * @return Todos los Transaclog
	 * @throws InvalidDataMSBDException
	 * @author Michael Amariscua
	 */
	public static String transacLog(Timestamp fechaInicio, Timestamp fechaFin) throws InvalidDataMSBDException{
		
		if (fechaInicio.after(fechaFin) || fechaInicio == null || fechaFin == null)
			throw new InvalidDataMSBDException("Las fechas ingresadas son nulas o la fecha de inicio es despues de la de fin");
		
		SimpleDateFormat formatoTrx_date = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat formatoTrx_time = new SimpleDateFormat("HHmmss");
		String fDiaInicio = formatoTrx_date.format(new Date(fechaInicio.getTime()));
		String fHoraInicio = formatoTrx_time.format(new Date(fechaInicio.getTime()));
		String fHoraFin = formatoTrx_time.format(new Date(fechaFin.getTime()));
		
		String query = "SELECT * FROM TransacLog "
				+ "WHERE FechaRequest = '"+fDiaInicio+"'"
				+ "AND   HoraRequest between '"+fHoraInicio+"' and '"+fHoraFin+"'";
		
		if (logger.isDebugEnabled()){
			logger.debug("Query generado "+query);
		}
		return query;		
	}

	/**
	 * Query para extraer todos los MPaysCards
	 * @return Todos los MPaysCards
	 * @author Luis Alejandro Aloisi Millan
	 */
	public static String mPaysCardsMQ() {

		String query = "SELECT * FROM MPaysCards";

		if (logger.isDebugEnabled()){
			logger.debug("Query generado "+query);
		}
		return query;
	}

	/**
	 * Query para extraer todos los MedioPago
	 * @return Todos los MedioPago
	 * @author Luis Alejandro Aloisi Millan
	 */
	public static String medioPagoMQ() {

		String query = "SELECT * FROM MedioPago";

		if (logger.isDebugEnabled()){
			logger.debug("Query generado "+query);
		}
		return query;
	}

	/**
	 * Query para extraer todos los CaptureMode
	 * @return Todos los CaptureMode
	 * @author Luis Alejandro Aloisi Millan
	 */
	public static String captureModeMQ() {

		String query = "SELECT * FROM CaptureMode";

		if (logger.isDebugEnabled()){
			logger.debug("Query generado "+query);
		}
		return query;
	}

	/**
	 * Query para extraer todos los TransPays
	 * @return Todos los TransPays
	 * @author Luis Alejandro Aloisi Millan
	 */
	public static String transPaysMQ(){

		String query = "SELECT * FROM TransPays";

		if (logger.isDebugEnabled()){
			logger.debug("Query generado "+query);
		}
		return query;
	}

	/**
	 * Query para extraer todos los SecurityKeys
	 * @return Todos los SecurityKeys
	 * @author Luis Alejandro Aloisi Millan
	 */
	public static String securityKeysMQ(){

		String query = "SELECT * FROM SecurityKeys";

		if (logger.isDebugEnabled()){
			logger.debug("Query generado "+query);
		}
		return query;
	}

	public static String binsMQ(){

		String query = "SELECT * FROM Bins";

		if (logger.isDebugEnabled()){
			logger.debug("Query generado "+query);
		}
		return query;
	}

	public static String binTablesMQ(){

		String query = "SELECT * FROM BinTables";

		if (logger.isDebugEnabled()){
			logger.debug("Query generado "+query);
		}
		return query;
	}

	public static String binTablesBinsMQ(){

		String query = "SELECT * FROM BinTablesBins";

		if (logger.isDebugEnabled()){
			logger.debug("Query generado "+query);
		}
		return query;
	}

	public static String retailerPaysMQ(){

		String query = "SELECT * FROM RetailerPays";

		if (logger.isDebugEnabled()){
			logger.debug("Query generado "+query);
		}
		return query;
	}

	public static String mPaysIssuerSwMQ(){

		String query = "SELECT * FROM MPaysIssuerSw";

		if (logger.isDebugEnabled()){
			logger.debug("Query generado "+query);
		}
		return query;
	}

	public static String universalBinsMQ(){

		String query = "SELECT * FROM UniversalBins";

		if (logger.isDebugEnabled()){
			logger.debug("Query generado "+query);
		}
		return query;
	}

	public static String rPaysIssuerSwMQ(){

		String query = "SELECT * FROM RPaysIssuerSw";

		if (logger.isDebugEnabled()){
			logger.debug("Query generado "+query);
		}
		return query;
	}

	public static String rPaysCardsMQ(){

		String query = "SELECT * FROM RPaysCards";

		if (logger.isDebugEnabled()){
			logger.debug("Query generado "+query);
		}
		return query;
	}

	public static String terminalCierreInsistenteTransaclog(Timestamp fechaInicio, Timestamp fechaFin, int numInsistencias) {
		SimpleDateFormat formatoTrx_date = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat formatoTrx_time = new SimpleDateFormat("HHmmss");
		String fDiaInicio = formatoTrx_date.format(new Date(fechaInicio.getTime()));
		String fDiaFin = formatoTrx_date.format(new Date(fechaFin.getTime()));
		String fHoraInicio = formatoTrx_time.format(new Date(fechaInicio.getTime()));
		String fHoraFin = formatoTrx_time.format(new Date(fechaFin.getTime()));

		String query =
				"SELECT tl.vtid, tl.paymentid " +
						"FROM transaclog tl " +
						"WHERE tl.transcode = '117' " +
						" AND ((tl.FechaRequest >= '" + fDiaInicio + "') " +
						" AND (tl.FechaRequest > '" + fDiaInicio + "' OR tl.HoraRequest >= '" + fHoraInicio + "')) " +
						" AND ((tl.FechaRequest <= '" + fDiaFin + "') " +
						" AND (tl.FechaRequest < '" + fDiaFin + "' OR tl.HoraRequest <= '" + fHoraFin + "')) " +
						" AND tl.b039 <> '00' " +
						" AND tl.paymentid IS NOT NULL" +
						" GROUP BY tl.vtid, tl.paymentid " +
						" HAVING COUNT(*) > " + numInsistencias;

		if (logger.isDebugEnabled()) {
			logger.debug("Query generado para transaclog: " + query);
		}
		return query;
	}

	public static String terminalCierreInsistenteMerchant(List<String> vtids, List<String> paymentids) {

		StringBuilder whereClause = new StringBuilder();
		for (int i = 0; i < vtids.size(); i++) {
			String vtid = vtids.get(i);
			String paymentid = paymentids.get(i);
			if (i > 0) {
				whereClause.append(" OR ");
			}
			whereClause.append("(vt.vtid = '").append(vtid).append("' AND m.paymentid = '").append(paymentid).append("')");
		}

		String query =
				"SELECT t.terminalid, t.tid, t.description " +
						"FROM terminals t " +
						"JOIN vterminalterminals vtt ON vtt.terminalid = t.terminalid " +
						"JOIN vterminals vt ON vt.vterminalid = vtt.vterminalid " +
						"JOIN merchantpays m ON t.merchantpayid = m.merchantpayid " +
						"WHERE " + whereClause +
						" GROUP BY t.terminalid, t.tid, t.description ";

		if (logger.isDebugEnabled()) {
			logger.debug("Query generado para Merchant: " + query);
		}
		return query;
	}

}
