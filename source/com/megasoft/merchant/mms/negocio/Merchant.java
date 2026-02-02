package com.megasoft.merchant.mms.negocio;

/**
 * 
 * @author Christian De Sousa
 *
 */
public class Merchant extends MMS{

	private static final long serialVersionUID = 1L;
	
	private int id;
	private Client client;
	private String nName;
	private String description;
	private int aActive;
	private String ipAdress;
	private int retailerID;
	private int otherWayPayment;
	private String activity;
	private int merchantTmpId;
	
	public Merchant(int id) {
		this.id = id;
	}
	
	public Merchant(int id, String ipAdress)
	{
		super();
		this.id = id;
		this.ipAdress = ipAdress;
	}
	
	public Merchant(int id, int idClient, String nName, String description, int aActive){
		super();
		this.id = id;
		this.client = new Client(idClient);
		this.nName = nName;
		this.description = description;
		this.aActive = aActive;
	}
	
	public Merchant(int id, String nName, String description, int aActive){
		super();
		this.id = id;
		this.nName = nName;
		this.description = description;
		this.aActive = aActive;
	}
	
	/**
	 * Agregado por Roderick Rangel
	 * Fecha: 10/03/2009
	 * Cambios, para agregar Retailers a la estructra del MMS
	 * Actualizacion: Se agrego otherWayPayment
	 * Fecha: 23/07/2009
	 * @param id
	 * @param nName
	 * @param description
	 * @param aActive
	 * @param retailerCentralID
	 * @param otherWayPayment
	 */
	public Merchant(int id, String nName, String description, int aActive, int retailerID, int otherWayPayment){
		super();
		this.id = id;
		this.nName = nName;
		this.description = description;
		this.aActive = aActive;
		this.retailerID = retailerID;
		this.otherWayPayment = otherWayPayment;
	}
	
	
	/**
	 * Agregado por Henry Santiago
	 * Fecha: 16/01/2013	 
	 * Actualizacion: Se agrega el campo activity
	 * @param id
	 * @param nName
	 * @param description
	 * @param aActive
	 * @param retailerCentralID
	 * @param otherWayPayment
	 */
	public Merchant(int id, String nName, String description, int aActive, int retailerID, int otherWayPayment,String activity,int merchantTmpId){
		super();
		this.id = id;
		this.nName = nName;
		this.description = description;
		this.aActive = aActive;
		this.retailerID = retailerID;
		this.otherWayPayment = otherWayPayment;
		this.activity = activity;
		this.merchantTmpId = merchantTmpId;
	}
	
	public Merchant(String nName, int id ){
		super();
		this.id = id;
		this.nName = nName;
	}

	public int getClientId() {
		return client.getId();
	}
	public void setClientId(int clientId) {
		this.client = new Client(clientId);
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
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
	public int getAActive() {
		return aActive;
	}
	public void setAActive(int active) {
		aActive = active;
	}
	public String getIPAdress() {
		return ipAdress;
	}
	public void setIPAdress(String ipAdress) {
		this.ipAdress = ipAdress;
	}
	
	public String toString(){
		if (client != null){
			return "Id: "+id+" ClientId: "+client.getId()+" nName:'"+nName+"' description:'"+description+"' nName:'"+nName+"' aActive:"+aActive+"' retailerID:'"+retailerID+"'";
		}
		return "Id: "+id+" nName:'"+nName+"' description:'"+description+"' nName:'"+nName+"' aActive:'"+aActive+"' retailerID:'"+retailerID+"'";
	}

	public int getRetailerID() {
		return retailerID;
	}

	public void setRetailerID(int retailerID) {
		this.retailerID = retailerID;
	}

	public int getOtherWayPayment() {
		return otherWayPayment;
	}

	public void setOtherWayPayment(int otherWayPayment) {
		this.otherWayPayment = otherWayPayment;
	}

	/**
	 * @return the activity
	 */
	public String getActivity() {
		return activity;
	}

	/**
	 * @param activity the activity to set
	 */
	public void setActivity(String activity) {
		this.activity = activity;
	}

	/**
	 * @return the merchantTmpId
	 */
	public int getMerchantTmpId() {
		return merchantTmpId;
	}

	/**
	 * @param merchantTmpId the merchantTmpId to set
	 */
	public void setMerchantTmpId(int merchantTmpId) {
		this.merchantTmpId = merchantTmpId;
	}
}
