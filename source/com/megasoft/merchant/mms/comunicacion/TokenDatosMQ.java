package com.megasoft.merchant.mms.comunicacion;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Esta clase contiene la informacion que se va a enviar a la cola de rabbitmq
 * @author Luis Alejandro Aloisi Millan
 *
 */
public class TokenDatosMQ implements Serializable{

	private static final long serialVersionUID = -4685671216966755588L;

	public String clientName;
	public int clientId;
	public int port;
	public String processingName;
	public int totalElements;
	public String checksum;
	public String startDate;  
	public String endDate;
	public String tableName;
	public String messageId;
	public int totalMessages;
	public double runtime;
	public double queryRuntime;
	public double arrayJsonRuntime;
	public double intervalRuntime;

	public boolean excludingColumns;

	public ArrayList<JSONObject> elements;

	public TokenDatosMQ() {
	}

	//NUEVO CONSTRUCTOR PARA TAREAS QUE TIENEN FECHA INICIO Y FECHA FIN
	public TokenDatosMQ(String clientName, int clientId, int port, String processingName,
			int totalElements, String checksum, String startDate, String endDate,String tableName, String messageId, int totalMessages,
			double runtime,double queryRuntime, double arrayJsonRuntime, double intervalRuntime,boolean excludingColumns, ArrayList<JSONObject> elements) {
		super();
		this.clientName = clientName;
		this.clientId = clientId;
		this.port = port;
		this.processingName = processingName;
		this.totalElements = totalElements;
		this.checksum = checksum;
		this.startDate = startDate;
		this.endDate = endDate;
		this.tableName=tableName;
		this.messageId = messageId;
		this.totalMessages = totalMessages;
		this.runtime = runtime;
		this.queryRuntime =queryRuntime;
		this.arrayJsonRuntime = arrayJsonRuntime;
		this.intervalRuntime =intervalRuntime;
		this.excludingColumns = excludingColumns;
		this.elements = elements;

	}

	//NUEVO CONSTRUCTOR PARA TAREAS QUE NO TIENEN FECHA INICIO Y FECHA FIN Y SI TIENEN RUNTIME y JSONObject
	public TokenDatosMQ(String clientName, int clientId, int port, String processingName,
						int totalElements, String checksum, String endDate,String tableName, String messageId, int totalMessages,
						double runtime,double queryRuntime, double arrayJsonRuntime, double intervalRuntime, boolean excludingColumns, ArrayList<JSONObject> elements) {
		super();
		this.clientName = clientName;
		this.clientId = clientId;
		this.port = port;
		this.processingName = processingName;
		this.totalElements = totalElements;
		this.checksum = checksum;
		this.endDate = endDate;
		this.tableName=tableName;
		this.messageId = messageId;
		this.totalMessages = totalMessages;
		this.runtime = runtime;
		this.queryRuntime =queryRuntime;
		this.arrayJsonRuntime = arrayJsonRuntime;
		this.intervalRuntime =intervalRuntime;
		this.excludingColumns = excludingColumns;
		this.elements = elements;

	}


	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public int getClientId() {
		return clientId;
	}

	public void setClientId(int clientId) {
		this.clientId = clientId;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setProcessingName(String processingName) {
		this.processingName = processingName;
	}

	public void setTotalElements(int totalElements) {
		this.totalElements = totalElements;
	}

	public String getChecksum() {
		return checksum;
	}

	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public void setTotalMessages(int totalMessages) {
		this.totalMessages = totalMessages;
	}

	public double getRuntime() {
		return runtime;
	}

	public void setRuntime(double runtime) {
		this.runtime = runtime;
	}

	public void setQueryRuntime(double queryRuntime) {
		this.queryRuntime = queryRuntime;
	}

	public void setArrayJsonRuntime(double arrayJsonRuntime) {
		this.arrayJsonRuntime = arrayJsonRuntime;
	}
	public void setIntervalRuntime(double intervalRuntime) {
		this.intervalRuntime = intervalRuntime;
	}
	public ArrayList<JSONObject> getElementsJsonArray() {
		return elements;
	}
	public void setElementsJsonArray(ArrayList<JSONObject> elementsJsonArray) {
		this.elements = elementsJsonArray;
	}

	public void setExcludingColumns(boolean excludingColumns) {
		this.excludingColumns = excludingColumns;
	}


}
