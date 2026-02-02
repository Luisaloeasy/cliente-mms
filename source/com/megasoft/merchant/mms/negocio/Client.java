package com.megasoft.merchant.mms.negocio;

import java.sql.Timestamp;

/**
 * 
 * @author Christian De Sousa
 *
 */
public class Client extends MMS{

	private static final long serialVersionUID = 1L;
	
	private int id;
	private String nName;
	private String email;
	private Timestamp lastActReportTime;
	private int activity;
	private Timestamp lastConnReportTime;
	private int connection;
	private int aActive;
	private Timestamp clientHour;

	public Client(int id, String nName, String email, Timestamp lastActReportTime, 
			int activity, Timestamp lastConnReportTime, int connection, 
			Timestamp clientHour, int aActive){
		super();
		this.id = id;
		this.nName = nName;
		this.email = email;
		this.lastActReportTime = normalizarTiempo(lastActReportTime);
		this.aActive = aActive;
		this.lastConnReportTime = normalizarTiempo(lastConnReportTime);
		this.connection = connection;
		this.activity = activity;
		this.clientHour = clientHour;
	}
	
	public Client(int id, int activity, Timestamp timestamp){
		super();
		this.id = id;
		this.activity = activity;
		this.lastActReportTime = normalizarTiempo(timestamp);
	}
	
	public Client(int id, Timestamp timestamp) {
		super();
		this.id = id;
		this.lastActReportTime = normalizarTiempo(timestamp);
	}
	
	public Client(int id){
		super();
		this.id = id;
	}
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNName() {
		return nName;
	}
	public void setNName(String name) {
		nName = name;
	}
	public int getActivity() {
		return activity;
	}
	public void setActivity(int activity) {
		this.activity = activity;
	}
	public Timestamp getLastActReportTime() {
		return lastActReportTime;
	}
	public void setLastActReportTime(Timestamp lastActReportTime) {
		this.lastActReportTime = normalizarTiempo(lastActReportTime);
	}
	public Timestamp getLastConnReportTime() {
		return lastConnReportTime;
	}
	public void setLastConnReportTime(Timestamp lastConnReportTime) {
		this.lastConnReportTime = normalizarTiempo(lastConnReportTime);
	}
	public int getAActive() {
		return aActive;
	}
	public void setAActive(int active) {
		aActive = active;
	}
	public int getConnection() {
		return connection;
	}
	public void setConnection(int connection) {
		this.connection = connection;
	}
	
	public Timestamp getClientHour() {
		return clientHour;
	}

	public void setClientHour(Timestamp clientHour) {
		this.clientHour = normalizarTiempo(clientHour);
	}
	
	public String toString() {
		return " Id: "+id+
				" NName: '"+nName+"' " +
				" Email: '"+email+"' " +
				" Activity = "+activity+
				" LastActReportTime "+lastActReportTime+
				" Connection = "+connection+
				" LastConnReportTime "+lastConnReportTime+
				" AActive "+aActive;
	}
}
